package enhancedportals.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import enhancedportals.network.CommonProxy;

public class ItemBlankUpgrade extends Item {
    public static ItemBlankUpgrade instance;
    IIcon texture;

    public ItemBlankUpgrade(String n) {
        super();
        instance = this;
        setCreativeTab(CommonProxy.creativeTab);
        setUnlocalizedName(n);
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return texture;
    }

    @Override
    public void registerIcons(IIconRegister ir) {
        texture = ir.registerIcon("enhancedportals:blank_upgrade");
    }
}
