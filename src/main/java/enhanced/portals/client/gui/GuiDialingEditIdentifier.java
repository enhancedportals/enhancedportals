package enhanced.portals.client.gui;

import java.util.Random;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.tabs.TabTip;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementGlyphSelector;
import enhanced.portals.client.gui.elements.ElementGlyphViewer;
import enhanced.portals.inventory.ContainerDialingEditIdentifier;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.tile.TileDialingDevice;

public class GuiDialingEditIdentifier extends BaseGui {
    public static final int CONTAINER_SIZE = 135;
    TileDialingDevice dial;
    GuiButton buttonCancel, buttonSave;
    ElementGlyphSelector selector;

    public GuiDialingEditIdentifier(TileDialingDevice d, EntityPlayer p) {
        super(new ContainerDialingEditIdentifier(d, p.inventory), CONTAINER_SIZE);
        dial = d;
        name = "gui.dialDevice";
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();
        int buttonWidth = 80;
        buttonCancel = new GuiButton(0, guiLeft + 7, guiTop + containerSize - 27, buttonWidth, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.cancel"));
        buttonSave = new GuiButton(1, guiLeft + xSize - buttonWidth - 7, guiTop + containerSize - 27, buttonWidth, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.save"));
        buttonList.add(buttonCancel);
        buttonList.add(buttonSave);
        addTab(new TabTip(this, "glyphs", EnhancedPortals.MOD_ID));
        selector = new ElementGlyphSelector(this, 7, 52);
        selector.setIdentifierTo(ProxyClient.saveGlyph);
        addElement(selector);
        addElement(new ElementGlyphViewer(this, 7, 29, selector));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (isShiftKeyDown()) {
            if (button.id == buttonCancel.id)
                selector.reset();
            else if (button.id == buttonSave.id) // Random
            {
                Random random = new Random();
                GlyphIdentifier iden = new GlyphIdentifier();

                for (int i = 0; i < (isCtrlKeyDown() ? 9 : random.nextInt(8) + 1); i++)
                    iden.addGlyph(random.nextInt(27));

                selector.setIdentifierTo(iden);
            }
        } else if (button.id == buttonCancel.id)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_D));
        else if (button.id == buttonSave.id) // Save Changes
        {
            ProxyClient.saveGlyph = selector.getGlyphIdentifier();
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_D));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.uniqueIdentifier"), 7, 19, 0x404040);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (isShiftKeyDown()) {
            buttonCancel.displayString = EnumChatFormatting.AQUA + Localization.get(EnhancedPortals.MOD_ID, "gui.clear");
            buttonSave.displayString = (isCtrlKeyDown() ? EnumChatFormatting.GOLD : EnumChatFormatting.AQUA) + Localization.get(EnhancedPortals.MOD_ID, "gui.random");
        } else {
            buttonCancel.displayString = Localization.get(EnhancedPortals.MOD_ID, "gui.cancel");
            buttonSave.displayString = Localization.get(EnhancedPortals.MOD_ID, "gui.save");
        }
    }
}
