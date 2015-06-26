package enhanced.portals.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import enhanced.base.network.packet.PacketBase;
import enhanced.portals.client.gui.GuiDialingEdit;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalTextureManager;

public class PacketTextureData extends PacketBase {
    PortalTextureManager ptm;
    String name;
    String glyphs;

    public PacketTextureData() {

    }

    public PacketTextureData(String n, String g, PortalTextureManager t) {
        ptm = t;
        name = n;
        glyphs = g;
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        NBTTagCompound data = ByteBufUtils.readTag(buffer);
        ptm = new PortalTextureManager();

        if (data.hasKey("Texture"))
            ptm.readFromNBT(data, "Texture");

        name = data.getString("name");
        glyphs = data.getString("glyphs");
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        NBTTagCompound data = new NBTTagCompound();

        if (ptm != null)
            ptm.writeToNBT(data, "Texture");

        data.setString("name", name);
        data.setString("glyphs", glyphs);

        ByteBufUtils.writeTag(buffer, data);

    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        ProxyClient.saveName = name;
        ProxyClient.saveGlyph = new GlyphIdentifier(glyphs);
        ProxyClient.saveTexture = ptm;
        ((GuiDialingEdit) FMLClientHandler.instance().getClient().currentScreen).receivedData();
    }

    @Override
    public void handleServerSide(EntityPlayer player) {

    }
}
