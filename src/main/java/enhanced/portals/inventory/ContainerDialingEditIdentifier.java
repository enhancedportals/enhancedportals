package enhanced.portals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.tile.TileDialingDevice;

public class ContainerDialingEditIdentifier extends BaseContainer {
    TileDialingDevice dial;

    public ContainerDialingEditIdentifier(TileDialingDevice d, InventoryPlayer p) {
        super(null, p);
        dial = d;
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {

    }
}
