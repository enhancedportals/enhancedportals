package enhanced.portals.client.gui;

import java.util.Random;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.tabs.TabTip;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.client.gui.elements.ElementGlyphSelector;
import enhanced.portals.client.gui.elements.ElementGlyphViewer;
import enhanced.portals.inventory.ContainerDialingEditIdentifier;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.frame.TileDialingDevice;

public class GuiDialingEditIdentifier extends BaseGui {
    public static final int CONTAINER_SIZE = 135;
    TileDialingDevice dial;
    GuiButton buttonCancel, buttonSave;
    ElementGlyphSelector selector;

    public GuiDialingEditIdentifier(TileDialingDevice d, EntityPlayer p) {
        super(new ContainerDialingEditIdentifier(d, p.inventory), CONTAINER_SIZE);
        dial = d;
        name = Localisation.get(EPMod.ID, Locale.GUI_DIALLING_DEVICE);
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();
        int buttonWidth = 80;
        buttonCancel = new GuiButton(0, guiLeft + 7, guiTop + containerSize - 27, buttonWidth, 20, Localisation.get(EPMod.ID, Locale.GUI_CANCEL));
        buttonSave = new GuiButton(1, guiLeft + xSize - buttonWidth - 7, guiTop + containerSize - 27, buttonWidth, 20, Localisation.get(EPMod.ID, Locale.GUI_SAVE));
        buttonList.add(buttonCancel);
        buttonList.add(buttonSave);
        addTab(new TabTip(this, "glyphs", EPMod.ID));
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
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, EPGuis.DIALING_DEVICE_D));
        else if (button.id == buttonSave.id) // Save Changes
        {
            ProxyClient.saveGlyph = selector.getGlyphIdentifier();
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, EPGuis.DIALING_DEVICE_D));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_UNIQUE_IDENTIFIER), 7, 19, 0x404040);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (isShiftKeyDown()) {
            buttonCancel.displayString = EnumChatFormatting.AQUA + Localisation.get(EPMod.ID, Locale.GUI_CLEAR);
            buttonSave.displayString = (isCtrlKeyDown() ? EnumChatFormatting.GOLD : EnumChatFormatting.AQUA) + Localisation.get(EPMod.ID, Locale.GUI_RANDOM);
        } else {
            buttonCancel.displayString = Localisation.get(EPMod.ID, Locale.GUI_CANCEL);
            buttonSave.displayString = Localisation.get(EPMod.ID, Locale.GUI_SAVE);
        }
    }
}
