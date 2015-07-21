package enhanced.portals.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.inventory.ContainerTransferItem;
import enhanced.portals.portal.frame.TileTransferItem;

public class GuiTransferItem extends BaseGui {
    public static final int CONTAINER_SIZE = 47;
    TileTransferItem item;

    public GuiTransferItem(TileTransferItem i, EntityPlayer p) {
        super(new ContainerTransferItem(i, p.inventory), CONTAINER_SIZE);
        name = Localisation.get(EPMod.ID, Locale.GUI_TRANSFER_ITEM);
        item = i;
    }

    @Override
    protected void drawBackgroundTexture() {
        super.drawBackgroundTexture();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(playerInventoryTexture);
        drawTexturedModalRect(guiLeft + xSize - 18 - 7, guiTop + 22, 7, 7, 18, 18);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(new NBTTagCompound()));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, guiLeft + 7, guiTop + 21, 140, 20, Localisation.get(EPMod.ID, item.isSending ? Locale.GUI_SENDING : Locale.GUI_RECEIVING)));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ((GuiButton) buttonList.get(0)).displayString = Localisation.get(EPMod.ID, item.isSending ? Locale.GUI_SENDING : Locale.GUI_RECEIVING);
    }
}
