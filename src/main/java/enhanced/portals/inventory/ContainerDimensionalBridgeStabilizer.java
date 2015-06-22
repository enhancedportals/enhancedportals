package enhanced.portals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.GuiDimensionalBridgeStabilizer;
import enhanced.portals.inventory.slot.SlotDBS;
import enhanced.portals.network.packet.PacketGuiData;
import enhanced.portals.tile.TileStabilizerMain;

public class ContainerDimensionalBridgeStabilizer extends BaseContainer {
    int lastPower = 0, lastMaxPower = 0, lastPortals = -1, lastInstability = 0, lastPowerState = -1;
    TileStabilizerMain stabilizer;

    public ContainerDimensionalBridgeStabilizer(TileStabilizerMain s, InventoryPlayer p) {
        super(s, p, GuiDimensionalBridgeStabilizer.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        stabilizer = s;
        addSlotToContainer(new SlotDBS(s, 0, 152, GuiDimensionalBridgeStabilizer.CONTAINER_SIZE - 25));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int currentPower = stabilizer.getEnergyStorage().getEnergyStored();
        int currentMaxPower = stabilizer.getEnergyStorage().getMaxEnergyStored();
        int currentPortals = stabilizer.getActiveConnections();
        int currentInstability = stabilizer.instability;
        int currentPowerState = stabilizer.powerState;

        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (lastPower != currentPower || lastMaxPower != currentMaxPower) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("energy", currentPower);
                tag.setInteger("max", currentMaxPower);
                EnhancedPortals.instance.packetPipeline.sendTo(new PacketGuiData(tag), (EntityPlayerMP) icrafting);
            }

            if (lastPortals != currentPortals)
                icrafting.sendProgressBarUpdate(this, 2, currentPortals);

            if (lastInstability != currentInstability)
                icrafting.sendProgressBarUpdate(this, 3, currentInstability);

            if (lastPowerState != currentPowerState)
                icrafting.sendProgressBarUpdate(this, 4, currentPowerState);
        }

        lastPower = currentPower;
        lastMaxPower = currentMaxPower;
        lastPortals = currentPortals;
        lastInstability = currentInstability;
        lastPowerState = currentPowerState;
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("increase_powerState")) {
            if (stabilizer.powerState < 3)
                stabilizer.powerState++;
        } else if (tag.hasKey("decrease_powerState")) {
            if (stabilizer.powerState > 0)
                stabilizer.powerState--;
        } else if (tag.hasKey("energy") && tag.hasKey("max")) {
            stabilizer.getEnergyStorage().setCapacity(tag.getInteger("max"));
            stabilizer.getEnergyStorage().setEnergyStored(tag.getInteger("energy"));
        }
    }

    @Override
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 2)
            stabilizer.intActiveConnections = par2;
        else if (par1 == 3)
            stabilizer.instability = par2;
        else if (par1 == 4)
            stabilizer.powerState = par2;
    }
}
