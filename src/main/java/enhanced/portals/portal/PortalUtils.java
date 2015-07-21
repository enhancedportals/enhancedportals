package enhanced.portals.portal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import enhanced.base.utilities.BlockPos;
import enhanced.base.utilities.WorldUtilities;
import enhanced.portals.Reference.Locale;
import enhanced.portals.portal.frame.TileController;
import enhanced.portals.portal.frame.TileDialingDevice;
import enhanced.portals.portal.frame.TileNetworkInterface;
import enhanced.portals.portal.frame.TilePortalManipulator;

public class PortalUtils {
    static final int MAXIMUM_CHANCES = 40;

    /***
     * Adds all the touching blocks to the processing queue.
     */
    static void addNearbyBlocks(World world, BlockPos w, int portalDirection, Queue<BlockPos> q) {
        // (world controller is in, the offset block from controller, 1-5, blank linkedList neighbors)
        // if portalDirection = 1, then add up, down, west, east
        // if portalDirection = 2, then add up, down, north, south
        // if portalDirection = 3, then add north, south, west, east
        // if portalDirection = 4, then add up, down, north-east, south-west
        // if portalDirection = 5, then add up, down, north-west, south-east
        //
        if (portalDirection == 4) {
            q.add(BlockPos.offset(w, 0, 1, 0)); // Up
            q.add(BlockPos.offset(w, 0, -1, 0)); // Down

            q.add(BlockPos.offset(w, 1, 0, -1)); // North-East
            q.add(BlockPos.offset(w, -1, 0, 1)); // South-West
            //
        } else if (portalDirection == 5) {
            q.add(BlockPos.offset(w, 0, 1, 0)); // Up
            q.add(BlockPos.offset(w, 0, -1, 0)); // Down

            q.add(BlockPos.offset(w, -1, 0, -1)); // North-West
            q.add(BlockPos.offset(w, 1, 0, 1)); // South-East
        } else
            for (int i = 0; i < 6; i++) { // Loop through the different directions for portalDirection 1-3.
                if (portalDirection == 1 && (i == 2 || i == 3)) // Skip North (2) and South (3)
                    continue;
                
                else if (portalDirection == 2 && (i == 4 || i == 5)) // Skip West (4) and East (5)
                    continue;
                
                else if (portalDirection == 3 && (i == 0 || i == 1)) // Skip Up (0) and Down (1)
                    continue;

                q.add(BlockPos.offset(w, ForgeDirection.getOrientation(i)));
            }
    }

    public static ArrayList<BlockPos> getAllPortalComponents(TileController controller) throws PortalException {
        ArrayList<BlockPos> portalComponents = new ArrayList<BlockPos>();
        Queue<BlockPos> toProcess = new LinkedList<BlockPos>();
        Queue<BlockPos> portalBlocks = getGhostedPortalBlocks(controller);
        World world = controller.getWorldObj();
        toProcess.add(controller.getBlockPos());

        if (portalBlocks.isEmpty())
            throw new PortalException(Locale.CHAT_ERROR_COULD_NOT_CREATE_PORTAL);

        boolean mod = false, dialler = false, network = false;

        while (!toProcess.isEmpty()) {
            BlockPos c = toProcess.remove();

            if (!portalComponents.contains(c)) {
                TileEntity t = WorldUtilities.getTileEntity(world, c);

                if (portalBlocks.contains(c) || t instanceof TilePortalPart) {
                    if (t instanceof TileNetworkInterface) {
                        if (dialler)
                            throw new PortalException(Locale.CHAT_ERROR_DIAL_AND_NETWORK);

                        network = true;
                    } else if (t instanceof TileDialingDevice) {
                        if (network)
                            throw new PortalException(Locale.CHAT_ERROR_DIAL_AND_NETWORK);

                        dialler = true;
                    } else if (t instanceof TilePortalManipulator)
                        if (!mod)
                            mod = true;
                        else
                            throw new PortalException(Locale.CHAT_ERROR_MULTIPLE_MANIPULATORS);

                    portalComponents.add(c);
                    addNearbyBlocks(world, c, 0, toProcess);

                    if (controller.portalType >= 4)
                        addNearbyBlocks(world, c, controller.portalType, toProcess); // Adds diagonals for those that require it
                }
            }
        }

        if (portalComponents.isEmpty())
            throw new PortalException("unknown");

        return portalComponents;
    }

    static Queue<BlockPos> getGhostedPortalBlocks(TileController controller) {
        for (int j = 0; j < 6; j++)
            for (int i = 1; i < 6; i++) {
                // Forge directions: Down, Up, North, South, West, East
                // Get Controller and cycle through forge directions from the coord.
                BlockPos c = BlockPos.offset(controller.getBlockPos(), ForgeDirection.getOrientation(j));
                // portalBlocks = (the world controller is in, the offset from the controller we're exploring, 1-5)
                Queue<BlockPos> portalBlocks = getGhostedPortalBlocks(controller.getWorldObj(), c, i);

                if (!portalBlocks.isEmpty()) {
                    controller.portalType = i;
                    return portalBlocks;
                }
            }

        return new LinkedList<BlockPos>();
    }

    static Queue<BlockPos> getGhostedPortalBlocks(World world, BlockPos start, int portalType) {
        Queue<BlockPos> portalBlocks = new LinkedList<BlockPos>();
        Queue<BlockPos> toProcess = new LinkedList<BlockPos>();
        int chances = 0;
        // Start is the offset block from the controller.
        toProcess.add(start);

        while (!toProcess.isEmpty()) {
            // c is now the offset block (start).
            BlockPos c = toProcess.remove();
            // Pass as long as portalBlocks does not already contain the offset block from the controller.
            if (!portalBlocks.contains(c)) // Check if the coords of the offset block happens to be an air block.
                if (WorldUtilities.isAirBlock(world, c)) {
                    // sides = (world that the controller is in, current list of portalBlocks, 1-5)
                    // Returns the number of portal frame blocks and items already in portalBlocks.
                    int sides = getGhostedSides(world, c, portalBlocks, portalType);

                    if (sides < 2)
                        if (chances < MAXIMUM_CHANCES) {
                            chances++;
                            sides += 2;
                        } else
                            return new LinkedList<BlockPos>();

                    if (sides >= 2) {
                        portalBlocks.add(c);
                        addNearbyBlocks(world, c, portalType, toProcess);
                    }
                } else if (!isPortalPart(world, c))
                    return new LinkedList<BlockPos>();
        }

        return portalBlocks;
    }

    static int getGhostedSides(World world, BlockPos block, Queue<BlockPos> portalBlocks, int portalType) {
        int sides = 0;
        Queue<BlockPos> neighbors = new LinkedList<BlockPos>();
        // (world controller is in, the offset block from controller, 1-5, blank linkedList neighbors)
        // if portalDirection = 1, then add up, down, west, east
        // if portalDirection = 2, then add up, down, north, south
        // if portalDirection = 3, then add north, south, west, east
        // if portalDirection = 4, then add up, down, north-east, south-west
        // if portalDirection = 5, then add up, down, north-west, south-east
        addNearbyBlocks(world, block, portalType, neighbors);

        // Go through all neighbor blocks.
        for (BlockPos c : neighbors)
            if (portalBlocks.contains(c) || isPortalPart(world, c))
                sides++;

        return sides;
    }

    static boolean isPortalPart(World world, BlockPos c) {
        TileEntity tile = WorldUtilities.getTileEntity(world, c);
        return tile != null && tile instanceof TilePortalPart;
    }
}
