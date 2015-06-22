package enhanced.portals.item;

import net.minecraft.item.Item;
import enhanced.portals.EnhancedPortals;

public class ItemDiamondNugget extends Item {
    public static ItemDiamondNugget instance;
    
    public ItemDiamondNugget(String n) {
        setTextureName(EnhancedPortals.MOD_ID + ":" + n);
        setUnlocalizedName(n);
        setMaxStackSize(64);
        setCreativeTab(EnhancedPortals.instance.creativeTab);
        instance = this;
    }
}
