package enhanced.portals.client.gui;

import java.util.Arrays;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.client.gui.elements.ElementGlyphDisplay;
import enhanced.portals.inventory.ContainerNetworkInterface;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.portal.controller.TileController;
import net.minecraft.entity.player.EntityPlayer;

public class GuiNetworkInterface extends BaseGui {
    public static final int CONTAINER_SIZE = 68;
    TileController controller;
    ElementGlyphDisplay display;

    public GuiNetworkInterface(TileController c, EntityPlayer p) {
        super(new ContainerNetworkInterface(c, p.inventory), CONTAINER_SIZE);
        controller = c;
        name = Localisation.get(EPMod.ID, Locale.GUI_NETWORK_INTERFACE);
        setHidePlayerInventory();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (x >= guiLeft + 7 && x <= guiLeft + 169 && y >= guiTop + 29 && y < guiTop + 47)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, EPGuis.NETWORK_INTERFACE_B));
    }

    @Override
    public void initGui() {
        super.initGui();
        display = new ElementGlyphDisplay(this, 7, 29, controller.nID);
        addElement(display);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        display.setIdentifier(controller.nID);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_NETWORK_IDENTIFIER), 7, 19, 0x404040);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_NETWORKED_PORTALS), 7, 52, 0x404040);
        String s = controller.connectedPortals == -1 ? Localisation.get(EPMod.ID, Locale.GUI_NOT_SET) : "" + controller.connectedPortals;
        getFontRenderer().drawString(s, xSize - getFontRenderer().getStringWidth(s) - 7, 52, 0x404040);

        if (x >= guiLeft + 7 && x <= guiLeft + 169 && y >= guiTop + 29 && y < guiTop + 47)
            drawHoveringText(Arrays.asList(new String[] { Localisation.get(EPMod.ID, Locale.GUI_CLICK_TO_MODIFY) }), x - guiLeft, y - guiTop, getFontRenderer());
    }
}
