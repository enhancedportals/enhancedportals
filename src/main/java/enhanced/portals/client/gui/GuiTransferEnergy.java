package enhanced.portals.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.elements.ElementRedstoneFlux;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.inventory.ContainerTransferEnergy;
import enhanced.portals.network.packet.PacketGuiData;
import enhanced.portals.tile.TileTransferEnergy;

public class GuiTransferEnergy extends BaseGui {
    public static final int CONTAINER_SIZE = 55;
    TileTransferEnergy energy;

    public GuiTransferEnergy(TileTransferEnergy e, EntityPlayer p) {
        super(new ContainerTransferEnergy(e, p.inventory), CONTAINER_SIZE);
        name = "gui.transferEnergy";
        energy = e;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, guiLeft + 7, guiTop + 29, 140, 20, Localization.get(EnhancedPortals.MOD_ID, "gui." + (energy.isSending ? "sending" : "receiving"))));
        addElement(new ElementRedstoneFlux(this, xSize - 21, 7, energy.storage));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(new NBTTagCompound()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ((GuiButton) buttonList.get(0)).displayString = Localization.get(EnhancedPortals.MOD_ID, "gui." + (energy.isSending ? "sending" : "receiving"));
    }
}
