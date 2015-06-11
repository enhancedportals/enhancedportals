package enhancedportals.item;

import net.minecraft.item.Item;
import enhancedportals.EnhancedPortals;
import enhancedportals.network.CommonProxy;

public class ItemDiamondNugget extends Item {
    public static ItemDiamondNugget instance;
    
    public ItemDiamondNugget(String n) {
        setTextureName(EnhancedPortals.MOD_ID + ":" + n);
        setUnlocalizedName(n);
        setMaxStackSize(64);
        setCreativeTab(CommonProxy.creativeTab);
        instance = this;
    }
}
