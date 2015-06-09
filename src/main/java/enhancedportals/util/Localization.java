package enhancedportals.util;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class Localization {
	public static String get(String key) {
		return StatCollector.translateToLocal(key);
	}
	
	public static String getError(String key) {
		return EnumChatFormatting.RED + get("error") + EnumChatFormatting.WHITE + get(key);
	}
	
	public static String getSuccess(String key) {
		return EnumChatFormatting.GREEN + get("success") + EnumChatFormatting.WHITE + get(key);
	}
}
