package enhancedportals.portal.structure;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import enhancedportals.EnhancedPortals;
import enhancedportals.base.PortalStructureBlock;
import enhancedportals.base.PortalStructureTileEntity;
import enhancedportals.network.ProxyClient;
import enhancedportals.network.ProxyCommon;
import enhancedportals.util.ConnectedTextures;
import enhancedportals.util.ConnectedTexturesDetailed;

public class SBFrame extends PortalStructureBlock {
	ConnectedTextures connectedTextures;
	IIcon defaultTexture;
	IIcon[] textureOverlays = new IIcon[10];
	IIcon[] textureFull = new IIcon[10];

	public SBFrame(String name) {
		super(name, Material.rock);

		if (ProxyCommon.CONFIG_CT_LEVEL == 2)
			connectedTextures = new ConnectedTexturesDetailed(EnhancedPortals.MOD_ID + ":frame/%s", this, -1);
		else if (ProxyCommon.CONFIG_CT_LEVEL == 1)
			connectedTextures = new ConnectedTextures(EnhancedPortals.MOD_ID + ":frame/%s", this, -1);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta) {
		default:
		case 0:
			return new STFrame();
		case 1:
			return new STController();
		case 2:
			return new STRedstoneInterface();
		case 3:
			return new STNetworkInterface();
		case 4:
			return new STDiallingDevice();
		case 6:
			return new STPortalManipulator();
		case 7:
			return new STTransferFluid();
		case 8:
			return new STTransferItem();
		case 9:
			return new STTransferEnergy();
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		if (connectedTextures != null) {
			connectedTextures.registerIcons(iconRegister);
			defaultTexture = connectedTextures.getBaseIcon();
		} else
			defaultTexture = iconRegister.registerIcon(EnhancedPortals.MOD_ID + ":frame/0");
		
		for (int i = 0; i < 10; i++) {
			textureOverlays[i] = iconRegister.registerIcon(EnhancedPortals.MOD_ID + ":frame_" + i);
			textureFull[i] = iconRegister.registerIcon(EnhancedPortals.MOD_ID + ":frame_" + i + "b");
		}
		
		ProxyClient.blankIcon = iconRegister.registerIcon(EnhancedPortals.MOD_ID + ":blank");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int s) {
		if (ProxyClient.renderPass == 0) {
			TileEntity tile = blockAccess.getTileEntity(x, y, z);
			boolean useCT = false;
			
			if (tile != null && tile instanceof PortalStructureTileEntity) {
				PortalStructureTileEntity te = (PortalStructureTileEntity) tile;
				IIcon texture = te.getBlockTexture(s);
				useCT = te.hasController();
				
				if (texture != null)
					return texture;
			}
			
			if (useCT && connectedTextures != null) {
				return connectedTextures.getIconForSide(blockAccess, x, y, z, s);
			}
			
			return defaultTexture;
		} else {
			int meta = blockAccess.getBlockMetadata(x, y, z);
			
			if (meta == 4 || (ProxyClient.hasGoggles && meta >= 0 && meta <= 9))
				return textureOverlays[meta];
			else
				return ProxyClient.blankIcon;
		}
	}
	
	@Override
	public IIcon getIcon(int s, int m) {
		return m >= 0 && m <= 9 ? textureFull[m] : defaultTexture;
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
		return false;
	}
	
	@Override
	public boolean canRenderInPass(int pass) {
		ProxyClient.renderPass = pass;
		return pass < 2;
	}
	
	@Override
    public int getRenderBlockPass() {
        return 1;
    }
	
	public boolean isOpaqueCube() {
        return false;
    }
	
	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int s) {
		 if (blockAccess.getBlock(x, y, z) == this)
			 return false;
		 
		return super.shouldSideBeRendered(blockAccess, x, y, z, s);
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int s) {
		TileEntity tile = blockAccess.getTileEntity(x, y, z);
		
		if (tile != null && tile instanceof STRedstoneInterface) {
			return ((STRedstoneInterface) tile).isProvidingStrongPower(s);
		}
		
		return 0;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int s) {
		TileEntity tile = blockAccess.getTileEntity(x, y, z);
		
		if (tile != null && tile instanceof STRedstoneInterface) {
			return ((STRedstoneInterface) tile).isProvidingWeakPower(s);
		}
		
		return 0;
	}
}
