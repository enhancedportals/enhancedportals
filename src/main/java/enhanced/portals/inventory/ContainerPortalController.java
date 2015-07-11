package enhanced.portals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.GuiPortalController;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.tile.TileController;

public class ContainerPortalController extends BaseContainer {
    TileController controller;
    String oldGlyphs = "EMPTY";
    int power = -1;

    public ContainerPortalController(TileController c, InventoryPlayer p) {
        super(null, p, GuiPortalController.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        controller = c;
        hideInventorySlots();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        String glyphs = EnhancedPortals.proxy.networkManager.getPortalUID(controller);
        int _power = controller.getEnergyStored(ForgeDirection.UNKNOWN);
        
        if (glyphs == null)
            glyphs = "";

        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);

            if (!glyphs.equals(oldGlyphs)) {
                NBTTagCompound t = new NBTTagCompound();
                t.setString("uid", glyphs);
                EnhancedPortals.instance.packetPipeline.sendTo(new PacketGuiData(t), (EntityPlayerMP) icrafting);
            }
            
            if (power != _power)
            	icrafting.sendProgressBarUpdate(this, 0, _power);
        }

        oldGlyphs = glyphs;
        power = _power;
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("uid"))
            controller.setUID(new GlyphIdentifier(tag.getString("uid")));
    }

    @Override
    public void updateProgressBar(int id, int val) {
    	if (id == 0)
    		controller.getEnergyStorage().setEnergyStored(val);
    }
}
