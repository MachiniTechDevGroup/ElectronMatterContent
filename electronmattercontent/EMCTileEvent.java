package electronmattercontent;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class EMCTileEvent extends Event {
	/**
	 * General Event, should post upon {@code validate()} or {@code invalidate()}
	 * @param tile The TileEntity to register
	 * @param validate To validate or not to validate?
	 */
	private IEMCTile tile;
	private boolean validate;
	public EMCTileEvent (IEMCTile tile, boolean validate) {
		this.tile = tile;
		this.validate = validate;
	}
	
	public IEMCTile getEMCTile() {
		return this.tile;
	}
	
	public boolean isTileBeingLoaded() {
		return this.validate;
	}
}
