package enhanced.portals.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import enhanced.base.item.ItemBase;
import enhanced.base.utilities.DimensionCoordinates;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;

public class ItemLocationCard extends ItemBase {
    public ItemLocationCard(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        DimensionCoordinates w = getDBSLocation(stack);

        if (w != null)
            list.add(Localisation.get(EPMod.ID, Locale.ITEM_LOCATION_SET));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking() && hasDBSLocation(stack)) {
            clearDBSLocation(stack);
            return stack;
        }

        return stack;
    }

    public static void clearDBSLocation(ItemStack s) {
        s.setTagCompound(null);
    }

    public static DimensionCoordinates getDBSLocation(ItemStack s) {
        if (hasDBSLocation(s)) {
            NBTTagCompound t = s.getTagCompound();
            return new DimensionCoordinates(t.getInteger("X"), t.getInteger("Y"), t.getInteger("Z"), t.getInteger("D"));
        }

        return null;
    }

    public static boolean hasDBSLocation(ItemStack s) {
        return s.hasTagCompound();
    }

    public static void setDBSLocation(ItemStack s, DimensionCoordinates w) {
        NBTTagCompound t = new NBTTagCompound();
        t.setInteger("X", w.posX);
        t.setInteger("Y", w.posY);
        t.setInteger("Z", w.posZ);
        t.setInteger("D", w.dimension);

        s.setTagCompound(t);
    }
}
