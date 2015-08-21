package enhanced.portals.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.EPRenderers;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class ItemGlasses extends ItemArmor {
    IIcon overlay;

    public ItemGlasses(String n) {
        super(ArmorMaterial.CLOTH, EPRenderers.glassesRenderIndex, 0);
        setCreativeTab(EnhancedPortals.instance.creativeTab);
        setUnlocalizedName(n);
        setTextureName(EPMod.ID + ":" + n);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return EPMod.ID + (type == "overlay" ? ":textures/models/armor/glasses_overlay.png" : ":textures/models/armor/glasses.png");
    }

    @Override
    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
        return false;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        overlay = register.registerIcon(EPMod.ID + ":glasses_overlay");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 0 ? getIconFromDamage(0) : overlay;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getColor(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();

        if (nbttagcompound == null)
            return 0xFFFFFF;
        
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
        return nbttagcompound1 == null ? 0xFFFFFF : (nbttagcompound1.hasKey("color", 3) ? nbttagcompound1.getInteger("color") : 0xFFFFFF);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass > 0)
            return 0xFFFFFF;

        return getColor(stack);
    }
}
