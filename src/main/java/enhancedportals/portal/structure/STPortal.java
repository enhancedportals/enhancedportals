package enhancedportals.portal.structure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import enhancedportals.base.PortalStructureTileEntity;

public class STPortal extends PortalStructureTileEntity {

	public STPortal(int meta) {
		super(meta);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		// TODO Auto-generated method stub
		return false;
	}

}
