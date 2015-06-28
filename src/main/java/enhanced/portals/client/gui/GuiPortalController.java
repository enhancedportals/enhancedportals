package enhanced.portals.client.gui;

import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.tabs.TabTip;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementGlyphDisplay;
import enhanced.portals.inventory.ContainerPortalController;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.tile.TileController;
import enhanced.portals.utility.Reference.EPGuis;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;

public class GuiPortalController extends BaseGui {
    public static final int CONTAINER_SIZE = 78;
    TileController controller;
    GuiButton buttonLock;
    ElementGlyphDisplay display;

    public GuiPortalController(TileController c, EntityPlayer p) {
        super(new ContainerPortalController(c, p.inventory), CONTAINER_SIZE);
        controller = c;
        name = Localisation.get(EPMod.ID, Locale.GUI_PORTAL_CONTROLLER);
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonLock = new GuiButton(10, guiLeft + 7, guiTop + containerSize - 27, 162, 20, Localisation.get(EPMod.ID, controller.isPublic ? Locale.GUI_PUBLIC : Locale.GUI_PRIVATE));
        buttonList.add(buttonLock);
        display = new ElementGlyphDisplay(this, 7, 29, controller.uID);
        addElement(display);
        addTab(new TabTip(this, "privatePublic", EPMod.ID));
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 32 && y < guiTop + 47)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, EPGuis.PORTAL_CONTROLLER_B));
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
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_UNIQUE_IDENTIFIER), 7, 19, 0x404040);

        if (x >= guiLeft + 7 && x <= guiLeft + 168 && y >= guiTop + 32 && y < guiTop + 47)
            drawHoveringText(Arrays.asList(new String[] { Localisation.get(EPMod.ID, Locale.GUI_CLICK_TO_MODIFY) }), x - guiLeft, y - guiTop, getFontRenderer());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        buttonLock.displayString = Localisation.get(EPMod.ID, controller.isPublic ? Locale.GUI_PUBLIC : Locale.GUI_PRIVATE);
        display.setIdentifier(controller.uID);
    }
}
