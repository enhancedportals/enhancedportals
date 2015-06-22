package enhanced.portals.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;
import enhanced.portals.EnhancedPortals;

public class ItemWrench extends Item implements IToolWrench {
    public static ItemWrench instance;

    IIcon texture;

    public ItemWrench(String n) {
        super();
        instance = this;
        setCreativeTab(EnhancedPortals.instance.creativeTab);
        setUnlocalizedName(n);
        setMaxStackSize(1);
    }

    @Override
    public boolean canWrench(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return texture;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        texture = register.registerIcon("enhancedportals:wrench");
    }

    @Override
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

    }
}
