package enhanced.portals.inventory;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.client.gui.GuiTransferEnergy;
import enhanced.portals.portal.transfer.TileTransferEnergy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerTransferEnergy extends BaseContainer {
    TileTransferEnergy energy;
    byte wasSending = -1;
    int lastEnergy = -1;

    public ContainerTransferEnergy(TileTransferEnergy e, InventoryPlayer p) {
        super(null, p, GuiTransferEnergy.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        energy = e;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        byte isSending = (byte) (energy.isSending ? 1 : 0);
        int en = energy.storage.getEnergyStored();

        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (wasSending != isSending)
                icrafting.sendProgressBarUpdate(this, 0, isSending);

            if (lastEnergy != en)
                icrafting.sendProgressBarUpdate(this, 1, en);

            wasSending = isSending;
            lastEnergy = en;
        }
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        energy.isSending = !energy.isSending;
    }

    @Override
    public void updateProgressBar(int id, int val) {
        if (id == 0)
            energy.isSending = val == 1;
        else if (id == 1)
            energy.storage.setEnergyStored(val);
    }
}
