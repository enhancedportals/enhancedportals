package uk.co.shadeddimensions.ep3.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import uk.co.shadeddimensions.ep3.network.CommonProxy;
import uk.co.shadeddimensions.ep3.tileentity.frame.TileNetworkInterface;

public class PacketNetworkInterfaceData extends MainPacket
{
    ChunkCoordinates coord, controller;
    String networkID;
    int connectedPortals;

    public PacketNetworkInterfaceData()
    {

    }

    public PacketNetworkInterfaceData(TileNetworkInterface tile)
    {
        coord = tile.getChunkCoordinates();
        networkID = tile.NetworkIdentifier;
        connectedPortals = CommonProxy.networkManager.getNetworkedPortals(tile.NetworkIdentifier).size() - 1;
        controller = tile.controller;
    }

    @Override
    public MainPacket consumePacket(DataInputStream stream) throws IOException
    {
        coord = readChunkCoordinates(stream);
        controller = readChunkCoordinates(stream);
        networkID = stream.readUTF();
        connectedPortals = stream.readInt();

        return this;
    }

    @Override
    public void execute(INetworkManager network, EntityPlayer player)
    {
        World world = player.worldObj;
        TileEntity tile = world.getBlockTileEntity(coord.posX, coord.posY, coord.posZ);

        if (tile != null && tile instanceof TileNetworkInterface)
        {
            TileNetworkInterface ni = (TileNetworkInterface) tile;

            ni.NetworkIdentifier = networkID;
            ni.connectedPortals = connectedPortals;
            ni.controller = controller;
        }
    }

    @Override
    public void generatePacket(DataOutputStream stream) throws IOException
    {
        writeChunkCoordinates(coord, stream);
        writeChunkCoordinates(controller, stream);
        stream.writeUTF(networkID);
        stream.writeInt(connectedPortals);
    }
}