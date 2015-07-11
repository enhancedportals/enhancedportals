package enhanced.portals.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import buildcraft.api.tools.IToolWrench;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.Reference.EPItems;
import enhanced.portals.Reference.PortalModules;
import enhanced.portals.item.ItemPortalModule;
import enhanced.portals.network.GuiHandler;

public class TilePortalManipulator extends TileFrame implements IInventory {
    ItemStack[] inventory = new ItemStack[9];
    PortalModules[] modules = new PortalModules[9];

    void updateModuleCache() {
        modules = new PortalModules[9];

        for (int i = 0; i < inventory.length; i++) {
            ItemStack s = inventory[i];

            if (s != null)
                if (s.getItem() == EPItems.portalModule)
                    modules[i] = PortalModules.get(s.getItemDamage());
        }
    }

    public boolean hasModule(PortalModules m) {
        for (PortalModules installed : modules)
            if (installed == m)
                return true;

        return false;
    }

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        if (stack != null) {
            if (stack.getItem() instanceof IToolWrench) {
                if (getPortalController() != null && getPortalController().isFinalized) {
                    GuiHandler.openGui(player, this, EPGuis.MODULE_MANIPULATOR);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {
        NBTTagList items = new NBTTagList();

        for (int i = 0; i < getSizeInventory(); i++) {
            NBTTagCompound t = new NBTTagCompound();
            ItemStack s = getStackInSlot(i);

            if (s != null)
                s.writeToNBT(t);

            items.appendTag(t);
        }

        tag.setTag("Modules", items);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void closeInventory() {

    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        ItemStack stack = getStackInSlot(i);

        if (stack != null)
            if (stack.stackSize <= j)
                setInventorySlotContents(i, null);
            else {
                stack = stack.splitStack(j);

                if (stack.stackSize == 0)
                    setInventorySlotContents(i, null);
            }

        return stack;
    }

    @Override
    public String getInventoryName() {
        return "tile.ep3.portalFrame.upgrade.name";
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inventory[i];
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return inventory[i];
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemPortalModule && !hasModule(PortalModules.get(stack.getItemDamage()));
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {
        NBTTagList items = tag.getTagList("Modules", 10);

        for (int i = 0; i < items.tagCount(); i++)
            setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i)));
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagList items = compound.getTagList("Modules", compound.getId());

        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < getSizeInventory())
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
        }

        updateModuleCache();
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        inventory[i] = itemstack;
        updateModuleCache();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList items = new NBTTagList();

        // Goes through all of the module inventory.
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }

        // Saves the inventory under "Modules" in NBT
        compound.setTag("Modules", items);
    }

    public ItemStack getModule(PortalModules m) {
        if (!hasModule(m))
            return null;

        for (int i = 0; i < inventory.length; i++)
            if (modules[i] == m)
                return inventory[i];

        return null;
    }
}
