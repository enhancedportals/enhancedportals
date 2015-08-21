package enhanced.portals.portal.transfer;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.elements.ElementRedstoneFlux;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiTransferEnergy extends BaseGui {
    public static final int CONTAINER_SIZE = 55;
    TileTransferEnergy energy;

    public GuiTransferEnergy(TileTransferEnergy e, EntityPlayer p) {
        super(new ContainerTransferEnergy(e, p.inventory), CONTAINER_SIZE);
        name = Localisation.get(EPMod.ID, Locale.GUI_TRANSFER_ENERGY);
        energy = e;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, guiLeft + 7, guiTop + 29, 140, 20, Localisation.get(EPMod.ID, energy.isSending ? Locale.GUI_SENDING : Locale.GUI_RECEIVING)));
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
        ((GuiButton) buttonList.get(0)).displayString = Localisation.get(EPMod.ID, energy.isSending ? Locale.GUI_SENDING : Locale.GUI_RECEIVING);
    }
}
