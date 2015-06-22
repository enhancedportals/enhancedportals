package enhanced.portals.client.gui;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementGlyphDisplay;
import enhanced.portals.inventory.ContainerNetworkInterface;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.tile.TileController;

public class GuiNetworkInterface extends BaseGui {
    public static final int CONTAINER_SIZE = 68;
    TileController controller;
    ElementGlyphDisplay display;

    public GuiNetworkInterface(TileController c, EntityPlayer p) {
        super(new ContainerNetworkInterface(c, p.inventory), CONTAINER_SIZE);
        controller = c;
        name = Localization.get(EnhancedPortals.MOD_ID, "gui.networkInterface");
        setHidePlayerInventory();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (x >= guiLeft + 7 && x <= guiLeft + 169 && y >= guiTop + 29 && y < guiTop + 47)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, GuiHandler.NETWORK_INTERFACE_B));
    }

    @Override
    public void initGui() {
        super.initGui();
        display = new ElementGlyphDisplay(this, 7, 29, controller.getIdentifierNetwork());
        addElement(display);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        display.setIdentifier(controller.getIdentifierNetwork());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.networkIdentifier"), 7, 19, 0x404040);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.networkedPortals"), 7, 52, 0x404040);
        String s = controller.connectedPortals == -1 ? Localization.get(EnhancedPortals.MOD_ID, "gui.notSet") : "" + controller.connectedPortals;
        getFontRenderer().drawString(s, xSize - getFontRenderer().getStringWidth(s) - 7, 52, 0x404040);

        if (x >= guiLeft + 7 && x <= guiLeft + 169 && y >= guiTop + 29 && y < guiTop + 47)
            drawHoveringText(Arrays.asList(new String[] { Localization.get(EnhancedPortals.MOD_ID, "gui.clickToModify") }), x - guiLeft, y - guiTop, getFontRenderer());
    }
}
