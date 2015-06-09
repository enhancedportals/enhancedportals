package enhancedportals.portal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import enhancedportals.base.PortalStructureTileEntity;
import enhancedportals.portal.structure.STController;
import enhancedportals.portal.structure.STDiallingDevice;
import enhancedportals.portal.structure.STFrame;
import enhancedportals.portal.structure.STNetworkInterface;
import enhancedportals.portal.structure.STPortalManipulator;
import enhancedportals.portal.structure.STRedstoneInterface;
import enhancedportals.portal.structure.STTransferEnergy;
import enhancedportals.portal.structure.STTransferFluid;
import enhancedportals.portal.structure.STTransferItem;

public class PortalStructureConstructor {
	ArrayList<ChunkCoordinates> PORTAL_COMPONENTS = new ArrayList<ChunkCoordinates>();
	ArrayList<STDiallingDevice> STRUCT_DIALLING = new ArrayList<STDiallingDevice>();
	ArrayList<STFrame> STRUCT_FRAME = new ArrayList<STFrame>();
	ArrayList<STNetworkInterface> STRUCT_NETWORK = new ArrayList<STNetworkInterface>();
	STPortalManipulator STRUCT_MANIP = null;
	ArrayList<STRedstoneInterface> STRUCT_REDSTONE = new ArrayList<STRedstoneInterface>();
	ArrayList<STTransferEnergy> STRUCT_TRANS_ENERGY = new ArrayList<STTransferEnergy>();
	ArrayList<STTransferFluid> STRUCT_TRANS_FLUID = new ArrayList<STTransferFluid>();
	ArrayList<STTransferItem> STRUCT_TRANS_ITEM = new ArrayList<STTransferItem>();
	ArrayList<ChunkCoordinates> STRUCT_PORTAL_LOC = new ArrayList<ChunkCoordinates>();
	int PORTAL_TYPE = -1;
	final int MAXIMUM_CHANCES = 40;

	public void construct(World world, STController controller) throws StructureConstructException {
		Queue<ChunkCoordinates> toProcess = new LinkedList<ChunkCoordinates>();
		STRUCT_PORTAL_LOC = getGhostedPortalBlocks(controller);

		if (STRUCT_PORTAL_LOC.isEmpty())
			throw new StructureConstructException("couldNotCreatePortalBlocks");

		toProcess.add(new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord));

		while (!toProcess.isEmpty()) {
			ChunkCoordinates c = toProcess.remove();
			
			if (!PORTAL_COMPONENTS.contains(c)) {
				TileEntity t = world.getTileEntity(c.posX, c.posY, c.posZ);
				
				if (STRUCT_PORTAL_LOC.contains(c) || t instanceof PortalStructureTileEntity) {
					if (t instanceof STDiallingDevice) {
						if (!STRUCT_NETWORK.isEmpty())
							throw new StructureConstructException("networkAndDial");
						
						STRUCT_DIALLING.add((STDiallingDevice) t);
					} else if (t instanceof STFrame) {
						STRUCT_FRAME.add((STFrame) t);
					} else if (t instanceof STNetworkInterface) {
						if (!STRUCT_DIALLING.isEmpty())
							throw new StructureConstructException("networkAndDial");
						
						STRUCT_NETWORK.add((STNetworkInterface) t);
					} else if (t instanceof STPortalManipulator) {
						if (STRUCT_MANIP != null)
							throw new StructureConstructException("multiplePortalManip");
						
						STRUCT_MANIP = (STPortalManipulator) t;
					} else if (t instanceof STRedstoneInterface) {
						STRUCT_REDSTONE.add((STRedstoneInterface) t);
					} else if (t instanceof STTransferEnergy) {
						STRUCT_TRANS_ENERGY.add((STTransferEnergy) t);
					} else if (t instanceof STTransferFluid) {
						STRUCT_TRANS_FLUID.add((STTransferFluid) t);
					} else if (t instanceof STTransferItem) {
						STRUCT_TRANS_ITEM.add((STTransferItem) t);
					}
					
					PORTAL_COMPONENTS.add(c);
					addNearbyBlocks(world, c, 0, toProcess);
				}
			}
		}
		
		if (PORTAL_COMPONENTS.isEmpty()) {
			throw new StructureConstructException("unknown");
		}
		
		controller.STRUCT_DIALLING = STRUCT_DIALLING.toArray(new STDiallingDevice[STRUCT_DIALLING.size()]);
		controller.STRUCT_FRAME = STRUCT_FRAME.toArray(new STFrame[STRUCT_FRAME.size()]);
		controller.STRUCT_NETWORK = STRUCT_NETWORK.toArray(new STNetworkInterface[STRUCT_NETWORK.size()]);
		controller.STRUCT_MANIP = STRUCT_MANIP;
		controller.STRUCT_REDSTONE = STRUCT_REDSTONE.toArray(new STRedstoneInterface[STRUCT_REDSTONE.size()]);
		controller.STRUCT_TRANS_ENERGY = STRUCT_TRANS_ENERGY.toArray(new STTransferEnergy[STRUCT_TRANS_ENERGY.size()]);
		controller.STRUCT_TRANS_FLUID = STRUCT_TRANS_FLUID.toArray(new STTransferFluid[STRUCT_TRANS_FLUID.size()]);
		controller.STRUCT_TRANS_ITEM = STRUCT_TRANS_ITEM.toArray(new STTransferItem[STRUCT_TRANS_ITEM.size()]);
		controller.STRUCT_PORTAL_LOC = STRUCT_PORTAL_LOC.toArray(new ChunkCoordinates[STRUCT_PORTAL_LOC.size()]);
		controller.PORTAL_TYPE = PORTAL_TYPE;
		
		System.out.println(".... Done!\nFrame: " + STRUCT_FRAME.size() + " Dialling: " + STRUCT_DIALLING.size() + " Network: " + STRUCT_NETWORK.size() + " Manip: " + (STRUCT_MANIP == null ? "No" : "Yes") + " Redstone: " + STRUCT_REDSTONE.size() + " TEnergy: " + STRUCT_TRANS_ENERGY.size() + " TFluid: " + STRUCT_TRANS_FLUID.size() + " TItem: " + STRUCT_TRANS_ITEM.size() + " Portal: " + STRUCT_PORTAL_LOC.size());
	}

	ArrayList<ChunkCoordinates> getGhostedPortalBlocks(STController controller) {
		for (int j = 0; j < 6; j++)
		{
			for (int i = 1; i < 4; i++)
			{
				ForgeDirection d = ForgeDirection.getOrientation(j);
				ChunkCoordinates c = new ChunkCoordinates(controller.xCoord + d.offsetX, controller.yCoord + d.offsetY, controller.zCoord + d.offsetZ);
				Queue<ChunkCoordinates> portalBlocks = getGhostedPortalBlocks(controller.getWorldObj(), c, i);

				if (!portalBlocks.isEmpty())
				{
					PORTAL_TYPE = i;
					return new ArrayList<ChunkCoordinates>(portalBlocks);
				}
			}
		}

		return new ArrayList<ChunkCoordinates>();
	}

	Queue<ChunkCoordinates> getGhostedPortalBlocks(World world, ChunkCoordinates start, int direction) {
		Queue<ChunkCoordinates> portalBlocks = new LinkedList<ChunkCoordinates>();
		Queue<ChunkCoordinates> toProcess = new LinkedList<ChunkCoordinates>();
		int chances = 0;
		toProcess.add(start);

		while (!toProcess.isEmpty())
		{
			ChunkCoordinates c = toProcess.remove();

			if (!portalBlocks.contains(c))
			{
				if (world.isAirBlock(c.posX, c.posY, c.posZ))
				{
					int sides = getGhostedSides(world, c, portalBlocks, direction);

					if (sides < 2)
					{
						if (chances < MAXIMUM_CHANCES)
						{
							chances++;
							sides += 2;
						}
						else
						{
							return new LinkedList<ChunkCoordinates>();
						}
					}

					if (sides >= 2)
					{
						portalBlocks.add(c);
						addNearbyBlocks(world, c, direction, toProcess);
					}
				}
				else if (!isPortalPart(world, c))
				{
					return new LinkedList<ChunkCoordinates>();
				}
			}
		}

		return portalBlocks;
	}

	int getGhostedSides(World world, ChunkCoordinates block, Queue<ChunkCoordinates> portalBlocks, int portalType)
	{
		int sides = 0;
		Queue<ChunkCoordinates> neighbors = new LinkedList<ChunkCoordinates>();
		addNearbyBlocks(world, block, portalType, neighbors);

		for (ChunkCoordinates c : neighbors)
		{
			if (portalBlocks.contains(c) || isPortalPart(world, c))
			{
				sides++;
			}
		}

		return sides;
	}

	boolean isPortalPart(World world, ChunkCoordinates c)
	{
		TileEntity tile = world.getTileEntity(c.posX, c.posY, c.posZ);
		return tile != null && tile instanceof PortalStructureTileEntity;
	}

	void addNearbyBlocks(World world, ChunkCoordinates w, int portalDirection, Queue<ChunkCoordinates> q)
	{
		for (int i = 0; i < 6; i++) {
			if (portalDirection == 1 && (i == 2 || i == 3)) 
				continue;
			else if (portalDirection == 2 && (i == 4 || i == 5)) 
				continue;
			else if (portalDirection == 3 && (i == 0 || i == 1)) 
				continue;

			ForgeDirection d = ForgeDirection.getOrientation(i);
			q.add(new ChunkCoordinates(w.posX + d.offsetX, w.posY + d.offsetY, w.posZ + d.offsetZ));
		}
	}
}
