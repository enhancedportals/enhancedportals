package enhancedportals.portal.structure;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import enhancedportals.base.PortalStructureBlock;

public class SBPortal extends PortalStructureBlock {

	protected SBPortal(String name, Material material) {
		super(name, material);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new STPortal(meta);
	}

}
