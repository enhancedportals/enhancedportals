package enhanced.portals.tile;

import java.util.ArrayList;
import java.util.Random;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhanced.base.utilities.DimensionCoordinates;
import enhanced.base.utilities.Localisation;
import enhanced.base.xmod.ComputerCraft;
import enhanced.base.xmod.OpenComputers;
import enhanced.core.Reference.ECItems;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.packet.PacketRerender;
import enhanced.portals.portal.EntityManager;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalException;
import enhanced.portals.portal.PortalTextureManager;
import enhanced.portals.portal.PortalUtils;
import enhanced.portals.utility.ComputerUtils;
import enhanced.portals.utility.GeneralUtils;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPConfiguration;
import enhanced.portals.utility.Reference.EPGuis;
import enhanced.portals.utility.Reference.EPItems;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.PortalModules;

@InterfaceList(value = { @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = ComputerCraft.MOD_ID), @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = OpenComputers.MOD_ID) })
public class TileController extends TileFrame implements IPeripheral, SimpleComponent {
    public ArrayList<ChunkCoordinates> portalFrames = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> portalBlocks = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> redstoneInterfaces = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> networkInterfaces = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> diallingDevices = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> transferFluids = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> transferItems = new ArrayList<ChunkCoordinates>();
    public ArrayList<ChunkCoordinates> transferEnergy = new ArrayList<ChunkCoordinates>();
    ChunkCoordinates moduleManipulator;

    public PortalTextureManager activeTextureData = new PortalTextureManager(), inactiveTextureData;
    public int connectedPortals = -1, instability = 0, portalType = 0;
    public boolean isPublic, isFinalized;

    DimensionCoordinates pairedControllerLOAD;
    TileController pairedController;
    Ticket chunkLoadTicket;
    String lastError;
    boolean processing;

    @SideOnly(Side.CLIENT)
    public GlyphIdentifier uID, nID;
    @SideOnly(Side.CLIENT)
    boolean isActive;

    @SideOnly(Side.CLIENT)
    public void setUID(GlyphIdentifier g) {
        if (g != null && g.size() == 0)
            uID = null;
        else
            uID = g;
    }
    
    @SideOnly(Side.CLIENT)
    public void setNID(GlyphIdentifier g) {
        if (g != null && g.size() == 0)
            nID = null;
        else
            nID = g;
    }
    
    public boolean isPortalActive() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? isActive : pairedController != null;
    }

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking()) {
            if (lastError != null) {
                player.addChatComponentMessage(new ChatComponentText(lastError));
                lastError = null;
            }
            
            return false;
        }

        try {
            if (stack != null)
                if (!isFinalized) {
                    if (GeneralUtils.isWrench(stack) && !worldObj.isRemote) {
                        configurePortal();
                        player.addChatComponentMessage(new ChatComponentText(Localisation.getChatSuccess(EPMod.ID, "reconfigure")));
                    }

                    return true;
                } else if (isFinalized)
                    if (GeneralUtils.isWrench(stack)) {
                        GuiHandler.openGui(player, this, EPGuis.PORTAL_CONTROLLER_A);
                        return true;
                    } else if (stack.getItem() == EPItems.nanobrush) {
                        GuiHandler.openGui(player, this, EPGuis.TEXTURE_A);
                        return true;
                    }
        } catch (PortalException e) {
            player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
        }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {
        tag.setByte("PortalState", (byte)(isFinalized ? 1 : 0));
        tag.setBoolean("PortalActive", isPortalActive());
        tag.setInteger("Instability", instability);

        activeTextureData.writeToNBT(tag, "Texture");

        if (moduleManipulator != null) {
            tag.setInteger("ModX", moduleManipulator.posX);
            tag.setInteger("ModY", moduleManipulator.posY);
            tag.setInteger("ModZ", moduleManipulator.posZ);
        }
    }
    
    @Override
    public void onDataPacket(NBTTagCompound tag) {
        isFinalized = tag.getByte("PortalState") == 1 ? true : false;
        isActive = tag.getBoolean("PortalActive");
        activeTextureData.readFromNBT(tag, "Texture");
        instability = tag.getInteger("Instability");

        if (tag.hasKey("ModX"))
            moduleManipulator = new ChunkCoordinates(tag.getInteger("ModX"), tag.getInteger("ModY"), tag.getInteger("ModZ"));

        ArrayList<ChunkCoordinates> f = EnhancedPortals.proxy.getControllerList(getChunkCoordinates());

        if (f != null) {
            for (ChunkCoordinates frames : f)
                worldObj.markBlockForUpdate(frames.posX, frames.posY, frames.posZ);

            EnhancedPortals.proxy.clearControllerList(getChunkCoordinates());
        }
    }

    @Override
    public void breakBlock(Block b, int oldMetadata) {
        deconstruct();
        EnhancedPortals.proxy.networkManager.removePortalNID(this);
        EnhancedPortals.proxy.networkManager.removePortalUID(this);
    }

    void configurePortal() throws PortalException {
        ArrayList<ChunkCoordinates> portalStructure = PortalUtils.getAllPortalComponents(this);

        for (ChunkCoordinates c : portalStructure) {
            TileEntity tile = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (tile instanceof TileController) {

            } else if (tile instanceof TileFrameBasic)
                portalFrames.add(c);
            else if (tile instanceof TileRedstoneInterface)
                redstoneInterfaces.add(c);
            else if (tile instanceof TileNetworkInterface)
                networkInterfaces.add(c);
            else if (tile instanceof TileDialingDevice)
                diallingDevices.add(c);
            else if (tile instanceof TilePortalManipulator)
                moduleManipulator = c;
            else if (tile instanceof TileTransferFluid)
                transferFluids.add(c);
            else if (tile instanceof TileTransferItem)
                transferItems.add(c);
            else if (tile instanceof TileTransferEnergy)
                transferEnergy.add(c);
            else {
                portalBlocks.add(c);
                continue;
            }

            ((TilePortalPart) tile).setPortalController(getChunkCoordinates());
        }

        isFinalized = true;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public void updateEntity() {
        if (pairedControllerLOAD != null && pairedControllerLOAD.getWorld() != null) {
            TileEntity t = pairedControllerLOAD.getTileEntity();
            
            if (t instanceof TileController)
                pairedController = (TileController) t;
            else
                deconstructConnectionRemote();
            
            pairedControllerLOAD = null;
        }
        
        super.updateEntity();
    }

    /**
     * Deconstructs the portal structure.
     */
    public void deconstruct() {
        if (processing)
            return;

        if (isPortalActive())
            deconstructConnection();

        for (ChunkCoordinates c : portalFrames) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (ChunkCoordinates c : redstoneInterfaces) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (ChunkCoordinates c : networkInterfaces) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (ChunkCoordinates c : diallingDevices) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (ChunkCoordinates c : transferFluids) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (ChunkCoordinates c : transferItems) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (ChunkCoordinates c : transferEnergy) {
            TileEntity t = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        if (moduleManipulator != null) {
            TileEntity t = worldObj.getTileEntity(moduleManipulator.posX, moduleManipulator.posY, moduleManipulator.posZ);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        portalBlocks.clear();
        portalFrames.clear();
        redstoneInterfaces.clear();
        networkInterfaces.clear();
        diallingDevices.clear();
        transferFluids.clear();
        transferItems.clear();
        transferEnergy.clear();
        moduleManipulator = null;
        isFinalized = false;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public TilePortalManipulator getModuleManipulator() {
        if (moduleManipulator != null) {
            TileEntity tile = worldObj.getTileEntity(moduleManipulator.posX, moduleManipulator.posY, moduleManipulator.posZ);

            if (tile instanceof TilePortalManipulator)
                return (TilePortalManipulator) tile;
        }

        return null;
    }

    public TileDialingDevice getDialDeviceRandom() {
        ChunkCoordinates dial = null;

        if (diallingDevices.isEmpty())
            return null;
        else if (diallingDevices.size() == 1)
            dial = diallingDevices.get(0);
        else
            dial = diallingDevices.get(new Random().nextInt(diallingDevices.size()));

        TileEntity tile = worldObj.getTileEntity(dial.posX, dial.posY, dial.posZ);

        if (tile != null && tile instanceof TileDialingDevice)
            return (TileDialingDevice) tile;

        return null;
    }

    public void onEntityEnterPortal(Entity entity, TilePortal tilePortal) {
        if (pairedController != null) {
            try {
                EntityManager.transferEntity(entity, this, pairedController);
                onEntityTeleported(entity);
                pairedController.onEntityTeleported(entity);
            } catch (PortalException e) {
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText(e.getMessage()));
            }
        }
        
        /*if (cachedDestinationLoc == null)
            return;

        // Set tile to the joined portal's controller.
        TileEntity tile = cachedDestinationLoc.getTileEntity();
        // Trigger redstone interfaces.
        onEntityTouchPortal(entity);

        if (tile != null && tile instanceof TileController) {
            TileController control = (TileController) tile;

            try {
                EntityManager.transferEntity(entity, this, control);
                onEntityTeleported(entity);
                control.onEntityTeleported(entity);
                control.onEntityTouchPortal(entity);
            } catch (PortalException e) {
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText(e.getMessage()));
            }
        }*/
    }

    public void onEntityTeleported(Entity entity) {
        TilePortalManipulator module = getModuleManipulator();

        if (module != null)
            if (module.hasModule(PortalModules.FEATHERFALL))
                if (entity instanceof EntityLivingBase)
                    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ECItems.potionFeatherfall.id, 200, 0));
    }

    public void onEntityTouchPortal(Entity entity) {
        for (ChunkCoordinates c : redstoneInterfaces)
            ((TileRedstoneInterface) worldObj.getTileEntity(c.posX, c.posY, c.posZ)).onEntityTeleport(entity);
    }

    public void onPartFrameBroken() {
        deconstruct();
    }

    /**
     * Creates the portal block. Throws an {@link PortalException} if an error occurs.
     */
    public void portalCreate() throws PortalException {
        for (ChunkCoordinates c : portalBlocks)
            if (!worldObj.isAirBlock(c.posX, c.posY, c.posZ))
                if (EPConfiguration.portalDestroysBlocks)
                    worldObj.setBlockToAir(c.posX, c.posY, c.posZ);
                else
                    throw new PortalException("failedToCreatePortal");

        for (ChunkCoordinates c : portalBlocks) {
            worldObj.setBlock(c.posX, c.posY, c.posZ, EPBlocks.portal, portalType, 2);

            TilePortal portal = (TilePortal) worldObj.getTileEntity(c.posX, c.posY, c.posZ);
            portal.portalController = getChunkCoordinates();
        }

        for (ChunkCoordinates c : redstoneInterfaces) {
            TileRedstoneInterface ri = (TileRedstoneInterface) worldObj.getTileEntity(c.posX, c.posY, c.posZ);
            ri.onPortalCreated();
        }
    }

    public void portalRemove() {
        if (processing) return;
        processing = true;

        for (ChunkCoordinates c : portalBlocks)
            worldObj.setBlockToAir(c.posX, c.posY, c.posZ);

        for (ChunkCoordinates c : redstoneInterfaces) {
            TileRedstoneInterface ri = (TileRedstoneInterface) worldObj.getTileEntity(c.posX, c.posY, c.posZ);
            ri.onPortalRemoved();
        }

        processing = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        isFinalized = tagCompound.getInteger("PortalState") == 1;
        instability = tagCompound.getInteger("Instability");
        portalType = tagCompound.getInteger("PortalType");
        isPublic = tagCompound.getBoolean("isPublic");

        portalFrames = GeneralUtils.loadChunkCoordList(tagCompound, "Frames");
        portalBlocks = GeneralUtils.loadChunkCoordList(tagCompound, "Portals");
        redstoneInterfaces = GeneralUtils.loadChunkCoordList(tagCompound, "RedstoneInterfaces");
        networkInterfaces = GeneralUtils.loadChunkCoordList(tagCompound, "NetworkInterface");
        diallingDevices = GeneralUtils.loadChunkCoordList(tagCompound, "DialDevice");
        transferEnergy = GeneralUtils.loadChunkCoordList(tagCompound, "TransferEnergy");
        transferFluids = GeneralUtils.loadChunkCoordList(tagCompound, "TransferFluid");
        transferItems = GeneralUtils.loadChunkCoordList(tagCompound, "TransferItems");
        moduleManipulator = GeneralUtils.loadChunkCoord(tagCompound, "ModuleManipulator");

        if (tagCompound.hasKey("PairedController"))
            pairedControllerLOAD = GeneralUtils.loadWorldCoord(tagCompound, "PairedController");
        
        activeTextureData.readFromNBT(tagCompound, "ActiveTextureData");

        if (tagCompound.hasKey("InactiveTextureData")) {
            inactiveTextureData = new PortalTextureManager();
            inactiveTextureData.readFromNBT(tagCompound, "InactiveTextureData");
        }
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("PortalState", isFinalized ? 1 : 0);
        tagCompound.setInteger("Instability", instability);
        tagCompound.setInteger("PortalType", portalType);
        tagCompound.setBoolean("isPublic", isPublic);

        GeneralUtils.saveChunkCoordList(tagCompound, portalFrames, "Frames");
        GeneralUtils.saveChunkCoordList(tagCompound, portalBlocks, "Portals");
        GeneralUtils.saveChunkCoordList(tagCompound, redstoneInterfaces, "RedstoneInterfaces");
        GeneralUtils.saveChunkCoordList(tagCompound, networkInterfaces, "NetworkInterface");
        GeneralUtils.saveChunkCoordList(tagCompound, diallingDevices, "DialDevice");
        GeneralUtils.saveChunkCoordList(tagCompound, transferEnergy, "TransferEnergy");
        GeneralUtils.saveChunkCoordList(tagCompound, transferFluids, "TransferFluid");
        GeneralUtils.saveChunkCoordList(tagCompound, transferItems, "TransferItems");
        GeneralUtils.saveChunkCoord(tagCompound, moduleManipulator, "ModuleManipulator");
        
        if (pairedController != null)
            GeneralUtils.saveWorldCoord(tagCompound, pairedController.getDimensionCoordinates(), "PairedController");

        activeTextureData.writeToNBT(tagCompound, "ActiveTextureData");

        if (inactiveTextureData != null)
            inactiveTextureData.writeToNBT(tagCompound, "InactiveTextureData");
    }

    public void revertTextureData() {
        if (inactiveTextureData == null)
            return;

        activeTextureData = new PortalTextureManager(inactiveTextureData);
        inactiveTextureData = null;
    }

    /***
     * Sends an update packet for the TilePortalController. Also sends one packet per chunk to notify the client it needs to re-render its portal/frame blocks.
     *
     * @param updateChunks
     *            Should we send packets to re-render the portal/frame blocks?
     */
    void sendUpdatePacket(boolean updateChunks) {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        if (updateChunks) {
            ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();
            chunks.add(new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));

            for (ChunkCoordinates c : portalFrames)
                if (!chunks.contains(new ChunkCoordIntPair(c.posX >> 4, c.posZ >> 4))) {
                    EnhancedPortals.instance.packetPipeline.sendToAllAround(new PacketRerender(c.posX, c.posY, c.posZ), this);
                    chunks.add(new ChunkCoordIntPair(c.posX >> 4, c.posZ >> 4));
                }
        }
    }

    // ** ** ** ** **

    public void forceChunk() {
        forceChunk(ForgeChunkManager.requestTicket(EnhancedPortals.instance, getWorldObj(), ForgeChunkManager.Type.NORMAL));
    }
    
    public void forceChunk(Ticket ticket) {
        if (ticket != null) {
            ticket.getModData().setInteger("controllerX", xCoord);
            ticket.getModData().setInteger("controllerY", yCoord);
            ticket.getModData().setInteger("controllerZ", zCoord);
            ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));
        }
        
        chunkLoadTicket = ticket;
    }
    
    public void releaseChunk() {
        if (chunkLoadTicket != null) {
            ForgeChunkManager.unforceChunk(chunkLoadTicket, new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));
            ForgeChunkManager.releaseTicket(chunkLoadTicket);
        }
    }
    
    public boolean hasEnoughPower() {
        return true; // TODO
    }

    public void constructConnection() {
        if (isPortalActive()) {
            lastError = Localisation.getChatError(EPMod.ID, "portalActive");
            return;
        } else if (!hasEnoughPower()) {
            lastError = Localisation.getChatError(EPMod.ID, "notEnoughPower");
            return;
        }
        
        GlyphIdentifier g = EnhancedPortals.proxy.networkManager.getNetworkedPortalNext(this);
        TileController paired = EnhancedPortals.proxy.networkManager.getPortalController(g);

        if (paired == this) {
            lastError = Localisation.getChatError(EPMod.ID, "cantDialSelf");
            return;
        }

        try {
            forceChunk();
            paired.constructConnection(this);
            portalCreate();
        } catch (PortalException e) {
            lastError = e.getMessage();
            releaseChunk();
            return;
        }

        pairedController = paired;
        markDirty();
        sendUpdatePacket(false);
    }

    public void constructConnection(GlyphIdentifier identifier, PortalTextureManager texture, EntityPlayer player) {
        if (isPortalActive()) {
            player.addChatComponentMessage(new ChatComponentText(Localisation.getChatError(EPMod.ID, "portalActive")));
            return;
        } else if (!hasEnoughPower()) {
            player.addChatComponentMessage(new ChatComponentText(Localisation.getChatError(EPMod.ID, "notEnoughPower")));
            return;
        }

        TileController paired = EnhancedPortals.proxy.networkManager.getPortalController(identifier);

        if (paired == this) {
            lastError = Localisation.getChatError(EPMod.ID, "cantDialSelf");
            return;
        }
        
        try {
            forceChunk();
            portalCreate();
            paired.constructConnection(this);
        } catch (PortalException e) {
            player.addChatComponentMessage(new ChatComponentText(Localisation.getChatError(EPMod.ID, e.getMessage())));
            releaseChunk();
            return;
        }

        pairedController = paired;
        markDirty();
        sendUpdatePacket(false);
    }

    public void constructConnection(TileController requester) throws PortalException {
        if (isPortalActive())
            throw new PortalException("portalActiveRemote");
        else if (!hasEnoughPower())
            throw new PortalException("noPowerRemote");

        forceChunk();
        portalCreate();
        pairedController = requester;
        markDirty();
        sendUpdatePacket(false);
    }

    public void deconstructConnection() {
        releaseChunk();
        pairedController.deconstructConnectionRemote();
        pairedController = null;
        portalRemove();
        markDirty();
        sendUpdatePacket(false);
    }

    public void deconstructConnectionRemote() {
        releaseChunk();
        pairedController = null; 
        portalRemove();
        markDirty();
        sendUpdatePacket(false);
    }

    public TileController getDestination() {
        return pairedController;
    }
    
    //
    
    public void setCustomFrameTexture(int tex) {
        activeTextureData.setCustomFrameTexture(tex);
        sendUpdatePacket(true);
    }

    public void setCustomPortalTexture(int tex) {
        activeTextureData.setCustomPortalTexture(tex);
        sendUpdatePacket(true);
    }
    
    public void setFrameColour(int colour) {
        activeTextureData.setFrameColour(colour);
        markDirty();
        sendUpdatePacket(true);
    }

    public void setFrameItem(ItemStack s) {
        activeTextureData.setFrameItem(s);
        markDirty();
        sendUpdatePacket(true);
    }
    
    public void setInstability(int instabil) {
        instability = instabil;
        markDirty();
        sendUpdatePacket(true);
    }
    
    public void setParticleColour(int colour) {
        activeTextureData.setParticleColour(colour);
        markDirty();
        sendUpdatePacket(false); // Particles are generated by querying this
    }

    public void setParticleType(int type) {
        activeTextureData.setParticleType(type);
        markDirty();
        sendUpdatePacket(false); // Particles are generated by querying this
    }
    
    public void setPortalColour(int colour) {
        activeTextureData.setPortalColour(colour);
        markDirty();
        sendUpdatePacket(true);
    }

    public void setPortalItem(ItemStack s) {
        activeTextureData.setPortalItem(s);
        markDirty();
        sendUpdatePacket(true);
    }
    
    public void setModuleManipulator(ChunkCoordinates chunkCoordinates) {
        moduleManipulator = chunkCoordinates;
        markDirty();
    }

    /* ============= *
     * = CROSS MOD = *
     * ============= */

    @Callback(doc = "function(color:number):boolean -- Sets the portal frame colour to the specified hexadecimal string.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] setFrameColour(Context context, Arguments args) throws Exception {
        return comp_SetFrameColour(ComputerUtils.argsToArray(args));
    }

    @Callback(doc = "function(color:number):boolean -- Sets the particle colour to the specified hexadecimal string.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] setParticleColour(Context context, Arguments args) throws Exception {
        return comp_SetParticleColour(ComputerUtils.argsToArray(args));
    }

    @Callback(doc = "function(color:number):boolean -- Sets the portal colour to the specified hexadecimal string.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] setPortalColour(Context context, Arguments args) throws Exception {
        return comp_SetPortalColour(ComputerUtils.argsToArray(args));
    }

    @Callback(doc = "function(uuid:string):string -- Sets the UID to the specified string. If no string is given it will reset the UID. Must be given as numbers separated by spaces.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] setUniqueIdentifier(Context context, Arguments args) throws Exception {
        return comp_SetUniqueIdentifier(ComputerUtils.argsToArray(args));
    }

    @Callback(direct = true, doc = "function():number -- Returns the hexadecimal colour of the particles.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] getParticleColour(Context context, Arguments args) throws Exception {
        return new Object[] { activeTextureData.getParticleColour() };
    }

    @Callback(direct = true, doc = "function():number -- Returns the hexadecimal colour of the portal.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] getPortalColour(Context context, Arguments args) throws Exception {
        return new Object[] { activeTextureData.getPortalColour() };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String getType() {
        return "ep_controller";
    }

    @Callback(direct = true, doc = "function():string -- Returns a string containing the numeric glyph IDs of each glyph in the unique identifier.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] getUniqueIdentifier(Context context, Arguments args) throws Exception {
        return comp_GetUniqueIdentifier();
    }

    @Callback(direct = true, doc = "function():boolean -- Returns true if the portal has an active connection.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] isPortalActive(Context context, Arguments args) {
        return new Object[] { isPortalActive() };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
        if (method == 0)
            return new Object[] { isPortalActive() };
        else if (method == 1)
            return comp_GetUniqueIdentifier();
        else if (method == 2)
            return comp_SetUniqueIdentifier(arguments);
        else if (method == 3)
            return new Object[] { activeTextureData.getFrameColour() };
        else if (method == 4)
            return comp_SetFrameColour(arguments);
        else if (method == 5)
            return new Object[] { activeTextureData.getPortalColour() };
        else if (method == 6)
            return comp_SetPortalColour(arguments);
        else if (method == 7)
            return new Object[] { activeTextureData.getParticleColour() };
        else if (method == 8)
            return comp_SetParticleColour(arguments);

        return null;
    }

    Object[] comp_GetUniqueIdentifier() {
        return new Object[] { EnhancedPortals.proxy.networkManager.getPortalUID(this) };
    }

    Object[] comp_SetFrameColour(Object[] arguments) throws Exception {
        if (arguments.length > 1 || arguments.length == 1 && arguments[0].toString().length() == 0)
            throw new Exception("Invalid arguments");

        try {
            int hex = Integer.parseInt(arguments.length == 1 ? arguments[0].toString() : "FFFFFF", 16);
            activeTextureData.setFrameColour(hex);
        } catch (NumberFormatException ex) {
            throw new Exception("Couldn't parse input as hexidecimal");
        }

        return new Object[] { true };
    }

    Object[] comp_SetParticleColour(Object[] arguments) throws Exception {
        if (arguments.length > 1 || arguments.length == 1 && arguments[0].toString().length() == 0)
            throw new Exception("Invalid arguments");

        try {
            activeTextureData.setParticleColour(new PortalTextureManager().getParticleColour());
        } catch (NumberFormatException ex) {
            throw new Exception("Couldn't parse input as hexidecimal");
        }

        return new Object[] { true };
    }

    Object[] comp_SetPortalColour(Object[] arguments) throws Exception {
        if (arguments.length > 1 || arguments.length == 1 && arguments[0].toString().length() == 0)
            throw new Exception("Invalid arguments");

        try {
            int hex = Integer.parseInt(arguments.length == 1 ? arguments[0].toString() : "FFFFFF", 16);
            activeTextureData.setPortalColour(hex);
        } catch (NumberFormatException ex) {
            throw new Exception("Couldn't parse input as hexidecimal");
        }

        return new Object[] { true };
    }

    Object[] comp_SetUniqueIdentifier(Object[] arguments) throws Exception {
        if (arguments.length == 0) {
            EnhancedPortals.proxy.networkManager.removePortalUID(this);
            return comp_GetUniqueIdentifier();
        } else if (arguments.length == 1) {
            String s = arguments[0].toString();
            s = s.replace(" ", GlyphIdentifier.GLYPH_SEPERATOR);

            String error = ComputerUtils.verifyGlyphArguments(s);
            if (error != null)
                throw new Exception(error);

            if (!EnhancedPortals.proxy.networkManager.setPortalUID(this, new GlyphIdentifier(s)))
                throw new Exception("UUID already in use");
        } else
            throw new Exception("Invalid arguments");

        return comp_GetUniqueIdentifier();
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public void detach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    @Override
    @Method(modid = OpenComputers.MOD_ID)
    public String getComponentName() {
        return "ep_controller";
    }

    @Callback(direct = true, doc = "function():number -- Returns the hexadecimal colour of the portal frame.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] getFrameColour(Context context, Arguments args) throws Exception {
        return new Object[] { activeTextureData.getFrameColour() };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String[] getMethodNames() {
        return new String[] { "isPortalActive", "getUniqueIdentifier", "setUniqueIdentifier", "getFrameColour", "setFrameColour", "getPortalColour", "setPortalColour", "getParticleColour", "setParticleColour" };
    }
}
