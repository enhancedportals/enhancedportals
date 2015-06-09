package enhancedportals.portal.network;

import java.util.HashMap;
import java.util.List;

import enhancedportals.util.WorldCoordinates;

public class NetworkMap {
	/** Stores Portal UID -> WorldCoordinates **/
	static HashMap<String, WorldCoordinates> portalPositions = new HashMap<String, WorldCoordinates>(); // Lookup of pos from UID
	static HashMap<WorldCoordinates, String> portalPositionsReverse = new HashMap<WorldCoordinates, String>(); // Lookup of UID from pos
	
	/** Stores all active connections (UID <-> UID) **/
	static HashMap<String, String> activeConnections = new HashMap<String, String>(); // Lookup of portal B from A
	static HashMap<String, String> activeConnectionsReverse = new HashMap<String, String>(); // Lookup of portal A from B
	
	/** Stores all mappings to the stabilizers **/
	static HashMap<String, WorldCoordinates> handlingStabilizer = new HashMap<String, WorldCoordinates>(); // Lookup of Stabilizer from UID
	static HashMap<WorldCoordinates, List<String>> handlingStabilizerReverse = new HashMap<WorldCoordinates, List<String>>(); // Lookup of UIDs from Stabilizer
	
	/** Adds a portal position or updates portal UID to a new position **/
	public static void updatePortalPosition(String UID, WorldCoordinates pos) {
		if (portalPositions.containsKey(UID)) {
			portalPositions.remove(UID);
			portalPositionsReverse.remove(pos);
			portalPositions.put(UID, pos);
			portalPositionsReverse.put(pos, UID);
			System.out.println("Updated UID of: " + UID + " to pos: " + pos);
		} else if (portalPositionsReverse.containsKey(pos)) {
			updatePortalPosition(portalPositionsReverse.get(pos), UID);
		} else {
			portalPositions.put(UID, pos);
			portalPositionsReverse.put(pos, UID);
			System.out.println("Added UID of: " + UID + " at pos: " + pos);
		}
	}
	
	/** Updates a portal UID to a new UID **/
	public static void updatePortalPosition(String oldUID, String newUID) {
		if (portalPositions.containsKey(oldUID)) {
			WorldCoordinates pos = portalPositions.get(oldUID);
			portalPositions.remove(oldUID);
			portalPositionsReverse.remove(pos);
			portalPositions.put(newUID, pos);
			portalPositionsReverse.put(pos, newUID);
			System.out.println("Updated UID of: " + oldUID + " to: " + newUID + " at pos: " + pos);
		}
	}
	
	/** Removes a portal UID **/
	public static void removePortalPosition(String UID) {
		if (portalPositions.containsKey(UID)) {
			WorldCoordinates pos = portalPositions.get(UID);
			portalPositions.remove(UID);
			portalPositionsReverse.remove(pos);
			System.out.println("Removed UID of: " + UID + " at pos: " + pos);
		}
	}
	
	/** Connects two portals together **/
	public static void connectPortals(String UID, String UID2) {
		activeConnections.put(UID, UID2);
		activeConnectionsReverse.put(UID2, UID);
		System.out.println("Connected " + UID + " to " + UID2);
	}
	
	/** Disconnects two portals **/
	public static void disconnectPortals(String UID, String UID2) {
		if (activeConnections.containsKey(UID)) {
			activeConnections.remove(UID);
			activeConnectionsReverse.remove(UID2);
			System.out.println("Disconnected " + UID + " from " + UID2);
		} else if (activeConnections.containsKey(UID2)) {
			activeConnections.remove(UID2);
			activeConnectionsReverse.remove(UID);
			System.out.println("Disconnected " + UID2 + " from " + UID);
		}
	}
	
	/** Disconnects two portals giving only one UID **/
	public static void disconnectPortals(String UID) {
		if (activeConnections.containsKey(UID)) {
			activeConnectionsReverse.remove(activeConnections.get(UID));
			activeConnections.remove(UID);
			System.out.println("Disconnected " + UID);
		} else if (activeConnectionsReverse.containsKey(UID)) {
			activeConnections.remove(activeConnectionsReverse.get(UID));
			activeConnectionsReverse.remove(UID);
			System.out.println("Disconnected " + UID);
		}
	}
	
	/** Loads from JSON **/
	public static void load() {
		
	}
	
	/** Saves to JSON **/
	public static void save() {
		
	}

	/** Clears everything -- Should be called every time the server stops **/
	public static void clear() {
		portalPositions.clear();
		portalPositionsReverse.clear();
		activeConnections.clear();
		activeConnectionsReverse.clear();
		handlingStabilizer.clear();
		handlingStabilizerReverse.clear();
	}

	/** Gets the Portal UID from the position **/
	public static String getPortalUID(WorldCoordinates pos) {
		return portalPositionsReverse.get(pos);
	}
}
