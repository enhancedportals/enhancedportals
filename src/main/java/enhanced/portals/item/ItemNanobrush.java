package enhanced.portals.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import enhanced.base.item.ItemBase;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.utility.Reference.EPMod;

public class ItemNanobrush extends ItemBase {
    public ItemNanobrush(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
        setMaxStackSize(1);
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }
}
