package enhanced.portals.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import enhanced.base.utilities.Localisation;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;
import enhanced.portals.utility.Reference.PortalFrames;

public class ItemFrame extends ItemBlockWithMetadata {
    public ItemFrame(Block b) {
        super(b, EPBlocks.frame);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        int damage = stack.getItemDamage();

        if (damage > 0)
            list.add(Localisation.get(EPMod.ID, Locale.BLOCK_PORTAL_FRAME_PART));
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List list) {
        for (int i = 0; i < PortalFrames.count(); i++)
            list.add(new ItemStack(par1, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        PortalFrames frame = PortalFrames.get(stack.getItemDamage());
        String name = "unknown";

        if (frame != null)
            name = frame.getName();

        return super.getUnlocalizedName() + "." + name;
    }
}
