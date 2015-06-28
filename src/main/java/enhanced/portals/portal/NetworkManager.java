package enhanced.portals.portal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import enhanced.base.utilities.BidiArrayMap;
import enhanced.base.utilities.BidiMap;
import enhanced.base.utilities.DimensionCoordinates;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.tile.TileController;

public class NetworkManager {
    BidiMap<String, DimensionCoordinates> portalCoords;
    BidiArrayMap<String, String> portalNetwrks;
    File portalFile, networkFile;
    MinecraftServer server;

    public NetworkManager(FMLServerStartingEvent event) {
        server = event.getServer();

        portalCoords = new BidiMap<String, DimensionCoordinates>();
        portalNetwrks = new BidiArrayMap<String, String>();

        portalFile = new File(EnhancedPortals.proxy.getWorldDir(), "EP3_PortalLocations.json");
        networkFile = new File(EnhancedPortals.proxy.getWorldDir(), "EP3_PortalNetworks.json");

        try {
            loadAllData();
        } catch (Exception e) {
            EnhancedPortals.instance.getLogger().catching(e);
        }
    }

    public void saveAllData() {
        makeFiles();

        try {
            Gson gson = new GsonBuilder().create();
            FileWriter portalWriter = new FileWriter(portalFile), networkWriter = new FileWriter(networkFile);

            gson.toJson(portalCoords.getMap(), portalWriter);
            gson.toJson(portalNetwrks.getMap(), networkWriter);

            portalWriter.close();
            networkWriter.close();
        } catch (Exception e) {
            EnhancedPortals.instance.getLogger().catching(e);
        }
    }

    public void loadAllData() throws Exception {
        if (!makeFiles()) return;

        Type type = new TypeToken<HashMap<String, DimensionCoordinates>>() { }.getType();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String portalData = FileUtils.readFileToString(portalFile), networkData = FileUtils.readFileToString(networkFile);

        HashMap<String, DimensionCoordinates> portalCoordinates = gson.fromJson(portalData, type);
        HashMap<String, String> portalNetworks = gson.fromJson(networkData, new HashMap<String, String>().getClass());
        HashMap<String, DimensionCoordinates> portalDbs = gson.fromJson(portalData, type);

        if (portalCoordinates == null)
            portalCoordinates = new HashMap<String, DimensionCoordinates>();

        if (portalNetworks == null)
            portalNetworks = new HashMap<String, String>();

        if (portalDbs == null)
            portalDbs = new HashMap<String, DimensionCoordinates>();

        if (!portalCoordinates.isEmpty())
            for (Entry<String, DimensionCoordinates> entry : portalCoordinates.entrySet())
                portalCoords.add(entry.getKey(), entry.getValue());

        if (!portalNetworks.isEmpty())
            for (Entry<String, String> entry : portalNetworks.entrySet())
                portalNetwrks.add(entry.getKey(), entry.getValue());
    }

    private boolean makeFiles() {
        try {
            if (!portalFile.exists())
                portalFile.createNewFile();

            if (!networkFile.exists())
                networkFile.createNewFile();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setPortalUID(TileController controller, GlyphIdentifier g) {
        if (g == null || g.size() == 0) {
            if (hasNID(controller))
                removePortalNID(controller);

            removePortalUID(controller);
            return true;
        }

        if (UIDInUse(g))
            return false;

        if (hasUID(controller)) {
            if (hasNID(controller))
                removePortalNID(controller);

            removePortalUID(controller);
        }

        portalCoords.add(g.getGlyphString(), controller.getDimensionCoordinates());
        return true;
    }

    public boolean setPortalNID(TileController controller, GlyphIdentifier g) {
        if (!hasUID(controller))
            return false;

        if (hasNID(controller))
            removePortalNID(controller);

        portalNetwrks.add(getPortalUID(controller), g.getGlyphString());
        return true;
    }

    public boolean hasUID(TileController controller) {
        return portalCoords.containsSecond(controller.getDimensionCoordinates());
    }

    public boolean hasNID(TileController controller) {
        return portalNetwrks.contains(getPortalUID(controller));
    }

    public boolean UIDInUse(GlyphIdentifier g) {
        return portalCoords.contains(g.getGlyphString());
    }

    public void removePortalUID(TileController controller) {
        if (hasUID(controller))
            portalCoords.removeSecond(controller.getDimensionCoordinates());
    }

    public void removePortalNID(TileController controller) {
        if (hasNID(controller))
            portalNetwrks.remove(getPortalUID(controller));
    }

    public String getPortalUID(TileController controller) {
        return hasUID(controller) ? portalCoords.getSecond(controller.getDimensionCoordinates()) : null;
    }

    public String getPortalNID(TileController controller) {
        return hasNID(controller) ? portalNetwrks.get(getPortalUID(controller)) : null;
    }

    public TileController getPortalController(GlyphIdentifier g) {
        DimensionCoordinates c = portalCoords.get(g.getGlyphString());
        TileController controller = (TileController) c.getTileEntity();
        return controller;
    }

    public ArrayList<String> getNetworkedPortals(GlyphIdentifier g) {
        return networkExists(g) ? portalNetwrks.getList(g.getGlyphString()) : new ArrayList<String>();
    }

    public int getNetworkedPortalsCount(GlyphIdentifier g) {
        return networkExists(g) ? portalNetwrks.getList(g.getGlyphString()).size() : -1;
    }

    boolean networkExists(GlyphIdentifier g) {
        return portalNetwrks.containsSecond(g.getGlyphString());
    }

    public GlyphIdentifier getNetworkedPortalNext(TileController controller) {
        ArrayList<String> network = getNetworkedPortals(new GlyphIdentifier(getPortalNID(controller)));
        String current = getPortalUID(controller);
        int index = network.indexOf(current);

        if (index == network.size() - 1)
            return new GlyphIdentifier(network.get(0));

        return new GlyphIdentifier(network.get(index + 1));
    }
}
