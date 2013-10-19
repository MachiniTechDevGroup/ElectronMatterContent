package electronmattercontent;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public class EMCNetwork {

	private List<IEMCTile> tiles = new ArrayList<IEMCTile>();
	private int totalEMC = 0;
	private World world;
	
	public EMCNetwork (World world) {
		this.world = world;
	}

	public static void init() {
		new EventListener();
	}

	public List<IEMCTile> getTiles() {
		return tiles;
	}
	
	public boolean addTileEntity(TileEntity tile) {
		try {
			tiles.add((IEMCTile)tile);
		} catch (Exception e) {
			EMC.warn("Tried to add a tile entity to an EMCNetwork, but tile doesn't implement IEMCTile!");
			return false;
		}
		return true;
	}
	
	public boolean removeTileEntity(TileEntity tile) {
		try {
			tiles.remove(tile);
		} catch (Exception e) {
			EMC.warn("Tried to remove a tile entity from an EMCNetwork, but tile doesn't exist!");
			return false;
		}
		return true;
	}

	private static class EventListener {
		private EventListener() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		@ForgeSubscribe
		public void onEMCNetEvent(EMCTileEvent event) {

		}
	}

}
