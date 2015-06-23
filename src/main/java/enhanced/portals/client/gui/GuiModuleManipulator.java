package enhanced.portals.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.inventory.ContainerModuleManipulator;
import enhanced.portals.tile.TilePortalManipulator;

public class GuiModuleManipulator extends BaseGui {
    public static final int CONTAINER_SIZE = 53;
    TilePortalManipulator module;

    public GuiModuleManipulator(TilePortalManipulator m, EntityPlayer p) {
        super(new ContainerModuleManipulator(m, p.inventory), CONTAINER_SIZE);
        module = m;
        name = Localization.get(EnhancedPortals.MOD_ID, "gui.moduleManipulator");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        getFontRenderer().drawString(Localization.get(EnhancedPortals.MOD_ID, "gui.modules"), 8, containerSize - 35, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        drawGuiBackgroundLayer(f, i, j);

        mc.renderEngine.bindTexture(playerInventoryTexture);
        drawTexturedModalRect(guiLeft + 7, guiTop + containerSize - 25, 7, 7, 162, 18);
    }
}
