package enhanced.portals.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import enhanced.portals.EnhancedPortals;

public class ItemBlankUpgrade extends Item {
    public static ItemBlankUpgrade instance;
    IIcon texture;

    public ItemBlankUpgrade(String n) {
        super();
        instance = this;
        setCreativeTab(EnhancedPortals.instance.creativeTab);
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
