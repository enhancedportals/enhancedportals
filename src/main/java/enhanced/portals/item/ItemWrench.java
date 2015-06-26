package enhanced.portals.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;
import enhanced.base.item.ItemBase;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.utility.Reference.EPMod;

public class ItemWrench extends ItemBase implements IToolWrench {
    public ItemWrench(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
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
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

    }
}
