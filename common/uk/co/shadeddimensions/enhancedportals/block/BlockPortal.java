package uk.co.shadeddimensions.enhancedportals.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import uk.co.shadeddimensions.enhancedportals.tileentity.TilePortal;
import uk.co.shadeddimensions.enhancedportals.util.PortalUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortal extends BlockEP
{
    public BlockPortal(int id, String name)
    {
        super(id, Material.portal, false);
        setBlockUnbreakable();
        setResistance(2000);
        setUnlocalizedName(name);
        setStepSound(soundGlassFootstep);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (par5Random.nextInt(100) == 0)
        {
            par1World.playSound(par2 + 0.5D, par3 + 0.5D, par4 + 0.5D, "portal.portal", 0.5F, par5Random.nextFloat() * 0.4F + 0.8F, false);
        }

        /*for (int l = 0; l < 4; ++l)
        {
            double d0 = par2 + par5Random.nextFloat();
            double d1 = par3 + par5Random.nextFloat();
            double d2 = par4 + par5Random.nextFloat();
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = par5Random.nextInt(2) * 2 - 1;
            d3 = (par5Random.nextFloat() - 0.5D) * 0.5D;
            d4 = (par5Random.nextFloat() - 0.5D) * 0.5D;
            d5 = (par5Random.nextFloat() - 0.5D) * 0.5D;

            if (par1World.getBlockId(par2 - 1, par3, par4) != blockID && par1World.getBlockId(par2 + 1, par3, par4) != blockID)
            {
                d0 = par2 + 0.5D + 0.25D * i1;
                d3 = par5Random.nextFloat() * 2.0F * i1;
            }
            else
            {
                d2 = par4 + 0.5D + 0.25D * i1;
                d5 = par5Random.nextFloat() * 2.0F * i1;
            }

            // TODO
            TilePortal portal = (TilePortal) par1World.getBlockTileEntity(par2, par3, par4);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new PortalFX(par1World, portal.texture, d0, d1, d2, d3, d4, d5));
        }*/
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return 0;
    }

    @Override
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
    {
        if (world.isRemote)
        {
            return;
        }

        PortalUtils.removePortalAround((WorldServer) world, x, y, z);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion par5Explosion)
    {
        if (world.isRemote)
        {
            return;
        }

        PortalUtils.removePortalAround((WorldServer) world, x, y, z);
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        // TODO: teleport
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        int meta = blockAccess.getBlockMetadata(x, y, z);
  
        if (meta == 1) // X
        {
            setBlockBounds(0f, 0f, 0.375f, 1f, 1f, 0.625f);
        }
        else if (meta == 2) // Z
        {
            setBlockBounds(0.375f, 0f, 0f, 0.625f, 1f, 1f);
        }
        else if (meta == 3) // XZ
        {
            setBlockBounds(0, 0.375f, 0f, 1f, 0.625f, 1f);
        }
        else if (meta >= 6)
        {
            setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
        } 
        else
        {
            setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TilePortal();
    }
    
    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
    {
        return false;
    }
    
    @Override
    public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
    {
        return false;
    }
}
