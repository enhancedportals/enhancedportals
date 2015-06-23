package enhanced.portals;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import enhanced.base.mod.BaseMod;
import enhanced.base.xmod.ComputerCraft;
import enhanced.portals.block.BlockFrame;
import enhanced.portals.block.BlockPortal;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.ProxyCommon;
import enhanced.portals.portal.NetworkManager;

@Mod(name = EnhancedPortals.MOD_NAME, modid = EnhancedPortals.MOD_ID, version = EnhancedPortals.MOD_VERSION, dependencies = EnhancedPortals.MOD_DEPENDENCIES)
public class EnhancedPortals extends BaseMod {
    public static final String MOD_NAME = "Enhanced Portals", MOD_ID = "enhancedportals", MOD_ID_SHORT = "ep3", MOD_VERSION = "3.0.12", MOD_DEPENDENCIES = "required-after:enhancedcore", MOD_URL = "https://raw.githubusercontent.com/enhancedportals/VERSION/master/VERSION%20-%20Enhanced%20Portals";
    public static final String MODID_OPENCOMPUTERS = "OpenComputers";

    @Instance(MOD_ID)
    public static EnhancedPortals instance;

    @SidedProxy(clientSide = "enhanced.portals.network.ProxyClient", serverSide = "enhanced.portals.network.ProxyCommon")
    public static ProxyCommon proxy;

    public EnhancedPortals() {
        super(MOD_URL, MOD_ID, MOD_ID_SHORT, MOD_NAME, MOD_VERSION);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Startup

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event, proxy);
    }

    @EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ComputerCraft.registerPeripheralProvider(BlockFrame.instance);
        creativeTab.setItem(new ItemStack(BlockPortal.instance, 1));
    }

    // World Events

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.networkManager = new NetworkManager(event);
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event) {
        PotionEffect effect = event.entityLiving.getActivePotionEffect(ProxyCommon.featherfallPotion);

        if (effect != null) {
            event.entityLiving.fallDistance = 0f;

            if (event.entityLiving.getActivePotionEffect(ProxyCommon.featherfallPotion).getDuration() <= 0)
                event.entityLiving.removePotionEffect(ProxyCommon.featherfallPotion.id);
        }
    }

    @SubscribeEvent
    public void worldSave(WorldEvent.Save event) {
        if (!event.world.isRemote)
            proxy.networkManager.saveAllData();
    }
}
