package enhanced.portals.portal.frame;

import java.util.ArrayList;
import java.util.Random;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhanced.base.utilities.BlockPos;
import enhanced.base.utilities.Localisation;
import enhanced.base.utilities.WorldPos;
import enhanced.base.utilities.WorldUtilities;
import enhanced.base.xmod.ComputerCraft;
import enhanced.base.xmod.OpenComputers;
import enhanced.core.Reference.ECItems;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.Reference.EPConfiguration;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.EPRenderers;
import enhanced.portals.Reference.Locale;
import enhanced.portals.Reference.PortalModules;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.packet.PacketRerender;
import enhanced.portals.portal.ComputerUtils;
import enhanced.portals.portal.EntityManager;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalException;
import enhanced.portals.portal.PortalTextureManager;
import enhanced.portals.portal.PortalUtils;
import enhanced.portals.portal.TilePortalPart;
import enhanced.portals.portal.portal.TilePortal;

@InterfaceList(value = { @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = ComputerCraft.MOD_ID), @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = OpenComputers.MOD_ID) })
public class TileController extends TileFrame implements IPeripheral, SimpleComponent, IEnergyHandler {
    public ArrayList<BlockPos> portalFrames = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> portalBlocks = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> redstoneInterfaces = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> networkInterfaces = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> diallingDevices = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> transferFluids = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> transferItems = new ArrayList<BlockPos>();
    public ArrayList<BlockPos> transferEnergy = new ArrayList<BlockPos>();
    BlockPos moduleManipulator;

    public PortalTextureManager activeTextureData = new PortalTextureManager(), inactiveTextureData;
    public int connectedPortals = -1, instability = 0, portalType = 0;
    public boolean isFinalized;

    WorldPos pairedControllerLOAD;
    TileController pairedController;
    Ticket chunkLoadTicket;
    String lastError;
    boolean processing;
    EnergyStorage storage = new EnergyStorage(16000);

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
        if (stack != null && stack.getItem() instanceof IToolWrench) {
            if (!isFinalized) {
                if (!worldObj.isRemote) {
                    try {
                        configurePortal();
                    } catch (PortalException e) {
                        player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                        return false;
                    }

                    player.addChatComponentMessage(Localisation.getChatSuccess(EPMod.ID, Locale.CHAT_SUCCESS_PORTAL_CONFIGURE));
                    return true;
                }
            } else {
                GuiHandler.openGui(player, this, EPGuis.PORTAL_CONTROLLER_A);
                return true;
            }
        } else if (lastError != null) {
            player.addChatComponentMessage(new ChatComponentText(lastError));
            lastError = null;
            return true;
        }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {
        tag.setByte("PortalState", (byte)(isFinalized ? 1 : 0));
        tag.setBoolean("PortalActive", isPortalActive());
        tag.setInteger("Instability", instability);

        activeTextureData.writeToNBT(tag, "Texture");

        if (moduleManipulator != null) 
            moduleManipulator.writeToNBT(tag, "portalManip");
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {
        isFinalized = tag.getByte("PortalState") == 1 ? true : false;
        isActive = tag.getBoolean("PortalActive");
        activeTextureData.readFromNBT(tag, "Texture");
        instability = tag.getInteger("Instability");
        moduleManipulator = BlockPos.readFromNBT(tag, "portalManip");

        ArrayList<BlockPos> f = EnhancedPortals.proxy.getControllerList(getBlockPos());

        if (f != null) {
            for (BlockPos frames : f)
                WorldUtilities.markBlockForUpdate(getWorldObj(), frames);

            EnhancedPortals.proxy.clearControllerList(getBlockPos());
        }
    }

    @Override
    public void breakBlock(Block b, int oldMetadata) {
        deconstruct();
        EnhancedPortals.proxy.networkManager.removePortalNID(this);
        EnhancedPortals.proxy.networkManager.removePortalUID(this);
    }

    void configurePortal() throws PortalException {
        ArrayList<BlockPos> portalStructure = PortalUtils.getAllPortalComponents(this);

        for (BlockPos c : portalStructure) {
            TileEntity tile = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (tile instanceof TileRedstoneInterface)
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
            else if (tile instanceof TileFrame)
                portalFrames.add(c);
            else {
                portalBlocks.add(c);
                continue;
            }

            ((TilePortalPart) tile).setPortalController(getBlockPos());
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
        if (pairedControllerLOAD != null && pairedControllerLOAD.isWorldAvailable()) {
            TileEntity t = WorldUtilities.getTileEntity(pairedControllerLOAD);

            if (t instanceof TileController)
                pairedController = (TileController) t;
            else
                deconstructConnectionRemote();

            pairedControllerLOAD = null;
        }

        if (isPortalActive() && !worldObj.isRemote && EPConfiguration.requirePower) {
            if (extractEnergy(ForgeDirection.UNKNOWN, EPConfiguration.keepAliveCost, true) == EPConfiguration.keepAliveCost)
                extractEnergy(ForgeDirection.UNKNOWN, EPConfiguration.keepAliveCost, false);
            else
                deconstructConnection();
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

        for (BlockPos c : portalFrames) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (BlockPos c : redstoneInterfaces) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (BlockPos c : networkInterfaces) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (BlockPos c : diallingDevices) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (BlockPos c : transferFluids) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (BlockPos c : transferItems) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        for (BlockPos c : transferEnergy) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (t != null && t instanceof TilePortalPart)
                ((TilePortalPart) t).setPortalController(null);
        }

        if (moduleManipulator != null) {
            TileEntity t = WorldUtilities.getTileEntity(getWorldObj(), moduleManipulator);

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

    public TilePortalManipulator getPortalManipulator() {
        if (moduleManipulator != null) {
            TileEntity tile = WorldUtilities.getTileEntity(getWorldObj(), moduleManipulator);

            if (tile instanceof TilePortalManipulator)
                return (TilePortalManipulator) tile;
        }

        return null;
    }

    public TileDialingDevice getDialDeviceRandom() {
        BlockPos dial = null;

        if (diallingDevices.isEmpty())
            return null;
        else if (diallingDevices.size() == 1)
            dial = diallingDevices.get(0);
        else
            dial = diallingDevices.get(new Random().nextInt(diallingDevices.size()));

        TileEntity tile = WorldUtilities.getTileEntity(getWorldObj(), dial);

        if (tile != null && tile instanceof TileDialingDevice)
            return (TileDialingDevice) tile;

        return null;
    }

    int getEntityTeleportCost(Entity entity) {
        if (entity == null || !EPConfiguration.requirePower)
            return 0;
        else if (entity instanceof EntityItem)
            return EPConfiguration.entityBaseCost / 10;

        return (int) (EPConfiguration.entityBaseCost * (entity.width * entity.height));
    }

    public void onEntityEnterPortal(Entity entity, TilePortal tilePortal) {
        if (pairedController == null) {
            portalRemove();
            return;
        }

        try {
            int teleportCost = getEntityTeleportCost(entity);
            int sim = extractEnergy(ForgeDirection.UNKNOWN, teleportCost, true), sim2 = pairedController.extractEnergy(ForgeDirection.UNKNOWN, teleportCost, true), third = teleportCost / 3;
            int instabilityA = sim == teleportCost ? 0 : sim >= third * 2 ? 1 : sim >= third ? 2 : -1;
            int instabilityB = sim2 == teleportCost ? 0 : sim2 >= third * 2 ? 1 : sim2 >= third ? 2 : -1;
            int instability = 0;

            if (instabilityA == -1)
                throw new PortalException(Locale.CHAT_ERROR_NOT_ENOUGH_ENERGY_TO_TRANSFER);
            else if (instabilityB == -1)
                throw new PortalException(Locale.CHAT_ERROR_NOT_ENOUGH_ENERGY_TO_RECEIVE);
            else
                instability = instabilityA + instabilityB;

            if (this.instability > instability)
                instability = this.instability;

            int rand = EPRenderers.random.nextInt(10);

            if (entity instanceof EntityLivingBase) {
                if (instability == 1) { // TODO: before-teleport instability effects
                    if (rand < 4) { // Level 1 @ 40%
                        PotionEffect blindness = new PotionEffect(Potion.blindness.id, 200, 1);
                        PotionEffect hunger = new PotionEffect(Potion.hunger.id, 200, 1);
                        PotionEffect poison = new PotionEffect(Potion.poison.id, 200, 1);

                        blindness.setCurativeItems(new ArrayList<ItemStack>());
                        hunger.setCurativeItems(new ArrayList<ItemStack>());
                        poison.setCurativeItems(new ArrayList<ItemStack>());

                        int effect = EPRenderers.random.nextInt(3);
                        ((EntityLivingBase) entity).addPotionEffect(effect == 0 ? blindness : effect == 1 ? hunger : poison);
                    }
                } else if (instability == 2) {
                    if (rand < 8)  { // Level 2 @ 80%
                        PotionEffect blindness = new PotionEffect(Potion.blindness.id, 400, 1);
                        PotionEffect hunger = new PotionEffect(Potion.hunger.id, 400, 1);
                        PotionEffect poison = new PotionEffect(Potion.poison.id, 400, 1);

                        blindness.setCurativeItems(new ArrayList<ItemStack>());
                        hunger.setCurativeItems(new ArrayList<ItemStack>());
                        poison.setCurativeItems(new ArrayList<ItemStack>());

                        int effect = EPRenderers.random.nextInt(3);

                        if (effect == 0) {
                            ((EntityLivingBase) entity).addPotionEffect(blindness);
                            ((EntityLivingBase) entity).addPotionEffect(hunger);
                        } else if (effect == 1) {
                            ((EntityLivingBase) entity).addPotionEffect(blindness);
                            ((EntityLivingBase) entity).addPotionEffect(poison);
                        } else {
                            ((EntityLivingBase) entity).addPotionEffect(poison);
                            ((EntityLivingBase) entity).addPotionEffect(hunger);
                        }
                    }
                } else if (instability == 3) {
                    if (rand < 5) { // Level 3 @ 50%
                        PotionEffect blindness = new PotionEffect(Potion.blindness.id, 600, 1);
                        PotionEffect hunger = new PotionEffect(Potion.hunger.id, 600, 1);
                        PotionEffect poison = new PotionEffect(Potion.poison.id, 600, 1);

                        blindness.setCurativeItems(new ArrayList<ItemStack>());
                        hunger.setCurativeItems(new ArrayList<ItemStack>());
                        poison.setCurativeItems(new ArrayList<ItemStack>());

                        ((EntityLivingBase) entity).addPotionEffect(blindness);
                        ((EntityLivingBase) entity).addPotionEffect(hunger);
                        ((EntityLivingBase) entity).addPotionEffect(poison);
                    }
                } else if (instability == 4) {
                    if (rand < 8) { // Level 4 @ 80%
                        // TODO: More punishing effect than this
                        PotionEffect blindness = new PotionEffect(Potion.blindness.id, 600, 1);
                        PotionEffect hunger = new PotionEffect(Potion.hunger.id, 600, 1);
                        PotionEffect poison = new PotionEffect(Potion.poison.id, 600, 1);

                        blindness.setCurativeItems(new ArrayList<ItemStack>());
                        hunger.setCurativeItems(new ArrayList<ItemStack>());
                        poison.setCurativeItems(new ArrayList<ItemStack>());

                        ((EntityLivingBase) entity).addPotionEffect(blindness);
                        ((EntityLivingBase) entity).addPotionEffect(hunger);
                        ((EntityLivingBase) entity).addPotionEffect(poison);
                    }
                }
            }

            EntityManager.teleportEntity(entity, this, pairedController);
            onEntityTeleported(entity, instability);
            pairedController.onEntityTeleported(entity, instability);
        } catch (PortalException e) {
            sendErrorMessage(e.getMessage(), entity);
        }
        
        for (BlockPos c : redstoneInterfaces)
            ((TileRedstoneInterface) WorldUtilities.getTileEntity(getWorldObj(), c)).onEntityTeleport(entity);
    }

    public void onEntityTeleported(Entity entity, int instability) {
        int teleportCost = getEntityTeleportCost(entity);
        
        if (instability == 0) {
            extractEnergy(ForgeDirection.UNKNOWN, teleportCost, false);
        } else if (instability == 1 || instability == 2) {
            extractEnergy(ForgeDirection.UNKNOWN, (teleportCost / 3) * 2, false);
        } else if (instability == 3 || instability == 4) {
            extractEnergy(ForgeDirection.UNKNOWN, teleportCost / 3, false);
        }

        TilePortalManipulator module = getPortalManipulator();

        if (module != null && module.hasModule(PortalModules.FEATHERFALL) && entity instanceof EntityLivingBase)
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ECItems.potionFeatherfall.id, 200, 0));
    }

    /**
     * Creates the portal block. Throws an {@link PortalException} if an error occurs.
     */
    public void portalCreate() throws PortalException {
        for (BlockPos c : portalBlocks)
            if (!WorldUtilities.isAirBlock(getWorldObj(), c))
                if (EPConfiguration.portalDestroysBlocks)
                    WorldUtilities.setBlockToAir(getWorldObj(), c);
                else
                    throw new PortalException(Locale.CHAT_ERROR_FAILED_TO_CREATE_PORTAL);

        for (BlockPos c : portalBlocks) {
            WorldUtilities.setBlock(getWorldObj(), c, EPBlocks.portal, portalType, 2);

            TilePortal portal = (TilePortal) WorldUtilities.getTileEntity(getWorldObj(), c);
            portal.portalController = getBlockPos();
        }

        for (BlockPos c : redstoneInterfaces) {
            TileRedstoneInterface ri = (TileRedstoneInterface) WorldUtilities.getTileEntity(getWorldObj(), c);
            ri.onPortalCreated();
        }
    }

    public void portalRemove() {
        if (processing) return;
        processing = true;

        for (BlockPos c : portalBlocks)
            WorldUtilities.setBlockToAir(getWorldObj(), c);

        for (BlockPos c : redstoneInterfaces) {
            TileRedstoneInterface ri = (TileRedstoneInterface) WorldUtilities.getTileEntity(getWorldObj(), c);
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
        storage.readFromNBT(tagCompound);

        portalFrames = BlockPos.readListFromNBT(tagCompound, "Frames");
        portalBlocks = BlockPos.readListFromNBT(tagCompound, "Portals");
        redstoneInterfaces = BlockPos.readListFromNBT(tagCompound, "RedstoneInterfaces");
        networkInterfaces = BlockPos.readListFromNBT(tagCompound, "NetworkInterface");
        diallingDevices = BlockPos.readListFromNBT(tagCompound, "DialDevice");
        transferEnergy = BlockPos.readListFromNBT(tagCompound, "TransferEnergy");
        transferFluids = BlockPos.readListFromNBT(tagCompound, "TransferFluid");
        transferItems = BlockPos.readListFromNBT(tagCompound, "TransferItems");
        moduleManipulator = BlockPos.readFromNBT(tagCompound, "ModuleManipulator");
        pairedControllerLOAD = WorldPos.readFromNBT(tagCompound, "PairedController");

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
        storage.writeToNBT(tagCompound);

        BlockPos.writeListToNBT(portalFrames, "Frames", tagCompound);
        BlockPos.writeListToNBT(portalBlocks, "Portals", tagCompound);
        BlockPos.writeListToNBT(redstoneInterfaces, "RedstoneInterfaces", tagCompound);
        BlockPos.writeListToNBT(networkInterfaces, "NetworkInterface", tagCompound);
        BlockPos.writeListToNBT(diallingDevices, "DialDevice", tagCompound);
        BlockPos.writeListToNBT(transferEnergy, "TransferEnergy", tagCompound);
        BlockPos.writeListToNBT(transferFluids, "TransferFluid", tagCompound);
        BlockPos.writeListToNBT(transferItems, "TransferItems", tagCompound);

        if (moduleManipulator != null)
            moduleManipulator.writeToNBT(tagCompound, "ModuleManipulator");

        if (pairedController != null)
            pairedController.getWorldPos().writeToNBT(tagCompound, "PairedController");

        activeTextureData.writeToNBT(tagCompound, "ActiveTextureData");

        if (inactiveTextureData != null)
            inactiveTextureData.writeToNBT(tagCompound, "InactiveTextureData");
    }

    void swapTextureData(PortalTextureManager textureManager) {
        if (textureManager == null) return;
        inactiveTextureData = new PortalTextureManager(activeTextureData);
        activeTextureData = textureManager;
        markDirty();
    }

    void revertTextureData() {
        if (inactiveTextureData == null) return;
        activeTextureData = new PortalTextureManager(inactiveTextureData);
        inactiveTextureData = null;
        markDirty();
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

            for (BlockPos c : portalFrames)
                if (!chunks.contains(c.getChunk())) {
                    EnhancedPortals.instance.packetPipeline.sendToAllAround(new PacketRerender(c.getX(), c.getY(), c.getZ()), this);
                    chunks.add(c.getChunk());
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
        } else {
            EnhancedPortals.instance.getLogger().warn("Could not get a ChunkLoading ticket for Portal at " + xCoord + ", " + yCoord + ", " + zCoord + " (Dimension: " + getWorldObj().provider.dimensionId + ") Things may not work as expected!"); 
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
        return EPConfiguration.requirePower ? storage.getEnergyStored() >= EPConfiguration.initializationCost : true;
    }

    void sendErrorMessage(String msg, Entity entity) {
        if (entity != null) {
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).addChatComponentMessage(Localisation.getChatError(EPMod.ID, msg));
                return;
            }
        }

        lastError = Localisation.getError(EPMod.ID, msg);
    }

    public void constructConnection() {
        if (EnhancedPortals.proxy.networkManager.hasNID(this))
            constructConnection(EnhancedPortals.proxy.networkManager.getNetworkedPortalNext(this), null, null);
    }

    public void constructConnection(GlyphIdentifier identifier, PortalTextureManager texture, EntityPlayer player) {
        if (isPortalActive()) {
            sendErrorMessage(Locale.CHAT_ERROR_PORTAL_ACTIVE, player);
            return;
        } else if (!hasEnoughPower()) {
            sendErrorMessage(Locale.CHAT_ERROR_NO_POWER, player);
            return;
        } else if (!EnhancedPortals.proxy.networkManager.UIDInUse(identifier)) {
            sendErrorMessage(Locale.CHAT_ERROR_NO_DESTINATION, player);
            return;
        }

        TileController paired = EnhancedPortals.proxy.networkManager.getPortalController(identifier);

        if (paired == this) {
            sendErrorMessage(Locale.CHAT_ERROR_CANT_DIAL_SELF, player);
            return;
        }

        try {
            forceChunk();
            paired.constructConnection(this, texture);

            try {
                portalCreate();
            } catch (PortalException ex) { // catch the error here and then send the deconstruct off to the paired controller
                paired.deconstructConnectionRemote();
                throw new PortalException(ex.getMessage()); // so it gets caught later on
            }

            swapTextureData(texture);
            storage.extractEnergy(EPConfiguration.initializationCost, false);
        } catch (PortalException e) {
            sendErrorMessage(e.getMessage(), player);
            releaseChunk();
            return;
        }

        pairedController = paired;
        markDirty();
        sendUpdatePacket(false);
    }

    public void constructConnection(TileController requester, PortalTextureManager texture) throws PortalException {
        if (isPortalActive())
            throw new PortalException(Locale.CHAT_ERROR_PORTAL_ACTIVE_REMOTE);
        else if (!hasEnoughPower())
            throw new PortalException(Locale.CHAT_ERROR_NO_POWER_REMOTE);

        forceChunk();
        portalCreate();
        pairedController = requester;
        swapTextureData(texture);
        storage.extractEnergy(EPConfiguration.initializationCost, false);
        markDirty();
        sendUpdatePacket(false);
    }

    public void deconstructConnection() {
        releaseChunk();
        pairedController.deconstructConnectionRemote();
        pairedController = null;
        portalRemove();
        revertTextureData();
        markDirty();
        sendUpdatePacket(false);
    }

    public void deconstructConnectionRemote() {
        releaseChunk();
        pairedController = null; 
        portalRemove();
        revertTextureData();
        markDirty();
        sendUpdatePacket(false);
    }

    public TileController getDestination() {
        return pairedController;
    }

    /* Setters to set value and update clients */

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

    public void setModuleManipulator(BlockPos chunkCoordinates) {
        moduleManipulator = chunkCoordinates;
        markDirty();
    }

    /* ComputerCraft & OpenComputers */

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

    /* ComputerCraft */

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String getType() {
        return "ep_controller";
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
    @Method(modid = ComputerCraft.MOD_ID)
    public String[] getMethodNames() {
        return new String[] { "isPortalActive", "getUniqueIdentifier", "setUniqueIdentifier", "getFrameColour", "setFrameColour", "getPortalColour", "setPortalColour", "getParticleColour", "setParticleColour" };
    }

    /* OpenComputers */

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
    @Method(modid = OpenComputers.MOD_ID)
    public String getComponentName() {
        return "ep_controller";
    }

    @Callback(direct = true, doc = "function():number -- Returns the hexadecimal colour of the portal frame.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] getFrameColour(Context context, Arguments args) throws Exception {
        return new Object[] { activeTextureData.getFrameColour() };
    }

    /* IEnergyHandler */

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    public EnergyStorage getEnergyStorage() {
        return storage;
    }
}
