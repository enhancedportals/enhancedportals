package enhanced.portals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.portal.frame.TileDialingDevice;

public class ContainerDialingEditIdentifier extends BaseContainer {
    TileDialingDevice dial;

    public ContainerDialingEditIdentifier(TileDialingDevice d, InventoryPlayer p) {
        super(null, p);
        dial = d;
        hideInventorySlots();
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {

    }
}
