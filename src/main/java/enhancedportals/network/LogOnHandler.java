package enhancedportals.network;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import enhancedportals.EnhancedPortals;

public class LogOnHandler {
    boolean displayed;
    
    @SubscribeEvent
    public void onLogIn(PlayerEvent.PlayerLoggedInEvent login) {
        if (!displayed && login.player != null && !CommonProxy.UPDATE_LATEST_VER.equals(EnhancedPortals.MOD_VERSION)) {
            EntityPlayer player = login.player;
            String lateVers = CommonProxy.UPDATE_LATEST_VER;
            CommonProxy.Notify(player, lateVers);
            displayed = true;
        }
    }
}
