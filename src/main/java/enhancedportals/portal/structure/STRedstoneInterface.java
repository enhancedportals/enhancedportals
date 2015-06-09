package enhancedportals.portal.structure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import enhancedportals.base.PortalStructureTileEntity;

public class STRedstoneInterface extends PortalStructureTileEntity {

	byte powerLevel = 0;
	
	public STRedstoneInterface() {
		super(2);
	}

	public int isProvidingStrongPower(int s) {
		return powerLevel;
	}

	public int isProvidingWeakPower(int s) {
		return powerLevel;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		// TODO Auto-generated method stub
		return false;
	}

}
