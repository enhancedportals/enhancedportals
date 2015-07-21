package enhanced.portals.portal.frame;

import io.netty.buffer.ByteBuf;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhanced.base.utilities.BlockPos;
import enhanced.base.utilities.WorldUtilities;
import enhanced.base.xmod.ComputerCraft;
import enhanced.base.xmod.OpenComputers;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.network.GuiHandler;

@InterfaceList(value = { @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = ComputerCraft.MOD_ID), @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = OpenComputers.MOD_ID) })
public class TileTransferFluid extends TileFrameTransfer implements IFluidHandler, IPeripheral, SimpleComponent {
    public FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);

    int tickTimer = 20, time = 0;

    IFluidHandler[] handlers = new IFluidHandler[6];

    boolean cached = false;

    byte outputTracker = 0;

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        if (stack != null) {
            if (stack.getItem() instanceof IToolWrench) {
                if (getPortalController() != null && getPortalController().isFinalized) {
                    GuiHandler.openGui(player, this, EPGuis.TRANSFER_FLUID);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
        if (method == 0)
            return new Object[] { tank.getFluid() != null ? tank.getFluid().getFluid().getName() : "" };
        else if (method == 1)
            return new Object[] { tank.getFluidAmount() };
        else if (method == 2)
            return new Object[] { tank.getFluidAmount() == tank.getCapacity() };
        else if (method == 3)
            return new Object[] { tank.getFluidAmount() == 0 };
        else if (method == 4)
            return new Object[] { isSending };

        return null;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public void detach(IComputerAccess computer) {

    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid()))
            return null;

        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    @Method(modid = OpenComputers.MOD_ID)
    public String getComponentName() {
        return "ep_transfer_fluid";
    }

    @Callback(direct = true, limit = 1, doc = "function():table -- Get a description of the fluid stored inside the module.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] getFluid(Context context, Arguments args) {
        return new Object[] { tank.getInfo() };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String[] getMethodNames() {
        return new String[] { "getFluidStored", "getAmountStored", "isFull", "isEmpty", "isSending" };
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    @Override
    @Method(modid = ComputerCraft.MOD_ID)
    public String getType() {
        return "ep_transfer_fluid";
    }

    @Callback(direct = true, doc = "function():boolean -- Returns true if the module is set to send fluids.")
    @Method(modid = OpenComputers.MOD_ID)
    public Object[] isSending(Context context, Arguments args) {
        return new Object[] { isSending };
    }

    @Override
    public void onNeighborChanged() {
        updateFluidHandlers();
    }

    @Override
    public void writeToGui(ByteBuf buffer) {
        if (tank.getFluid() != null) {
            buffer.writeBoolean(false);
            buffer.writeInt(tank.getFluid().getFluidID());
            buffer.writeInt(tank.getFluidAmount());
        } else
            buffer.writeBoolean(false);
    }

    @Override
    public void readFromGui(ByteBuf buffer) {
        if (buffer.readBoolean())
            tank.setFluid(new FluidStack(FluidRegistry.getFluid(buffer.readInt()), buffer.readInt()));
        else
            tank.setFluid(null);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.writeToNBT(tag);
    }

    void transferFluid(int side) {
        if (handlers[side] == null)
            return;

        tank.drain(handlers[side].fill(ForgeDirection.getOrientation(side).getOpposite(), tank.getFluid(), true), true);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote)
            if (isSending) {
                if (time >= tickTimer) {
                    time = 0;

                    TileController controller = getPortalController();

                    if (controller != null && controller.isPortalActive() && tank.getFluidAmount() > 0) {
                        TileController exitController = controller.getDestination();

                        if (exitController != null)
                            for (BlockPos c : exitController.transferFluids) {
                                TileEntity tile = WorldUtilities.getTileEntity(exitController.getWorldObj(), c);

                                if (tile != null && tile instanceof TileTransferFluid) {
                                    TileTransferFluid fluid = (TileTransferFluid) tile;

                                    if (!fluid.isSending)
                                        if (fluid.fill(null, tank.getFluid(), false) > 0)
                                            tank.drain(fluid.fill(null, tank.getFluid(), true), true);
                                }

                                if (tank.getFluidAmount() == 0)
                                    break;
                            }
                    }
                }

                time++;
            } else {
                if (!cached)
                    updateFluidHandlers();

                for (int i = outputTracker; i < 6 && tank.getFluidAmount() > 0; i++)
                    transferFluid(i);

                outputTracker++;
                outputTracker = (byte) (outputTracker % 6);
            }
    }

    void updateFluidHandlers() {
        for (int i = 0; i < 6; i++) {
            BlockPos c = BlockPos.offset(getBlockPos(), ForgeDirection.getOrientation(i));
            TileEntity tile = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (tile != null && tile instanceof IFluidHandler) {
                IFluidHandler fluid = (IFluidHandler) tile;

                if (fluid.getTankInfo(ForgeDirection.getOrientation(i).getOpposite()) != null)
                    handlers[i] = fluid;
                else
                    handlers[i] = null;
            } else
                handlers[i] = null;
        }

        cached = true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.readFromNBT(tag);
    }
}
