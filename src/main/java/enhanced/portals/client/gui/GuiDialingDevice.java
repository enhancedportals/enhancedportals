package enhanced.portals.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.tabs.TabTip;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementScrollDiallingDevice;
import enhanced.portals.inventory.ContainerDialingDevice;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.network.packet.PacketGuiData;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.tile.TileController;
import enhanced.portals.tile.TileDialingDevice;

public class GuiDialingDevice extends BaseGui {
    public static final int CONTAINER_SIZE = 175, CONTAINER_WIDTH = 256;
    TileDialingDevice dial;
    TileController controller;
    GuiButton buttonDial;

    public GuiDialingDevice(TileDialingDevice d, EntityPlayer p) {
        super(new ContainerDialingDevice(d, p.inventory), CONTAINER_SIZE);
        texture = new ResourceLocation(EnhancedPortals.MOD_ID, "textures/gui/dialling_device.png");
        xSize = CONTAINER_WIDTH;
        dial = d;
        controller = dial.getPortalController();
        name = Localization.get(EnhancedPortals.MOD_ID, "gui.dialDevice");
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonDial = new GuiButton(1, guiLeft + xSize - 147, guiTop + ySize - 27, 140, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.terminate"));
        buttonDial.enabled = controller.isPortalActive();
        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + ySize - 27, 100, 20, Localization.get(EnhancedPortals.MOD_ID, "gui.manualEntry")));
        buttonList.add(buttonDial);

        addElement(new ElementScrollDiallingDevice(this, dial, 7, 28));
        addTab(new TabTip(this, "dialling", EnhancedPortals.MOD_ID));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        buttonDial.enabled = controller.isPortalActive();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.storedIdentifiers"), 7, 18, 0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_B));
        else if (button.id == 1)
            if (controller.isPortalActive()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("terminate", true);
                EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
            }
    }

    public void onEntrySelected(int entry) {
        if (!controller.isPortalActive()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dial", entry);
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    public void onEntryEdited(int entry) {
        ProxyClient.editingID = entry;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("edit", entry);
        EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    public void onEntryDeleted(int entry) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("delete", entry);
        EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
    }
}
