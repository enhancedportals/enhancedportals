package enhancedportals.util;

import net.minecraft.util.ChunkCoordinates;

public class WorldCoordinates extends ChunkCoordinates {
	int world;
	
	public WorldCoordinates(int x, int y, int z, int d) {
		posX = x;
		posY = y;
		posZ = z;
		world = d;
	}
	
	public WorldCoordinates(ChunkCoordinates c, int d) {
		posX = c.posX;
		posY = c.posY;
		posZ = c.posZ;
		world = d;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + world << 32;
	}
	
	@Override
	public String toString() {
		return "Pos{x=" + this.posX + ", y=" + this.posY + ", z=" + this.posZ + ", d=" + world + "}";
	}
}
