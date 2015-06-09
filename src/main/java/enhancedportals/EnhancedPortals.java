package enhancedportals;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.LoggerConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import enhancedportals.base.CreativeTab;
import enhancedportals.network.ProxyCommon;

@Mod(name = EnhancedPortals.MOD_NAME, modid = EnhancedPortals.MOD_ID, version = EnhancedPortals.MOD_VERSION, dependencies = EnhancedPortals.MOD_DEPENDENCIES)
public class EnhancedPortals {
	public static final String MOD_NAME = "EnhancedPortals";
	public static final String MOD_ID = "enhancedportals";
	public static final String MOD_VERSION = "3.1.0A";
	public static final String MOD_DEPENDENCIES = "";
	
	@Instance(MOD_ID)
	public static EnhancedPortals instance;

	@SidedProxy(clientSide = "enhancedportals.network.ProxyClient", serverSide = "enhancedportals.network.ProxyCommon")
	public static ProxyCommon proxy;
	
	public EnhancedPortals() {
		LoggerConfig fml = new LoggerConfig(FMLCommonHandler.instance().getFMLLogger().getName(), Level.ALL, true);
        LoggerConfig modConf = new LoggerConfig(ProxyCommon.logger.getName(), Level.ALL, true);
        modConf.setParent(fml);
        MinecraftForge.EVENT_BUS.register(this);
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event) {
		proxy.init();
    }
	
	@EventHandler
    public void pre(FMLPreInitializationEvent event) {
		proxy.pre();
    }
	
	@EventHandler
    public void post(FMLPostInitializationEvent event) {
		proxy.post();
    }
}
