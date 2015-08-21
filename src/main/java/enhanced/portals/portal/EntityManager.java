package enhanced.portals.portal;

import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.common.FMLCommonHandler;
import enhanced.base.utilities.BlockPos;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.Reference.Locale;
import enhanced.portals.Reference.PortalModules;
import enhanced.portals.portal.controller.TileController;
import enhanced.portals.portal.manipulator.TilePortalManipulator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityManager {
    static final int PLAYER_COOLDOWN_RATE = 10;

    static float getRotation(Entity entity, TileController controller, BlockPos loc) {
        World world = controller.getWorldObj();
        TilePortalManipulator module = controller.getPortalManipulator();

        if (module != null) {
            ItemStack s = module.getModule(PortalModules.FACING);

            if (s != null) {
                NBTTagCompound tag = s.getTagCompound();
                int facing = 0;

                if (tag != null)
                    facing = tag.getInteger("facing");

                return facing * 90F - 180F;
            }
        }

        if (controller.portalType == 1) {
            if (world.isSideSolid(loc.getX(), loc.getY(), loc.getZ() + 1, ForgeDirection.NORTH))
                return 180f;

            return 0f;
        } else if (controller.portalType == 2) {
            if (world.isSideSolid(loc.getX() - 1, loc.getY(), loc.getZ(), ForgeDirection.EAST))
                return -90f;

            return 90f;
        } else if (controller.portalType == 4) {
            if (world.isBlockNormalCubeDefault(loc.getX() + 1, loc.getY(), loc.getZ() + 1, true))
                return 135f;

            return -45f;
        } else if (controller.portalType == 5) {
            if (world.isBlockNormalCubeDefault(loc.getX() - 1, loc.getY(), loc.getZ() + 1, true))
                return -135f;

            return 45f;
        }

        return entity.rotationYaw;
    }

    static void handleMomentum(Entity entity, int touchedPortalType, int exitPortalType, float exitYaw, boolean keepMomentum) {
        if (!keepMomentum) {
            entity.motionX = entity.motionY = entity.motionZ = 0;
            return;
        } else if (touchedPortalType == 1) {
            if (exitPortalType == 2) {
                double temp = entity.motionZ;
                entity.motionZ = entity.motionX;
                entity.motionX = exitYaw == -90 ? -temp : temp;
            } else if (exitPortalType == 3) {
                double temp = entity.motionZ;
                entity.motionZ = entity.motionY;
                entity.motionY = temp;
            } else if (exitPortalType == 4) {
                double temp = entity.motionZ;
                entity.motionZ = entity.motionY;
                entity.motionY = -temp;
            }
        } else if (touchedPortalType == 2) {
            if (exitPortalType == 1) {
                double temp = entity.motionZ;
                entity.motionZ = entity.motionX;
                entity.motionX = exitYaw == 0 ? -temp : temp;
            } else if (exitPortalType == 3) {
                double temp = entity.motionX;
                entity.motionX = entity.motionY;
                entity.motionY = temp;
            } else if (exitPortalType == 4) {
                double temp = entity.motionX;
                entity.motionX = entity.motionY;
                entity.motionY = -temp;
            }
        } else if (touchedPortalType == 3 || touchedPortalType == 4)
            if (exitPortalType == 1) {
                double temp = entity.motionY;
                entity.motionY = entity.motionZ;
                entity.motionZ = exitYaw == 0 ? -temp : temp;
            } else if (exitPortalType == 2) {
                double temp = entity.motionY;
                entity.motionY = entity.motionX;
                entity.motionX = exitYaw == -90 ? -temp : temp;
            } else if (exitPortalType == 3)
                entity.motionY = touchedPortalType == 3 ? -entity.motionY : entity.motionY;

        entity.velocityChanged = true;
    }

    public static boolean isEntityFitForTravel(Entity entity) {
        return entity != null && entity.timeUntilPortal == 0;
    }

    public static void setEntityPortalCooldown(Entity entity) {
        if (entity == null)
            return;

        if (entity instanceof EntityPlayer || entity instanceof EntityMinecart || entity instanceof EntityBoat || entity instanceof EntityHorse)
            entity.timeUntilPortal = entity.timeUntilPortal == -1 ? 0 : PLAYER_COOLDOWN_RATE;
        else
            entity.timeUntilPortal = entity.timeUntilPortal == -1 ? 0 : 300; // Reduced to 300 ticks from 900.
    }

    public static Entity teleportEntity(Entity entity, TileController entrance, TileController exit) throws PortalException {
        if (entity == null || !isEntityFitForTravel(entity))
            return entity;

        while (entity.ridingEntity != null) // Go all the way down and get the bottom entity of the stack
            entity = entity.ridingEntity;

        Entity rider = entity.riddenByEntity;

        if (rider != null) { // If we have a rider, dismount it and then teleport it first
            rider.mountEntity(null);
            rider = teleportEntity(rider, entrance, exit);
        }

        entity = teleportEntity(entrance, exit, entity);

        if (rider != null) // Remount the entities
            rider.mountEntity(entity);

        setEntityPortalCooldown(entity);
        return entity;
    }

    static Entity teleportEntity(TileController start, TileController end, Entity entity) throws PortalException {
        TilePortalManipulator exitManip = end.getPortalManipulator();
        BlockPos exitLoc = getExitLocation(entity, end);

        if (exitLoc == null)
            throw new PortalException(Locale.CHAT_ERROR_FAILED_TO_TRANSFER);
        
        if (entity instanceof EntityPlayer)
            return teleportPlayerEntity(entity, start, end, exitManip != null && exitManip.hasModule(PortalModules.MOMENTUM), exitLoc, getRotation(entity, end, exitLoc));
        
        return teleportNonPlayerEntity(entity, start, end, exitManip != null && exitManip.hasModule(PortalModules.MOMENTUM), exitLoc, getRotation(entity, end, exitLoc));
    }

    static BlockPos getExitLocation(Entity entity, TileController exit) throws PortalException {
        ArrayList<BlockPos> portals = exit.portalBlocks;
        AxisAlignedBB entityBoundingBox = AxisAlignedBB.getBoundingBox(0, 0, 0, entity.width, entity.yOffset + entity.ySize, entity.width);
        
        for (BlockPos p : portals) {
            if (isValidForSpawn(exit.getWorldObj(), entityBoundingBox.copy().offset(p.getX(), p.getY(), p.getZ()))) {
                return p;
            }
        }

        return null;
    }
    
    static Entity teleportPlayerEntity(Entity entity, TileController start, TileController end, boolean keepMomentum, BlockPos exitLoc, float yaw) {
        WorldServer startWorld = (WorldServer) start.getWorldObj(), endWorld = (WorldServer) end.getWorldObj();
        boolean dimensionalTransport = startWorld.provider.dimensionId != endWorld.provider.dimensionId;
        EntityPlayerMP player = (EntityPlayerMP) entity;
        ServerConfigurationManager config = null;
        double exitX = exitLoc.getX() + (end.portalType == 1 || end.portalType == 3 ? entity.width < 1 ? 0.5 : entity.width / 2 : 0.5);
        double exitY = exitLoc.getY();
        double exitZ = exitLoc.getZ() + (end.portalType == 2 || end.portalType == 3 ? entity.width < 1 ? 0.5 : entity.width / 2 : 0.5);
        
        player.closeScreen();
        
        if (dimensionalTransport) {
            config = player.mcServer.getConfigurationManager();
            player.dimension = endWorld.provider.dimensionId;
            player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, endWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
        
            startWorld.removeEntity(player);
            player.isDead = false;
            player.setLocationAndAngles(exitX, exitY, exitZ, yaw, player.rotationPitch);
            handleMomentum(player, start.portalType, end.portalType, yaw, keepMomentum);
            endWorld.spawnEntityInWorld(player);
            player.setWorld(endWorld);

            config.func_72375_a(player, startWorld);
            player.playerNetServerHandler.setPlayerLocation(exitX, exitY, exitZ, yaw, entity.rotationPitch);
            player.theItemInWorldManager.setWorld(endWorld);

            config.updateTimeAndWeatherForPlayer(player, endWorld);
            config.syncPlayerInventory(player);

            player.worldObj.theProfiler.endSection();
            startWorld.resetUpdateEntityTick();
            endWorld.resetUpdateEntityTick();
            player.worldObj.theProfiler.endSection();

            for (Iterator<PotionEffect> potion = player.getActivePotionEffects().iterator(); potion.hasNext();)
                player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potion.next()));

            player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));

            FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, startWorld.provider.dimensionId, player.dimension);
        } else {
            player.rotationYaw = yaw;
            player.setPositionAndUpdate(exitX, exitY, exitZ);
            handleMomentum(player, start.portalType, end.portalType, yaw, keepMomentum);
            player.worldObj.updateEntityWithOptionalForce(player, false);
        }
        
        return player;
    }

    static Entity teleportNonPlayerEntity(Entity entity, TileController start, TileController end, boolean keepMomentum, BlockPos exitLoc, float yaw) {
        boolean dimensionalTransport = start.getWorldObj().provider.dimensionId != end.getWorldObj().provider.dimensionId;
        WorldServer theWorld = (WorldServer) (dimensionalTransport ? end.getWorldObj() : start.getWorldObj());
        double exitX = exitLoc.getX() + (end.portalType == 1 || end.portalType == 3 ? entity.width < 1 ? 0.5 : entity.width / 2 : 0.5);
        double exitY = exitLoc.getY();
        double exitZ = exitLoc.getZ() + (end.portalType == 2 || end.portalType == 3 ? entity.width < 1 ? 0.5 : entity.width / 2 : 0.5);

        NBTTagCompound tag = new NBTTagCompound();
        entity.writeToNBTOptional(tag);

        if (entity instanceof IInventory) { // Clear the inventory (so these items don't get dropped on the floor when the entity dies)
            IInventory entityInventory = (IInventory) entity;

            for (int i = 0; i < entityInventory.getSizeInventory(); i++)
                entityInventory.setInventorySlotContents(i, null);
        }

        entity.setDead(); // Delete the entity. Will be taken care of next tick.
        Entity newEntity = EntityList.createEntityFromNBT(tag, theWorld); // Create new entity.

        if (newEntity != null) { // Set position, rotation and momentum of new entity at the other portal.
            handleMomentum(newEntity, start.portalType, end.portalType, yaw, keepMomentum);
            newEntity.setLocationAndAngles(exitX, exitY, exitZ, yaw, entity.rotationPitch);
            newEntity.forceSpawn = true;
            theWorld.spawnEntityInWorld(newEntity);
            newEntity.setWorld(theWorld);
            setEntityPortalCooldown(newEntity);
        }

        if (dimensionalTransport) // If we're sending the entity to the new world, do this on the old world too.
            ((WorldServer) start.getWorldObj()).resetUpdateEntityTick();

        theWorld.resetUpdateEntityTick();
        return newEntity;
    }

    static boolean isValidForSpawn(World world, AxisAlignedBB box) {
        int i = MathHelper.floor_double(box.minX);
        int j = MathHelper.floor_double(box.maxX + 1.0D);
        int k = MathHelper.floor_double(box.minY);
        int l = MathHelper.floor_double(box.maxY + 1.0D);
        int i1 = MathHelper.floor_double(box.minZ);
        int j1 = MathHelper.floor_double(box.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    if (!world.isAirBlock(k1, l1, i2) && world.getBlock(k1, l1, i2) != EPBlocks.portal) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
}
