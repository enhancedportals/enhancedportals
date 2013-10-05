package uk.co.shadeddimensions.ep3.tileentity.frame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import uk.co.shadeddimensions.ep3.network.CommonProxy;
import uk.co.shadeddimensions.ep3.portal.NetworkManager;
import uk.co.shadeddimensions.ep3.tileentity.TilePortalFrame;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileNetworkInterface extends TilePortalFrame
{
    public String NetworkIdentifier;

    @SideOnly(Side.CLIENT)
    public int connectedPortals = 0;

    public TileNetworkInterface()
    {
        NetworkIdentifier = NetworkManager.BLANK_IDENTIFIER;
    }

    @Override
    public void actionPerformed(int id, String string, EntityPlayer player)
    {
        if (id == 0)
        {
            if (!NetworkIdentifier.equals(NetworkManager.BLANK_IDENTIFIER))
            {
                CommonProxy.networkManager.removePortalFromNetwork(getController().UniqueIdentifier, NetworkIdentifier);
            }
            else if (!string.equals(NetworkManager.BLANK_IDENTIFIER))
            {
                CommonProxy.networkManager.addPortalToNetwork(getController().UniqueIdentifier, string);
            }

            NetworkIdentifier = string;
        }

        CommonProxy.sendUpdatePacketToAllAround(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        NetworkIdentifier = tagCompound.getString("NetworkIdentifier");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        tagCompound.setString("NetworkIdentifier", NetworkIdentifier);
    }
}