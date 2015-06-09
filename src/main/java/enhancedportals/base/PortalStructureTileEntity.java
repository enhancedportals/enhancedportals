package enhancedportals.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import enhancedportals.portal.structure.STController;

public abstract class PortalStructureTileEntity extends TileEntityEP {
	STController controller;
	
	public PortalStructureTileEntity(int meta) {
		super(meta);
	}

	public IIcon getBlockTexture(int s) {
		// TODO custom texture
		return null;
	}

	public abstract boolean onBlockActivated(EntityPlayer player, ItemStack held);

	public boolean hasController() {
		return controller != null;
	}
	
	public void setController(STController c) {
		controller = c;
	}
}
