package enhanced.portals;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
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
import cpw.mods.fml.relauncher.Side;
import enhanced.base.mod.BaseMod;
import enhanced.base.xmod.ComputerCraft;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.network.ProxyCommon;
import enhanced.portals.portal.NetworkManager;
import enhanced.portals.tile.TileController;

@Mod(name = EPMod.name, modid = EPMod.ID, version = EPMod.version, dependencies = EPMod.dependencies)
public class EnhancedPortals extends BaseMod implements OrderedLoadingCallback {
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
        ForgeChunkManager.setForcedChunkLoadingCallback(instance, this);
    }

    // World Events

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.networkManager = new NetworkManager(event);
    }

    @SubscribeEvent
    public void worldSave(WorldEvent.Save event) {
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            proxy.networkManager.saveAllData();
    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket ticket : tickets) {
            int x = ticket.getModData().getInteger("controllerX");
            int y = ticket.getModData().getInteger("controllerY");
            int z = ticket.getModData().getInteger("controllerZ");
            TileEntity tile = world.getTileEntity(x, y, z);
            
            if (tile instanceof TileController)
                ((TileController) tile).forceChunk(ticket);
        }
    }

    @Override
    public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
        List<Ticket> valid = new ArrayList<Ticket>();
        
        for (Ticket ticket : tickets) {
            int x = ticket.getModData().getInteger("controllerX");
            int y = ticket.getModData().getInteger("controllerY");
            int z = ticket.getModData().getInteger("controllerZ");
            TileEntity tile = world.getTileEntity(x, y, z);
            
            if (tile instanceof TileController)
                valid.add(ticket);
        }
        
        return valid;
    }
}
