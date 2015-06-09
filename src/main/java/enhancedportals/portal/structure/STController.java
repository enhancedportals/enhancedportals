package enhancedportals.portal.structure;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import enhancedportals.base.PortalStructureTileEntity;
import enhancedportals.network.ProxyCommon;
import enhancedportals.portal.NetworkMap;
import enhancedportals.portal.PortalStructureConstructor;
import enhancedportals.portal.StructureConstructException;
import enhancedportals.util.WorldCoordinates;

public class STController extends PortalStructureTileEntity {
	public STDiallingDevice[] STRUCT_DIALLING;
	public STFrame[] STRUCT_FRAME;
	public STNetworkInterface[] STRUCT_NETWORK;
	public STPortalManipulator STRUCT_MANIP;
	public STRedstoneInterface[] STRUCT_REDSTONE;
	public STTransferEnergy[] STRUCT_TRANS_ENERGY;
	public STTransferFluid[] STRUCT_TRANS_FLUID;
	public STTransferItem[] STRUCT_TRANS_ITEM;
	public STPortal[] STRUCT_PORTAL;
	public ChunkCoordinates[] STRUCT_PORTAL_LOC;
	
	Random rand = new Random(); // TEMPORARY
	
	public STController() {
		super(1);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		//if (getWorldObj().isRemote)
		//	return true;

		if (held.getItem() == Items.blaze_rod) {
			PortalStructureConstructor structure = new PortalStructureConstructor();
			
			try {
				structure.construct(getWorldObj(), this);
			} catch (StructureConstructException e) {
				System.out.println(e.getMessage());
			}
			
			return true;
		}
		
		if (held.getItem() == Items.apple) {
			if (ProxyCommon.first == null) {
				ProxyCommon.first = new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId);
			} else if (!ProxyCommon.first.equals(new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId))) {
				NetworkMap.connectPortals(NetworkMap.getPortalUID(ProxyCommon.first), NetworkMap.getPortalUID(new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId)));
				ProxyCommon.first = null;
			}
		} else if (held.getItem() == Items.arrow) {
			NetworkMap.disconnectPortals( NetworkMap.getPortalUID(new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId)));
		} else if (held.getItem() == Items.bone) {
			NetworkMap.updatePortalPosition(rand.nextInt(10) + "-" + rand.nextInt(10), new WorldCoordinates(xCoord, yCoord, zCoord, getWorldObj().provider.dimensionId));
		}
		
		return true;
	}
}
