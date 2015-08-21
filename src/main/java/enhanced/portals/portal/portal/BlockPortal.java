package enhanced.portals.portal.portal;

import java.util.Random;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enhanced.base.repack.codechicken.lib.colour.Colour;
import enhanced.base.repack.codechicken.lib.colour.ColourARGB;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.Reference.EPConfiguration;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.EPRenderers;
import enhanced.portals.Reference.PortalModules;
import enhanced.portals.client.PortalParticleFX;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.portal.EntityManager;
import enhanced.portals.portal.controller.TileController;
import enhanced.portals.portal.manipulator.TilePortalManipulator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockContainer {
    IIcon texture;

    public BlockPortal(String n) {
        super(Material.portal);
        setBlockUnbreakable();
        setResistance(2000);
        setBlockName(n);
        setLightOpacity(0);
        setStepSound(soundTypeGlass);
    }

    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            return ((TilePortal) tile).getColour();

        return 0xFFFFFF;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TilePortal();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            return ((TilePortal) tile).getBlockTexture(side);

        return null;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return texture;
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return null;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 14;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public int getRenderType() {
        return EPRenderers.portal;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            return ((TilePortal) tile).activate(player, player.inventory.getCurrentItem());

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (EntityManager.isEntityFitForTravel(entity)) {
            if (entity instanceof EntityPlayer)
                ((EntityPlayer) entity).closeScreen();

            if (!world.isRemote) {
                TileEntity t = world.getTileEntity(x, y, z);

                if (t instanceof TilePortal)
                    ((TilePortal) t).onEntityCollidedWithBlock(entity);
            }
        }

        EntityManager.setEntityPortalCooldown(entity);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (EPConfiguration.disableSounds && EPConfiguration.disableParticles)
            return;

        TileEntity tile = world.getTileEntity(x, y, z);

        if (!(tile instanceof TilePortal))
            return;

        int metadata = world.getBlockMetadata(x, y, z);
        TileController controller = ((TilePortal) tile).getPortalController();
        TilePortalManipulator module = controller == null ? null : controller.getPortalManipulator();
        boolean doSounds = !EPConfiguration.disableSounds && random.nextInt(100) == 0, doParticles = !EPConfiguration.disableParticles;

        if (module != null) {
            if (doSounds)
                doSounds = !module.hasModule(PortalModules.SOUNDS_REMOVE);

            if (doParticles)
                doParticles = !module.hasModule(PortalModules.PARTICLES_REMOVE);
        }

        if (doSounds)
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "portal.portal", 0.5F, random.nextFloat() * 0.4F + 0.8F, false);

        if (doParticles)
            for (int l = 0; l < 4; ++l) {
                double d0 = x + random.nextFloat();
                double d1 = y + random.nextFloat();
                double d2 = z + random.nextFloat();
                double d3 = 0.0D;
                double d4 = 0.0D;
                double d5 = 0.0D;
                int i1 = random.nextInt(2) * 2 - 1;
                d3 = (random.nextFloat() - 0.5D) * 0.5D;
                d4 = (random.nextFloat() - 0.5D) * 0.5D;
                d5 = (random.nextFloat() - 0.5D) * 0.5D;

                if (metadata == 1) {
                    d2 = z + 0.5D + 0.25D * i1;
                    d5 = random.nextFloat() * 2.0F * i1;
                } else if (metadata == 2) {
                    d0 = x + 0.5D + 0.25D * i1;
                    d3 = random.nextFloat() * 2.0F * i1;
                } else if (metadata == 3) {
                    d1 = y + 0.5D + 0.25D * i1;
                    d4 = random.nextFloat() * 2.0F * i1;
                } else if (metadata == 4)
                    d3 = d5 = random.nextFloat() * 2F * i1;
                else if (metadata == 5) {
                    d3 = d5 = random.nextFloat() * 2F * i1;
                    d3 = -d3;
                }

                PortalParticleFX fx = new PortalParticleFX(world, controller, d0, d1, d2, d3, d4, d5);

                if (module != null) {
                    boolean rainbow = module.hasModule(PortalModules.PARTICLES_RAINBOW), tint = module.hasModule(PortalModules.PARTICLES_TINTSHADE);

                    if (rainbow || tint) {
                        Colour c = new ColourARGB(0, (int) (fx.getRedColorF() * 255), (int) (fx.getGreenColorF() * 255), (int) (fx.getBlueColorF() * 255));

                        if (rainbow)
                            c.set(new ColourARGB(0, EPRenderers.random.nextInt(256), EPRenderers.random.nextInt(256), EPRenderers.random.nextInt(256)));

                        if (tint)
                            if (EPRenderers.random.nextBoolean())
                                c.interpolate(new ColourARGB(0, 255, 255, 255), MathHelper.clamp_double(EPRenderers.random.nextDouble(), 0.3, 0.7));
                            else
                                c.interpolate(new ColourARGB(0, 0, 0, 0), MathHelper.clamp_double(EPRenderers.random.nextDouble(), 0.3, 0.7));

                        int r = c.r, g = c.g, b = c.b;

                        if (c.r < 0)
                            r = 128 + c.r + 128;

                        if (c.g < 0)
                            g = 128 + c.g + 128;

                        if (c.b < 0)
                            b = 128 + c.b + 128;

                        fx.setRBGColorF(r / 255f, g / 255f, b / 255f);
                    }
                }

                FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
            }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        texture = iconRegister.registerIcon(EPMod.ID + ":portal");
        int counter = 0;
        ProxyClient.customPortalTextures.clear();

        while (ProxyClient.resourceExists("textures/blocks/customPortal/" + String.format("%02d", counter) + ".png")) {
            EnhancedPortals.instance.getLogger().debug("Registered custom portal Icon: " + String.format("%02d", counter) + ".png");
            ProxyClient.customPortalTextures.add(iconRegister.registerIcon(EPMod.ID + ":customPortal/" + String.format("%02d", counter)));
            counter++;
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TilePortal) {
            TilePortal portal = (TilePortal) tile;
            TileController controller = portal.getPortalController();
            TilePortalManipulator manip = controller == null ? null : controller.getPortalManipulator();

            if (controller != null && manip != null && manip.hasModule(PortalModules.PORTAL_INVISIBLE)) {
                setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
                return;
            }

            int meta = blockAccess.getBlockMetadata(x, y, z);

            if (meta == 1)
                setBlockBounds(0f, 0f, 0.375f, 1f, 1f, 0.625f);
            else if (meta == 2)
                setBlockBounds(0.375f, 0f, 0f, 0.625f, 1f, 1f);
            else if (meta == 3)
                setBlockBounds(0, 0.375f, 0f, 1f, 0.625f, 1f);
            else
                setBlockBounds(0f, 0f, 0f, 1f, 1, 1f);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (blockAccess.getBlock(x, y, z) == this || blockAccess.getBlock(x, y, z) == EPBlocks.frame)
            return false;

        return super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }
}
