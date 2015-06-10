package enhancedportals.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class GuiBetterSlider extends GuiButton {
    public float sliderValue = 1.0F;
    public boolean dragging;

    public GuiBetterSlider(int id, int x, int y, String displayText, float initialValue) {
        super(id, x, y, 150, 20, displayText);
        sliderValue = initialValue;
    }

    public GuiBetterSlider(int id, int x, int y, String displayText, float initialValue, int w) {
        super(id, x, y, w, 20, displayText);
        sliderValue = initialValue;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
        if (visible) {
            if (dragging) {
                sliderValue = (float) (par2 - (xPosition + 4)) / (float) (width - 8);

                if (sliderValue < 0.0F)
                    sliderValue = 0.0F;

                if (sliderValue > 1.0F)
                    sliderValue = 1.0F;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(xPosition + (int) (sliderValue * (width - 8)), yPosition, 0, 66, 4, height);
            drawTexturedModalRect(xPosition + (int) (sliderValue * (width - 8)) + 4, yPosition, 196, 66, 4, height);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent e).
     */
    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        if (super.mousePressed(par1Minecraft, par2, par3)) {
            sliderValue = (float) (par2 - (xPosition + 4)) / (float) (width - 8);

            if (sliderValue < 0.0F)
                sliderValue = 0.0F;

            if (sliderValue > 1.0F)
                sliderValue = 1.0F;

            dragging = true;
            return true;
        } else
            return false;
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void mouseReleased(int par1, int par2) {
        dragging = false;
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if (visible) {
            FontRenderer fontrenderer = par1Minecraft.fontRenderer;
            par1Minecraft.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            field_146123_n = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + width && par3 < yPosition + height;
            drawTexturedModalRect(xPosition, yPosition, 0, 46, width / 2, height);
            drawTexturedModalRect(xPosition + width / 2, yPosition, 199 - width / 2, 46, 1 + width / 2, height);
            mouseDragged(par1Minecraft, par2, par3);
            int l = 14737632;

            if (!enabled)
                l = -6250336;
            else if (field_146123_n)
                l = 16777120;

            drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, l);
        }
    }
}
