package enhanced.portals.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.elements.ElementFluid;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.inventory.ContainerTransferFluid;
import enhanced.portals.tile.TileTransferFluid;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;

public class GuiTransferFluid extends BaseGui {
    public static final int CONTAINER_SIZE = 75;
    TileTransferFluid fluid;

    public GuiTransferFluid(TileTransferFluid f, EntityPlayer p) {
        super(new ContainerTransferFluid(f, p.inventory), CONTAINER_SIZE);
        name = Localisation.get(EPMod.ID, Locale.GUI_TRANSFER_FLUID);
        fluid = f;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, guiLeft + 7, guiTop + 49, 140, 20, Localisation.get(EPMod.ID, fluid.isSending ? Locale.GUI_SENDING : Locale.GUI_RECEIVING)));
        addElement(new ElementFluid(this, xSize - 25, 7, fluid.tank));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(new NBTTagCompound()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ((GuiButton) buttonList.get(0)).displayString = Localisation.get(EPMod.ID, fluid.isSending ? Locale.GUI_SENDING : Locale.GUI_RECEIVING);
    }
}
