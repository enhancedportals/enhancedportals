package enhanced.portals;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
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
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.ProxyCommon;
import enhanced.portals.portal.NetworkManager;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPMod;

@Mod(name = EPMod.name, modid = EPMod.ID, version = EPMod.version, dependencies = EPMod.dependencies)
public class EnhancedPortals extends BaseMod {
    @Instance(EPMod.ID)
    public static EnhancedPortals instance;

    @SidedProxy(clientSide = EPMod.proxyClient, serverSide = EPMod.proxyCommon)
    public static ProxyCommon proxy;

    public EnhancedPortals() {
        super(EPMod.url, EPMod.ID, EPMod.shortID, EPMod.name, EPMod.version);
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
        ComputerCraft.registerPeripheralProvider(EPBlocks.frame);
        creativeTab.setItem(new ItemStack(EPBlocks.portal, 1));
    }

    // World Events

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.networkManager = new NetworkManager(event);
    }

    @SubscribeEvent
    public void worldSave(WorldEvent.Save event) {
        if (!event.world.isRemote)
            proxy.networkManager.saveAllData();
    }
}
