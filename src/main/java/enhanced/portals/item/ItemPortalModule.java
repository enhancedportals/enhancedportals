package enhanced.portals.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import enhanced.base.item.ItemBase;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;
import enhanced.portals.utility.Reference.PortalModules;

public class ItemPortalModule extends ItemBase {
    IIcon[] overlayIcons = new IIcon[PortalModules.count()];

    public ItemPortalModule(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(Localisation.get(EPMod.ID, Locale.ITEM_PORTAL_MODULE));

        if (stack.getItemDamage() == PortalModules.FACING.ordinal()) {
            NBTTagCompound t = stack.getTagCompound();
            int i = 0;

            if (t != null)
                i = t.getInteger("facing");

            list.add(EnumChatFormatting.GRAY + Localisation.get(EPMod.ID, Locale.GUI_FACING_X + i));
        }

        list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal(getUnlocalizedNameInefficiently(stack) + ".desc"));
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        if (pass == 1)
            return overlayIcons[damage];

        return super.getIconFromDamageForRenderPass(damage, pass);
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (itemStack.getItemDamage() == PortalModules.PORTAL_INVISIBLE.ordinal())
            return EnumRarity.epic;
        else if (itemStack.getItemDamage() == PortalModules.MOMENTUM.ordinal())
            return EnumRarity.rare;

        return EnumRarity.common;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < PortalModules.values().length; i++)
            list.add(new ItemStack(item, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking() && stack.getItemDamage() == PortalModules.FACING.ordinal()) {
            NBTTagCompound tag = stack.getTagCompound();

            if (tag == null) {
                tag = new NBTTagCompound();
                tag.setInteger("facing", 1);
            } else {
                int face = tag.getInteger("facing") + 1;

                if (face >= 4)
                    face = 0;

                tag.setInteger("facing", face);
            }

            stack.setTagCompound(tag);
        }

        return stack;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);

        for (int i = 0; i < overlayIcons.length; i++)
            overlayIcons[i] = register.registerIcon(EPMod.ID + ":portal_module_" + i);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
}
