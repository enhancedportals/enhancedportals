package enhanced.portals.portal.frame;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhanced.base.utilities.Localisation;
import enhanced.base.xmod.ComputerCraft;
import enhanced.base.xmod.OpenComputers;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.network.GuiHandler;

@InterfaceList(value = { @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = ComputerCraft.MOD_ID), @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = OpenComputers.MOD_ID) })
public class TileNetworkInterface extends TileFrame implements IPeripheral, SimpleComponent {
    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        if (stack != null) {
            if (stack.getItem() instanceof IToolWrench) {
                if (getPortalController() != null && getPortalController().isFinalized) {
                    if (!EnhancedPortals.proxy.networkManager.hasUID(getPortalController())) {
                        if (!worldObj.isRemote) {
                            player.addChatComponentMessage(Localisation.getChatError(EPMod.ID, "noUidSet"));
                            return true;
                        }
                    } else {
                        GuiHandler.openGui(player, getPortalController(), EPGuis.NETWORK_INTERFACE_A);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {

    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
        if (method == 0)
            getPortalController().constructConnection();
        else if (method == 1)
            getPortalController().deconstructConnection();

        return null;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public void detach(IComputerAccess computer) {

    }

    @Callback(doc = "function():boolean -- Attempts to create a connection to the next portal in the network.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] dial(Context context, Arguments args) {
        getPortalController().constructConnection();
        return new Object[] { true };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    @Override
    @Method(modid = OpenComputers.MOD_ID)
    public String getComponentName() {
        return "ep_interface_network";
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String[] getMethodNames() {
        return new String[] { "dial", "terminate" };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String getType() {
        return "ep_interface_network";
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {

    }

    @Callback(doc = "function():boolean -- Terminates any active connection.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] terminate(Context context, Arguments args) {
        getPortalController().deconstructConnection();
        return new Object[] { true };
    }
}
