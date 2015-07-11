package enhanced.portals.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.utilities.Localisation;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.inventory.ContainerModuleManipulator;
import enhanced.portals.tile.TilePortalManipulator;

public class GuiModuleManipulator extends BaseGui {
    public static final int CONTAINER_SIZE = 53;
    TilePortalManipulator module;

    public GuiModuleManipulator(TilePortalManipulator m, EntityPlayer p) {
        super(new ContainerModuleManipulator(m, p.inventory), CONTAINER_SIZE);
        module = m;
        name = Localisation.get(EPMod.ID, Locale.GUI_PORTAL_MANIPULATOR);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_MODULES), 8, containerSize - 35, 0x404040);
    }

    @Override
    protected void drawBackgroundTexture() {
        super.drawBackgroundTexture();

        mc.renderEngine.bindTexture(playerInventoryTexture);
        drawTexturedModalRect(guiLeft + 7, guiTop + containerSize - 25, 7, 7, 162, 18);
    }
}
