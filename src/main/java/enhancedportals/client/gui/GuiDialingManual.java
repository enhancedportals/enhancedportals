package enhancedportals.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhancedportals.EnhancedPortals;
import enhancedportals.client.gui.elements.ElementGlyphSelector;
import enhancedportals.client.gui.elements.ElementGlyphViewer;
import enhancedportals.inventory.ContainerDialingManual;
import enhancedportals.network.ClientProxy;
import enhancedportals.network.GuiHandler;
import enhancedportals.network.packet.PacketGuiData;
import enhancedportals.network.packet.PacketRequestGui;
import enhancedportals.tile.TileController;
import enhancedportals.tile.TileDialingDevice;
import enhancedportals.utility.Localization;

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
        name = "gui.dialDevice";
        controller = dial.getPortalController();
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();

        selector = new ElementGlyphSelector(this, 7, 59);
        addElement(new ElementGlyphViewer(this, 7, 27, selector));
        addElement(selector);

        buttonDial = new GuiButton(3, guiLeft + xSize - 87, guiTop + 136, 80, 20, Localization.get("gui.dial"));
        buttonDial.enabled = !controller.isPortalActive();

        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 115, 80, 20, Localization.get("gui.clear")));
        buttonList.add(new GuiButton(1, guiLeft + xSize - 87, guiTop + 115, 80, 20, Localization.get("gui.save")));
        buttonList.add(new GuiButton(2, guiLeft + 7, guiTop + 136, 80, 20, Localization.get("gui.cancel")));
        buttonList.add(buttonDial);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);

        getFontRenderer().drawString(Localization.get("gui.uniqueIdentifier"), 7, 18, 0x404040);
        getFontRenderer().drawString(Localization.get("gui.glyphs"), 7, 50, 0x404040);

        if (warningTimer > 0) {
            drawRect(7, 27, 7 + 162, 27 + 18, 0xAA000000);
            String s = Localization.get("gui.noUidSet");
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
                ClientProxy.saveGlyph = selector.getGlyphIdentifier();
                ClientProxy.saveName = "Unnamed Portal";
                EnhancedPortals.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_C));
            } else
                warningTimer = 100;
        } else if (button.id == 2)
            EnhancedPortals.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_A));
        else if (button.id == 3)
            if (selector.getGlyphIdentifier().size() > 0) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("dial", selector.getGlyphIdentifier().getGlyphString());
                EnhancedPortals.packetPipeline.sendToServer(new PacketGuiData(tag));
                Minecraft.getMinecraft().thePlayer.closeScreen();
            } else
                warningTimer = 100;
    }
}
