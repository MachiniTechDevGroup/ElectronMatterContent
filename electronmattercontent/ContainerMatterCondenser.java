package electronmattercontent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerMatterCondenser extends Container {
	private TileEntityMatterCondenser te;
	private int lastEMC = 0;
	private int lastEng = 0;

	public ContainerMatterCondenser(TileEntityMatterCondenser entity, InventoryPlayer inventory) {
		this.te = entity;
		// EMC slot
		this.addSlotToContainer(new SlotEMC(te, 0, 8, 16));

		// EMC input slots
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				this.addSlotToContainer(new SlotEMCIn(te, 1 + x + (9 * y), 18 * x + 8, 18 * y + 44));
			}
		}

		// EMC output slots
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 3; y++) {
				this.addSlotToContainer(new SlotEMCOut(te, 28 + x + (9 * y), 18 * x + 8, 18 * y + 108));
			}
		}

		// EMC upgrade slots
		for (int y = 0; y < EMCUpgrade.UP_COUNT; y++) {
			this.addSlotToContainer(new SlotUpgrade(te, 55 + y, 174, 44 + 18 * y, y));
		}

		// Add the player's inventory
		int var3;

		for (var3 = 0; var3 < 3; ++var3) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(new Slot(inventory, var4 + var3 * 9 + 9, 19 + var4 * 18, 172 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3) {
			this.addSlotToContainer(new Slot(inventory, var3, 19 + var3 * 18, 230));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return te.isUseableByPlayer(var1);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotnum) {
		System.out.println("Slot Number: " + slotnum);
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotnum);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotnum >= 0 && slotnum <= 27) // Input / To
			{
				if (!this.mergeItemStack(itemstack1, 55, 91, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (slotnum >= 28 && slotnum <= 54 && !(this.getSlot(0).getStack().isItemEqual(this.getSlot(slotnum).getStack()))) // Ease of recondensing items
			{
				if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
					return null;
				}
			} else if (slotnum >= 28 && slotnum <= 54 && (this.getSlot(0).getStack().isItemEqual(this.getSlot(slotnum).getStack()))) {
				if (!this.mergeItemStack(itemstack1, 55, 91, false)) {
					return null;
				}
			} else if (slotnum > 59) // Player Inventory
			{
				if (EMCAPI.instance().hasEMCEnt(itemstack1) && this.getSlot(0).getStack() == null) {
					if (!this.mergeItemStack(itemstack1, 0, 1, true)) {
						return null;
					}
				} else if (EMCAPI.instance().hasEMCEnt(itemstack1) && this.getSlot(0).getStack() != null) {
					if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
						return null;
					}
				} else if (!EMCAPI.instance().hasEMCEnt(itemstack1)) {
					if (slotnum >= 87) // Place in inventory
					{
						if (!this.mergeItemStack(itemstack1, 60, 87, false)) {
							return null;
						}
					} else // Place in hotbar
					{
						if (!this.mergeItemStack(itemstack1, 87, 96, false)) {
							return null;
						}
					}
				} else if (itemstack1.getItem() instanceof EMCUpgrade) {
					switch (itemstack1.getItemDamage()) {
						case EMCUpgrade.STORAGE:
							if (!this.mergeItemStack(itemstack1, 55, 56, false)) {
								return null;
							}
							break;
						case EMCUpgrade.SMELT:
							if (!this.mergeItemStack(itemstack1, 56, 57, false)) {
								return null;
							}
							break;
						case EMCUpgrade.COMPRESS:
							if (!this.mergeItemStack(itemstack1, 57, 58, false)) {
								return null;
							}
							break;
						case EMCUpgrade.GRIND:
							if (!this.mergeItemStack(itemstack1, 58, 59, false)) {
								return null;
							}
							break;
						case EMCUpgrade.FREEZE:
							if (!this.mergeItemStack(itemstack1, 59, 60, false)) {
								return null;
							}
							break;
					}
				}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, this.te.progress);
		par1ICrafting.sendProgressBarUpdate(this, 1, this.te.ueJoules);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastEMC != this.te.progress) {
				icrafting.sendProgressBarUpdate(this, 0, this.te.progress);
			}

			if (this.lastEng != this.te.ueJoules) {
				icrafting.sendProgressBarUpdate(this, 1, this.te.ueJoules);
			}
		}

		this.lastEMC = this.te.progress;
		this.lastEng = this.te.ueJoules;
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if (par1 == 0) {
			this.te.progress = par2;
		}

		if (par1 == 1) {
			this.te.ueJoules = par2;
		}
	}
}
