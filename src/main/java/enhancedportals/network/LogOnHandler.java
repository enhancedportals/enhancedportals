package enhancedportals.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import enhancedportals.EnhancedPortals;
import enhancedportals.util.Localization;

public class LogOnHandler {
	
	@SubscribeEvent
    public void onLogIn(PlayerEvent.PlayerLoggedInEvent login) {
        if (login.player != null && !ProxyCommon.UPDATE_LATEST_VER.equals(EnhancedPortals.MOD_VERSION)) {
            EntityPlayer player = login.player;
            String lateVers = ProxyCommon.UPDATE_LATEST_VER;
            player.addChatMessage(new ChatComponentText(String.format(Localization.get("updateTo"), ProxyCommon.UPDATE_LATEST_VER) + " :: " + String.format(Localization.get("updateFrom"), EnhancedPortals.MOD_VERSION)));
        }
    }
	
}
