package enhanced.portals.item.manual;

import enhanced.base.inventory.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerManual extends BaseContainer {
    public ContainerManual(InventoryPlayer p) {
        super(null, p);
        hideInventorySlots();
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {

    }
}
