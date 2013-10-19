package electronmattercontent;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;

public class TileEntityHugeChest extends TileEntityChest {
	private ItemStack[] inv = new ItemStack[144];
	private int stacksStored;
	private String custName;

	public int getProgressScaled(int height) {
		return this.stacksStored * height / this.inv.length;
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		this.inv[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public ItemStack getStackInSlot(int par1) {
		return this.inv[par1];
	}
	
	@Override
	public int getSizeInventory() {
		return this.inv.length;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if (this.inv[par1] != null) {
			ItemStack itemstack;

			if (this.inv[par1].stackSize <= par2) {
				itemstack = this.inv[par1];
				this.inv[par1] = null;
				this.onInventoryChanged();
				return itemstack;
			} else {
				itemstack = this.inv[par1].splitStack(par2);

				if (this.inv[par1].stackSize == 0) {
					this.inv[par1] = null;
				}

				this.onInventoryChanged();
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.inv[par1] != null) {
			ItemStack itemstack = this.inv[par1];
			this.inv[par1] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
		this.inv = new ItemStack[this.getSizeInventory()];

		if (par1NBTTagCompound.hasKey("CustomName")) {
			this.custName = par1NBTTagCompound.getString("CustomName");
		}

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < this.inv.length) {
				this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inv.length; ++i) {
			if (this.inv[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.inv[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		par1NBTTagCompound.setTag("Items", nbttaglist);

		if (this.isInvNameLocalized()) {
			par1NBTTagCompound.setString("CustomName", this.custName);
		}
	}
}
