package enhanced.portals.client.gui;

import java.util.Arrays;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.elements.ElementRedstoneFlux;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.client.gui.elements.ElementGlyphDisplay;
import enhanced.portals.inventory.ContainerPortalController;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.portal.controller.TileController;
import net.minecraft.entity.player.EntityPlayer;

public class GuiPortalController extends BaseGui {
    public static final int CONTAINER_SIZE = 66; //55;
    TileController controller;
    ElementGlyphDisplay display;

    public GuiPortalController(TileController c, EntityPlayer p) {
        super(new ContainerPortalController(c, p.inventory), CONTAINER_SIZE);
        xSize += 18;
        controller = c;
        name = Localisation.get(EPMod.ID, Locale.GUI_PORTAL_CONTROLLER);
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();
        display = new ElementGlyphDisplay(this, 7, 41, controller.uID);
        addElement(display);
        addElement(new ElementRedstoneFlux(this, xSize - 21, ySize - 49, controller.getEnergyStorage()));
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 41 && y < guiTop + 59)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, EPGuis.PORTAL_CONTROLLER_B));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_UNIQUE_IDENTIFIER), 7, 30, 0x404040);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 41 && y < guiTop + 59)
            drawHoveringText(Arrays.asList(new String[] { Localisation.get(EPMod.ID, Locale.GUI_CLICK_TO_MODIFY) }), x - guiLeft, y - guiTop, getFontRenderer());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        display.setIdentifier(controller.uID);
    }
}
