package enhancedportals.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class FrameItem extends ItemBlockWithMetadata {
	
	String[] unlocalizedName = { "frame", "controller", "redstone", "network_interface", "dial_device", "unused", "upgrade", "fluid", "item", "energy" };
	
	public FrameItem(Block b) {
		super(b, b);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List data, boolean p_77624_4_) {
		// TODO
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List subtypes) {
		for (int i = 0; i < unlocalizedName.length; i++) {
			subtypes.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + unlocalizedName[MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedName.length - 1)];
    }
}
