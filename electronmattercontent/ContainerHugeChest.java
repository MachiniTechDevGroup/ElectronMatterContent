package electronmattercontent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;

public class ContainerHugeChest extends ContainerChest {
	private TileEntityHugeChest te;
	public ContainerHugeChest(TileEntityHugeChest entity, InventoryPlayer inv) {
		super(entity, inv);
		this.te = entity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return te.isUseableByPlayer(var1);
	}

}
