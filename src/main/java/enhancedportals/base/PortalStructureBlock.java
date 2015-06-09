
package enhancedportals.base;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class PortalStructureBlock extends BlockContainerEP {

	protected PortalStructureBlock(String name, Material material) {
		super(name, material);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if (tile instanceof PortalStructureTileEntity) {
			return ((PortalStructureTileEntity) tile).onBlockActivated(player, player.inventory.mainInventory[player.inventory.currentItem]);
		}
		
		return super.onBlockActivated(world, x, y, z, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
	}

}
