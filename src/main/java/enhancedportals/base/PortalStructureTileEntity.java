package enhancedportals.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public abstract class PortalStructureTileEntity extends TileEntityEP {

	public PortalStructureTileEntity(int meta) {
		super(meta);
	}

	public IIcon getBlockTexture(int s) {
		// TODO custom texture
		return null;
	}

	public abstract boolean onBlockActivated(EntityPlayer player, ItemStack held);

}
