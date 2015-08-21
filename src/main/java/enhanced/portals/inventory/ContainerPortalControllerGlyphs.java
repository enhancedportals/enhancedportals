package enhanced.portals.inventory;

import cpw.mods.fml.common.FMLCommonHandler;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.client.gui.GuiPortalControllerGlyphs;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.controller.TileController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerPortalControllerGlyphs extends BaseContainer {
    TileController controller;

    public ContainerPortalControllerGlyphs(TileController c, InventoryPlayer p) {
        super(null, p, GuiPortalControllerGlyphs.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        controller = c;
        hideInventorySlots();
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("uid")) {
            if (EnhancedPortals.proxy.networkManager.setPortalUID(controller, new GlyphIdentifier(tag.getString("uid"))) == false) {
                NBTTagCompound errorTag = new NBTTagCompound();
                errorTag.setInteger("error", 0);
                EnhancedPortals.instance.packetPipeline.sendTo(new PacketGuiData(errorTag), (EntityPlayerMP) player);
            } else
                player.openGui(EnhancedPortals.instance, EPGuis.PORTAL_CONTROLLER_A, controller.getWorldObj(), controller.xCoord, controller.yCoord, controller.zCoord);
        }
        else if (tag.hasKey("error") && FMLCommonHandler.instance().getEffectiveSide().isClient())
            ((GuiPortalControllerGlyphs) Minecraft.getMinecraft().currentScreen).setWarningMessage();
    }
}
