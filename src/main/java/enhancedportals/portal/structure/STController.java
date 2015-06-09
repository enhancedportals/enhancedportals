package enhancedportals.portal.structure;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import enhancedportals.base.PortalStructureTileEntity;
import enhancedportals.network.ProxyCommon;
import enhancedportals.portal.network.NetworkMap;
import enhancedportals.util.WorldCoordinates;

public class STController extends PortalStructureTileEntity {

	Random rand = new Random();
	
	public STController() {
		super(1);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		//if (getWorldObj().isRemote)
		//	return true;

		if (held.getItem() == Items.apple) {
			if (ProxyCommon.first == null) {
				ProxyCommon.first = new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId);
			} else if (!ProxyCommon.first.equals(new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId))) {
				NetworkMap.connectPortals(NetworkMap.getPortalUID(ProxyCommon.first), NetworkMap.getPortalUID(new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId)));
				ProxyCommon.first = null;
				player.addChatMessage(new ChatComponentText("Connected."));
			}
		} else if (held.getItem() == Items.arrow) {
			NetworkMap.disconnectPortals( NetworkMap.getPortalUID(new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId)));
			player.addChatMessage(new ChatComponentText("Disconnected."));
		} else if (held.getItem() == Items.bone) {
			NetworkMap.updatePortalPosition(rand.nextInt(10) + "-" + rand.nextInt(10) + "-2-3-4-5-6-7-8", new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId));
			player.addChatMessage(new ChatComponentText("Added."));
		}
		
		return true;
	}
}
