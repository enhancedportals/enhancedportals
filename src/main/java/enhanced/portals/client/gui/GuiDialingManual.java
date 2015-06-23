package enhanced.portals.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementGlyphSelector;
import enhanced.portals.client.gui.elements.ElementGlyphViewer;
import enhanced.portals.inventory.ContainerDialingManual;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.network.packet.PacketGuiData;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.tile.TileController;
import enhanced.portals.tile.TileDialingDevice;

public class GuiDialingManual extends BaseGui {
    public static final int CONTAINER_SIZE = 163;
    TileDialingDevice dial;
    TileController controller;
    ElementGlyphSelector selector;
    int warningTimer;
    GuiButton buttonDial;

    public GuiDialingManual(TileDialingDevice d, EntityPlayer p) {
        super(new ContainerDialingManual(d, p.inventory), CONTAINER_SIZE);
        dial = d;
        name = Localization.get(EnhancedPortals.MOD_ID, "gui.dialDevice");
        controller = dial.getPortalController();
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();

        selector = new ElementGlyphSelector(this, 7, 59);
        addElement(new ElementGlyphViewer(this, 7, 27, selector));
        addElement(selector);

        buttonDial = new GuiButton(3, guiLeft + xSize - 87, guiTop + 136, 80, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.dial"));
        buttonDial.enabled = !controller.isPortalActive();

        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 115, 80, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.clear")));
        buttonList.add(new GuiButton(1, guiLeft + xSize - 87, guiTop + 115, 80, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.save")));
        buttonList.add(new GuiButton(2, guiLeft + 7, guiTop + 136, 80, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.cancel")));
        buttonList.add(buttonDial);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);

        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.uniqueIdentifier"), 7, 18, 0x404040);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.glyphs"), 7, 50, 0x404040);

        if (warningTimer > 0) {
            drawRect(7, 27, 7 + 162, 27 + 18, 0xAA000000);
            String s = Localization.get(EnhancedPortals.MOD_ID, "gui.noUidSet");
            getFontRenderer().drawString(s, xSize / 2 - getFontRenderer().getStringWidth(s) / 2, 33, 0xff4040);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (warningTimer > 0)
            warningTimer--;

        buttonDial.enabled = !controller.isPortalActive();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0)
            selector.setIdentifierTo(null);
        else if (button.id == 1) // save
        {
            if (selector.getGlyphIdentifier().size() > 0) {
                ProxyClient.saveGlyph = selector.getGlyphIdentifier();
                ProxyClient.saveName = "Unnamed Portal";
                EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_C));
            } else
                warningTimer = 100;
        } else if (button.id == 2)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_A));
        else if (button.id == 3)
            if (selector.getGlyphIdentifier().size() > 0) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("dial", selector.getGlyphIdentifier().getGlyphString());
                EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
                Minecraft.getMinecraft().thePlayer.closeScreen();
            } else
                warningTimer = 100;
    }
}
