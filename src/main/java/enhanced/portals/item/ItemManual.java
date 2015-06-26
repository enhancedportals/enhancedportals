package enhanced.portals.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import enhanced.base.item.ItemBase;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.utility.Reference.EPGuis;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;

public class ItemManual extends ItemBase {
    public ItemManual(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
        setMaxStackSize(1);
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add(Localisation.get(EPMod.ID, Locale.ITEM_MANUAL_DESC));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.openGui(EnhancedPortals.instance, EPGuis.MANUAL, world, 0, 0, 0);
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
        if (world.isRemote && player.isSneaking() && ProxyClient.setManualPageFromBlock(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z))) {
            player.openGui(EnhancedPortals.instance, EPGuis.MANUAL, world, 0, 0, 0);
            return true;
        }

        return false;
    }
}
