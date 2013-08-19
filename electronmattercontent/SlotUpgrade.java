package electronmattercontent;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot {

	private int meta;

	public SlotUpgrade(IInventory par1iInventory, int par2, int par3, int par4, int meta) {
		super(par1iInventory, par2, par3, par4);
		this.meta = meta;
	}
	
	@Override
	public boolean isItemValid(ItemStack is) {
		return (is.getItem() instanceof EMCUpgrade) && (is.getItemDamage() == this.meta);
	}

}
