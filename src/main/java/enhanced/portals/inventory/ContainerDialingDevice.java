package enhanced.portals.inventory;

import java.util.ArrayList;

import enhanced.base.inventory.BaseContainer;
import enhanced.base.network.packet.PacketGui;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.network.packet.PacketTextureData;
import enhanced.portals.portal.GlyphElement;
import enhanced.portals.portal.dial.TileDialingDevice;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerDialingDevice extends BaseContainer {
    TileDialingDevice dial;
    ArrayList<String> list = new ArrayList<String>();

    public ContainerDialingDevice(TileDialingDevice d, InventoryPlayer p) {
        super(null, p);
        dial = d;
        hideInventorySlots();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (list.size() != dial.glyphList.size())
            updateAll();
        else
            for (int i = 0; i < list.size(); i++)
                if (!list.get(i).equals(dial.glyphList.get(i).name)) {
                    updateAll();
                    break;
                }

        list.clear();

        for (GlyphElement e : dial.glyphList)
            list.add(e.name);
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("terminate"))
            dial.getPortalController().deconstructConnection();
        else if (tag.hasKey("dial")) {
            int id = tag.getInteger("dial");

            if (dial.glyphList.size() > id) {
                GlyphElement e = dial.glyphList.get(id);
                dial.getPortalController().constructConnection(e.identifier, e.texture, player);
            }
        } else if (tag.hasKey("edit")) {
            int id = tag.getInteger("edit");

            if (dial.glyphList.size() > id) {
                GlyphElement e = dial.glyphList.get(id);
                player.openGui(EnhancedPortals.instance, EPGuis.DIALING_DEVICE_D, dial.getWorldObj(), dial.xCoord, dial.yCoord, dial.zCoord);
                EnhancedPortals.instance.packetPipeline.sendTo(new PacketTextureData(e.name, e.identifier.getGlyphString(), e.texture), (EntityPlayerMP) player);
            }
        } else if (tag.hasKey("delete")) {
            int id = tag.getInteger("delete");

            if (dial.glyphList.size() > id)
                dial.glyphList.remove(id);

            EnhancedPortals.instance.packetPipeline.sendTo(new PacketGui(dial), (EntityPlayerMP) player);
        }
    }

    void updateAll() {
        for (int i = 0; i < crafters.size(); i++) {
            ICrafting icrafting = (ICrafting) crafters.get(i);
            EnhancedPortals.instance.packetPipeline.sendTo(new PacketGui(dial), (EntityPlayerMP) icrafting);
        }
    }
}