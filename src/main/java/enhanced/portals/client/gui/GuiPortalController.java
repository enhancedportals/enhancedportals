package enhanced.portals.client.gui;

import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.tabs.TabTip;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementGlyphDisplay;
import enhanced.portals.inventory.ContainerPortalController;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.packet.PacketGuiData;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.tile.TileController;

public class GuiPortalController extends BaseGui {
    public static final int CONTAINER_SIZE = 78;
    TileController controller;
    GuiButton buttonLock;
    ElementGlyphDisplay display;

    public GuiPortalController(TileController c, EntityPlayer p) {
        super(new ContainerPortalController(c, p.inventory), CONTAINER_SIZE);
        controller = c;
        name = "gui.portalController";
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonLock = new GuiButton(10, guiLeft + 7, guiTop + containerSize - 27, 162, 20, Localization.get(EnhancedPortals.MOD_ID, "gui." + (controller.isPublic ? "public" : "private")));
        buttonList.add(buttonLock);
        display = new ElementGlyphDisplay(this, 7, 29, controller.getIdentifierUnique());
        addElement(display);
        addTab(new TabTip(this, "privatePublic", EnhancedPortals.MOD_ID));
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 32 && y < guiTop + 47)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, GuiHandler.PORTAL_CONTROLLER_B));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == buttonLock.id) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("public", true);
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.uniqueIdentifier"), 7, 19, 0x404040);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 32 && y < guiTop + 47)
            drawHoveringText(Arrays.asList(new String[] { Localization.get(EnhancedPortals.MOD_ID, "gui.clickToModify") }), x - guiLeft, y - guiTop, getFontRenderer());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        buttonLock.displayString = Localization.get(EnhancedPortals.MOD_ID, "gui." + (controller.isPublic ? "public" : "private"));
        display.setIdentifier(controller.getIdentifierUnique());
    }
}
