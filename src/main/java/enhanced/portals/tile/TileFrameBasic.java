package enhanced.portals.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.utility.GeneralUtils;
import enhanced.portals.utility.Reference.EPGuis;
import enhanced.portals.utility.Reference.EPItems;

public class TileFrameBasic extends TileFrame {
    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        TileController controller = getPortalController();

        if (stack != null && controller != null && controller.isFinalized)
            if (GeneralUtils.isWrench(stack)) {
                GuiHandler.openGui(player, controller, EPGuis.PORTAL_CONTROLLER_A);
                return true;
            } else if (stack.getItem() == EPItems.nanobrush) {
                GuiHandler.openGui(player, controller, EPGuis.TEXTURE_A);
                return true;
            }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {

    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {

    }
}
