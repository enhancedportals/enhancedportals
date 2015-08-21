package enhanced.portals.portal.manipulator;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerPortalManipulator extends BaseContainer {
    TilePortalManipulator module;

    // Called when accessing the Module Manipulator in-game.
    public ContainerPortalManipulator(TilePortalManipulator m, InventoryPlayer p) {
        super(m, p, GuiPortalManipulator.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        module = m;

        for (int i = 0; i < 9; i++)
            addSlotToContainer(new SlotPortalModule(module, i, 8 + i * 18, GuiPortalManipulator.CONTAINER_SIZE - 24));
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {

    }
}
