package enhanced.portals.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import enhanced.base.inventory.BaseContainer;
import enhanced.base.network.PacketBase;

public class PacketGuiData extends PacketBase {
    NBTTagCompound tag;

    public PacketGuiData() {

    }

    public PacketGuiData(NBTTagCompound t) {
        tag = t;
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        tag = ByteBufUtils.readTag(buffer);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, tag);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        Container container = player.openContainer;

        if (container != null && container instanceof BaseContainer)
            ((BaseContainer) container).handleGuiPacket(tag, player);
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        Container container = player.openContainer;

        if (container != null && container instanceof BaseContainer)
            ((BaseContainer) container).handleGuiPacket(tag, player);
    }
}
