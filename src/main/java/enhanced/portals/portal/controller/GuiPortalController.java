package enhanced.portals.portal.controller;

import java.util.Arrays;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.button.GuiButtonSmall;
import enhanced.base.client.gui.elements.ElementRedstoneFlux;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.network.packet.PacketRequestGui;
import net.minecraft.client.gui.GuiButton;
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
        display = new ElementGlyphDisplay(this, 7, 29, controller.uID);
        addElement(display);
        addElement(new ElementRedstoneFlux(this, xSize - 21, ySize - 49, controller.getEnergyStorage()));
        buttonList.add(new GuiButtonSmall(0, guiLeft + 148, guiTop + 49, 10, 10, "+"));
        buttonList.add(new GuiButtonSmall(1, guiLeft + 159, guiTop + 49, 10, 10, "-"));
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 29 && y < guiTop + 47)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, EPGuis.PORTAL_CONTROLLER_B));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_UNIQUE_IDENTIFIER), 7, 18, 0x404040);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_FORCED_INSTABILITY + "." + controller.instability), 7, 50, 0x808080);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 29 && y < guiTop + 47)
            drawHoveringText(Arrays.asList(new String[] { Localisation.get(EPMod.ID, Locale.GUI_CLICK_TO_MODIFY) }), x - guiLeft, y - guiTop, getFontRenderer());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        display.setIdentifier(controller.uID);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        
    }
}
