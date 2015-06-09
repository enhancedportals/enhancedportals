package enhancedportals.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import enhancedportals.EnhancedPortals;

public class CreativeTab extends CreativeTabs {

	public CreativeTab() {
		super(EnhancedPortals.MOD_ID);
	}

	@Override
	public Item getTabIconItem() {
		return Items.apple; // TODO
	}

}
