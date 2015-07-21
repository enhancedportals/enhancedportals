package enhanced.portals.portal.frame;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import enhanced.base.block.BlockContainerBase;
import enhanced.base.utilities.ConnectedTexturesDetailed;
import enhanced.base.xmod.ComputerCraft;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.EPRenderers;
import enhanced.portals.Reference.PortalFrames;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.portal.TilePortalPart;

@InterfaceList(value = { @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = ComputerCraft.MOD_ID) })
public class BlockFrame extends BlockContainerBase implements IPeripheralProvider {
    public static ConnectedTexturesDetailed connectedTextures;

    public BlockFrame(String n) {
        super(EPMod.ID, n, Material.rock, EnhancedPortals.instance.creativeTab, 5f);
        setResistance(2000);
        connectedTextures = new ConnectedTexturesDetailed(EPMod.ID + ":frame/%s", this, -1);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int unknown) {
        TileEntity t = world.getTileEntity(x, y, z);

        if (t != null && t instanceof TileFrame)
            ((TileFrame) t).breakBlock(block, unknown);

        super.breakBlock(world, x, y, z, block, unknown);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        EPRenderers.renderPass = pass;
        return pass < 2;
    }

    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TileFrame)
            return ((TileFrame) tile).getColour();

        return 0xFFFFFF;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        PortalFrames frame = Reference.PortalFrames.get(metadata);

        if (frame == PortalFrames.BASIC)
            return new TileFrame();
        else if (frame == PortalFrames.CONTROLLER)
            return new TileController();
        else if (frame == PortalFrames.REDSTONE)
            return new TileRedstoneInterface();
        else if (frame == PortalFrames.NETWORK)
            return new TileNetworkInterface();
        else if (frame == PortalFrames.DIAL)
            return new TileDialingDevice();
        else if (frame == PortalFrames.PORTAL_MANIPULATOR)
            return new TilePortalManipulator();
        else if (frame == PortalFrames.TRANSFER_FLUID)
            return new TileTransferFluid();
        else if (frame == PortalFrames.TRANSFER_ITEM)
            return new TileTransferItem();
        else if (frame == PortalFrames.TRANSFER_ENERGY)
            return new TileTransferEnergy();

        return null;
    }

    @Override
    public int damageDropped(int par1) {
        return par1;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TileFrame)
            return ((TileFrame) tile).getBlockTexture(side, EPRenderers.renderPass);

        return connectedTextures.getBaseIcon();
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        PortalFrames frame = PortalFrames.get(meta);

        if (frame != null)
            return frame.getFull();

        return connectedTextures.getBaseIcon();
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        TileEntity t = world.getTileEntity(x, y, z);

        if (t != null && (t instanceof TileController || t instanceof TileNetworkInterface || t instanceof TileDialingDevice || t instanceof TileTransferEnergy || t instanceof TileTransferFluid || t instanceof TileTransferItem))
            return (IPeripheral) t;

        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < PortalFrames.count(); i++)
            list.add(new ItemStack(this, 1, i));
    }

    @Override
    public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TileRedstoneInterface)
            return ((TileRedstoneInterface) tile).isProvidingPower(side);

        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TileRedstoneInterface)
            return ((TileRedstoneInterface) tile).isProvidingPower(side);

        return 0;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileFrame)
            return ((TileFrame) tile).activate(player, player.inventory.getCurrentItem());

        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TilePortalPart)
            ((TilePortalPart) tile).onBlockPlaced(entity, stack);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileRedstoneInterface)
            ((TileRedstoneInterface) tile).onNeighborBlockChange(b);
        else if (tile instanceof TileFrameTransfer)
            ((TileFrameTransfer) tile).onNeighborChanged();
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        connectedTextures.registerIcons(register);
        PortalFrames.registerBlockIcons(register);

        int counter = 0;
        ProxyClient.customFrameTextures.clear();

        while (ProxyClient.resourceExists("textures/blocks/customFrame/" + String.format("%02d", counter) + ".png")) {
            EnhancedPortals.instance.getLogger().debug("Registered custom frame Icon: " + String.format("%02d", counter) + ".png");
            ProxyClient.customFrameTextures.add(register.registerIcon(EPMod.ID + ":customFrame/" + String.format("%02d", counter)));
            counter++;
        }
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int s) {
        if (blockAccess.getBlock(x, y, z) == this)
            return false;
        return super.shouldSideBeRendered(blockAccess, x, y, z, s);
    }
}
