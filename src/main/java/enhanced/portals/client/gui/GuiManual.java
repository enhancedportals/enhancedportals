package enhanced.portals.client.gui;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.manual.ManualParser;
import enhanced.base.manual.PageManual;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.inventory.ContainerManual;

public class GuiManual extends BaseGui {
    public static final int CONTAINER_SIZE = 180, CONTAINER_WIDTH = 279;
    static HashMap<String, PageManual> manualPages;
    static ResourceLocation textureB = new ResourceLocation(EPMod.ID, "textures/gui/manualB.png");
    static String activePage = "intro";
    static String activeSecondPage = "toc";

    public GuiManual(EntityPlayer p) {
        super(new ContainerManual(p.inventory), CONTAINER_SIZE);
        xSize = CONTAINER_WIDTH;
        setHidePlayerInventory();
        texture = new ResourceLocation(EPMod.ID, "textures/gui/manualA.png");

        if (manualPages == null)
            manualPages = ManualParser.loadManual(EPMod.ID);
    }

    @Override
    protected void drawBackgroundTexture() {
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 140, ySize);
        mc.renderEngine.bindTexture(textureB);
        drawTexturedModalRect(guiLeft + 140, guiTop, 0, 0, 139, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        manualPages.get(activePage).render(false);

        if (activeSecondPage != null)
            manualPages.get(activeSecondPage).render(true);

        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 4 && activeSecondPage != null && !activeSecondPage.isEmpty() && manualPages.get(activeSecondPage).hasNextPage()) {
            activePage = manualPages.get(activeSecondPage).getNextPage();
            activeSecondPage = manualPages.get(activePage).getNextPage();
            return;
        } else if (mouseButton == 3 && manualPages.get(activePage).hasPrevPage()) {
            String prev = manualPages.get(activePage).getPrevPage();
            PageManual m = manualPages.get(prev);
            
            if (m.hasPrevPage()) {
                activePage = m.getPrevPage();
                activeSecondPage = prev;
            }
            return;
        } else if (mouseY >= guiTop + CONTAINER_SIZE + 3 && mouseY < guiTop + CONTAINER_SIZE + 13)
            if (mouseX >= guiLeft + CONTAINER_WIDTH - 23 && mouseX < guiLeft + CONTAINER_WIDTH - 5) {
                // next
            } else if (mouseX >= guiLeft + 5 && mouseX < guiLeft + 23) {
                // prev
            } else if (mouseX >= guiLeft + xSize / 2 - 10 && mouseX < guiLeft + xSize / 2 - 10 + 21) {
                // back
            }
    }

    @Override
    public void initGui() {
        super.initGui();
    }
}
