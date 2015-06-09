package enhancedportals.portal.structure;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
	public int PORTAL_TYPE = -1;
	
	Random rand = new Random(); // TEMPORARY
	
	public STController() {
		super(1);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if (getWorldObj().isRemote)
			return true;

		if (held.getItem() == Items.blaze_rod) {
			PortalStructureConstructor structure = new PortalStructureConstructor();
			
			try {
				structure.construct(getWorldObj(), this);
			} catch (StructureConstructException e) {
				System.out.println(e.getMessage());
				return true;
			}
			
			setControllerForChildren(false);			
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
	
	void setControllerForChildren(boolean clear) {
		STController controller = clear ? null : this;
		
		for (PortalStructureTileEntity t : STRUCT_DIALLING) {
			t.setController(controller);
		}
		
		for (PortalStructureTileEntity t : STRUCT_FRAME) {
			t.setController(controller);
		}
		
		for (PortalStructureTileEntity t : STRUCT_NETWORK) {
			t.setController(controller);
		}
		
		if (STRUCT_MANIP != null)
			STRUCT_MANIP.setController(controller);
		
		for (PortalStructureTileEntity t : STRUCT_REDSTONE) {
			t.setController(controller);
		}
		
		for (PortalStructureTileEntity t : STRUCT_TRANS_ENERGY) {
			t.setController(controller);
		}
		
		for (PortalStructureTileEntity t : STRUCT_TRANS_FLUID) {
			t.setController(controller);
		}
		
		for (PortalStructureTileEntity t : STRUCT_TRANS_ITEM) {
			t.setController(controller);
		}
	}
	
	boolean constructPortal() {
		if (STRUCT_PORTAL != null || STRUCT_PORTAL.length > 0)
			return false;
		
		STRUCT_PORTAL = new STPortal[STRUCT_PORTAL_LOC.length];
		
		for (int i = 0; i < STRUCT_PORTAL.length; i++) {
			ChunkCoordinates c = STRUCT_PORTAL_LOC[i];
			getWorldObj().setBlock(c.posX, c.posY, c.posZ, SBPortal.instance);
			STRUCT_PORTAL[i] = (STPortal) getWorldObj().getTileEntity(c.posX, c.posY, c.posZ);
		}
		
		return true;
	}
	
	void deconstructPortal() {
		for (int i = 0; i < STRUCT_PORTAL.length; i++) {
			STPortal portal = STRUCT_PORTAL[i];
			getWorldObj().setBlock(portal.xCoord, portal.yCoord, portal.zCoord, Blocks.air);
		}
		
		STRUCT_PORTAL = null;
	}
}
