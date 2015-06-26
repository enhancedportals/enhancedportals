package enhanced.portals.client.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.client.gui.button.GuiBetterSlider;
import enhanced.base.client.gui.button.GuiRGBSlider;
import enhanced.base.client.gui.tabs.TabTip;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.network.packet.PacketRequestGui;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.elements.ElementScrollParticles;
import enhanced.portals.client.gui.tabs.TabColour;
import enhanced.portals.inventory.ContainerTextureParticle;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.portal.PortalTextureManager;
import enhanced.portals.tile.TileController;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPGuis;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;

public class GuiTextureParticle extends BaseGui {
    public static final int CONTAINER_SIZE = 92, CONTAINER_WIDTH = 190;
    protected TileController controller;
    protected GuiRGBSlider sliderR, sliderG, sliderB;
    protected GuiButton buttonReset, buttonSave;

    public GuiTextureParticle(TileController c, EntityPlayer p) {
        super(new ContainerTextureParticle(c, p.inventory), CONTAINER_SIZE);
        controller = c;
        xSize = CONTAINER_WIDTH;
        name = Localisation.get(EPMod.ID, Locale.GUI_PARTICLE);
        texture = new ResourceLocation(EPMod.ID, "textures/gui/textures_particles.png");
        leftNudge = 7;
        hasSingleTexture = true;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == buttonSave.id || button.id == buttonReset.id) {
            if (button.id == buttonSave.id) {
                int hex = Integer.parseInt(String.format("%02x%02x%02x", sliderR.getValue(), sliderG.getValue(), sliderB.getValue()), 16);
                getPTM().setParticleColour(hex);
            } else if (button.id == buttonReset.id) {
                int colour = 0x0077D8;
                getPTM().setParticleColour(colour);

                Color c = new Color(colour);
                sliderR.sliderValue = c.getRed() / 255f;
                sliderG.sliderValue = c.getGreen() / 255f;
                sliderB.sliderValue = c.getBlue() / 255f;
            }

            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("colour", Integer.parseInt(String.format("%02x%02x%02x", sliderR.getValue(), sliderG.getValue(), sliderB.getValue()), 16));
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
        } else if (button.id == 500)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, EPGuis.TEXTURE_A));
        else if (button.id == 501)
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketRequestGui(controller, EPGuis.TEXTURE_B));
    }

    @Override
    public void initGui() {
        super.initGui();

        Color c = new Color(getPTM().getParticleColour());
        sliderR = new GuiRGBSlider(100, guiLeft + xSize + 4, guiTop + 25, Localisation.get(EPMod.ID, Locale.GUI_RED), c.getRed() / 255f, 105);
        sliderG = new GuiRGBSlider(101, guiLeft + xSize + 4, guiTop + 46, Localisation.get(EPMod.ID, Locale.GUI_GREEN), c.getGreen() / 255f, 105);
        sliderB = new GuiRGBSlider(102, guiLeft + xSize + 4, guiTop + 67, Localisation.get(EPMod.ID, Locale.GUI_BLUE), c.getBlue() / 255f, 105);

        buttonList.add(sliderR);
        buttonList.add(sliderG);
        buttonList.add(sliderB);

        buttonSave = new GuiButton(110, guiLeft + xSize + 4, guiTop + 88, 53, 20, Localisation.get(EPMod.ID, Locale.GUI_SAVE));
        buttonReset = new GuiButton(111, guiLeft + xSize + 57, guiTop + 88, 53, 20, Localisation.get(EPMod.ID, Locale.GUI_RESET));

        buttonList.add(buttonSave);
        buttonList.add(buttonReset);

        buttonList.add(new GuiButton(500, guiLeft + 7, guiTop + containerSize - 18, 20, 20, ""));
        buttonList.add(new GuiButton(501, guiLeft + 28, guiTop + containerSize - 18, 20, 20, ""));

        addTab(new TabColour(this, sliderR, sliderG, sliderB, buttonSave, buttonReset));
        addTab(new TabTip(this, "colourTip", EPMod.ID));
        addElement(new ElementScrollParticles(this, 7, 17, texture));
    }

    @Override
    protected void mouseMovedOrUp(int par1, int par2, int par3) {
        super.mouseMovedOrUp(par1, par2, par3);

        if (par3 == 0)
            for (Object o : buttonList)
                if (o instanceof GuiBetterSlider) {
                    GuiBetterSlider slider = (GuiBetterSlider) o;
                    slider.mouseReleased(par1, par2);
                }
    }

    public void particleSelected(int particle) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("type", particle);
        EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        getItemRenderer().renderWithColor = false;
        ItemStack frame = new ItemStack(EPBlocks.frame, 0, 0), portal = new ItemStack(EPBlocks.portal, 0, 0);
        Color frameColour = new Color(getPTM().getFrameColour()), portalColour = new Color(getPTM().getPortalColour());

        if (getPTM() != null) {
            frameColour = new Color(getPTM().getFrameColour());
            portalColour = new Color(getPTM().getPortalColour());

            if (getPTM().getFrameItem() != null)
                frame = getPTM().getFrameItem();

            if (getPTM().getPortalItem() != null)
                portal = getPTM().getPortalItem();
        }

        GL11.glColor3f(frameColour.getRed() / 255F, frameColour.getGreen() / 255F, frameColour.getBlue() / 255F);

        if (getPTM().hasCustomFrameTexture())
            drawIconNoReset(ProxyClient.customFrameTextures.get(getPTM().getCustomFrameTexture()), 9, containerSize - 16, 0);
        else
            drawItemStack(frame, 9, containerSize - 16);

        GL11.glColor3f(portalColour.getRed() / 255F, portalColour.getGreen() / 255F, portalColour.getBlue() / 255F);

        if (getPTM().hasCustomPortalTexture())
            drawIconNoReset(ProxyClient.customPortalTextures.get(getPTM().getCustomPortalTexture()), 30, containerSize - 16, 0);
        else
            drawItemStack(portal, 30, containerSize - 16);

        GL11.glColor3f(1f, 1f, 1f);
        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    public PortalTextureManager getPTM() {
        return controller.activeTextureData;
    }
}
