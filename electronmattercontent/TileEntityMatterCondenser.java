package electronmattercontent;

/*import ic2.api.Direction;
 import ic2.api.EnergyNet;
 import ic2.api.IEnergySink;
 import ic2.api.IEnergyTile;*/
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMatterCondenser extends TileEntity implements ISidedInventory/* , IEnergySink */{
	private ItemStack[] inv = new ItemStack[60];
	public ItemStack to = null;// The ItemStack to condense to
	private String name;
	int progress = 0;// The amount of emc it has condensed already
	private int emcreq = 0;// The amount of emc required
	private ItemStack[] up = new ItemStack[EMCUpgrade.UP_COUNT];
	private int emccap = 1000000;

	// IC2
	public static final int MAX_ENERGY = 100000;
	int energy = 0;
	private boolean net = false;

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.inv[slot];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack stack = getStackInSlot(i);

		if (i == 0) {
			this.emcreq = 0;
		}

		if (i > 54 && i <= 59) {
			this.up[i - 55] = null;
			if (i == 55) {
				this.emccap = 1000000;
			}
		}

		if (stack != null) {
			if (stack.stackSize <= j) {
				setInventorySlotContents(i, null);
			} else {
				stack = stack.splitStack(j);

				if (stack.stackSize == 0) {
					setInventorySlotContents(i, null);
				}
			}
		}

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);

		if (stack != null) {
			setInventorySlotContents(i, null);
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv[i] = itemstack;

		if (i == 0) {
			this.to = itemstack;

			if (itemstack != null) {
				this.emcreq = EMCAPI.instance().getEMCEntofItem(itemstack).getEMC();
			}
		}

		if (i > 54 && i <= 59) {
			this.up[i - 55] = itemstack;
			if (i == 55 && itemstack != null) {
				this.emccap = 1000000 + (250000 * itemstack.stackSize);
			}
		}

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return "Electron Rearranger";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
		this.inv = new ItemStack[this.getSizeInventory()];
		this.progress = par1NBTTagCompound.getInteger("EMC");
		this.emccap = par1NBTTagCompound.getInteger("Cap");

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.inv.length) {
				this.inv[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.to = this.inv[0];

		if (this.to != null) {
			this.emcreq = EMCAPI.instance().getEMCEntofItem(this.to).getEMC();
		} else {
			this.emcreq = 0;
		}

		for (int i = 0; i < EMCUpgrade.UP_COUNT; i++) {
			this.up[i] = this.inv[i + 55];
		}

		if (par1NBTTagCompound.hasKey("CustomName")) {
			this.name = par1NBTTagCompound.getString("CustomName");
		}
	}

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
		par1NBTTagCompound.setInteger("EMC", this.progress);
		par1NBTTagCompound.setInteger("Cap", this.emccap);

		if (this.isInvNameLocalized()) {
			par1NBTTagCompound.setString("CustomName", this.name);
		}
	}

	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public void updateEntity() {
		/*
		 * if (EMC.IC2) // IC2 { if (this.net != this.isPartofENet()) { this.setIC2ENetStatus(this.isPartofENet()); } }
		 */

		// Self
		if (this.to != null) {
			this.condenseItem();

			if (this.canCondenseTo()) {
				this.doCondense();
			}
		}
	}

	/**
	 * Can you condense?
	 * @return A boolean stating whether the condenser can use its stored emc to condense an item
	 */
	private boolean canCondenseTo() {
		if (EMC.IC2) {
			return this.progress >= this.emcreq && this.energy >= this.computeEnergyUsage();
		}

		return this.progress >= this.emcreq;
	}

	/**
	 * Condenses a single item into emc, if possible
	 */
	private void condenseItem() {
		for (int s = 1; s < 28; s++) {
			if (this.inv[s] != null) // Found one!
			{
				if (EMCAPI.instance().hasEMCEnt(this.inv[s])) {
					if (this.progress + EMCAPI.instance().getEMCEntofItem(this.inv[s]).getEMC() > this.emccap) {// Over capacity.
						return;
					}
					this.progress += EMCAPI.instance().getEMCEntofItem(this.inv[s]).getEMC();
					this.inv[s].stackSize--;

					if (this.inv[s].stackSize == 0) {
						this.inv[s] = null;
					}
					return;
				}
			}
		}
	}

	/**
	 * Condenses an item from the stored emc
	 */
	private void doCondense() {
		int slot = this.findFirstOutputSlot();

		if (slot == -1) {
			return;
		}

		if (this.inv[slot] == null) {
			this.inv[slot] = this.to.copy();
			this.inv[slot].stackSize = 1;// That's a bug, Dave!
			this.progress -= EMCAPI.instance().getEMCEntofItem(this.to).getEMC();
			this.energy -= this.computeEnergyUsage();
		} else {
			this.inv[slot].stackSize++;
			this.progress -= EMCAPI.instance().getEMCEntofItem(this.to).getEMC();
			this.energy -= this.computeEnergyUsage();
		}
	}

	public void setName(String displayName) {
		this.name = displayName;
	}

	private int findFirstOutputSlot() // Gets the first available output slot
	{
		for (int s = 28; s < 55; s++) {
			if (this.to != null) {
				if (this.inv[s] == null) // Should not crash, conditional or
				{
					// evaluates null first
					return s;
				} else if (this.inv[s].isItemEqual(this.to)) {
					if (this.inv[s].stackSize != this.inv[s].getMaxStackSize()) {
						return s;
					}
				}
			}
		}

		return -1;
	}

	/**
	 * For ease of use with the GUI.
	 * @param width The width in pixels of the arrow
	 * @return A scaled integer to aid with {@code drawTexturedModalRect}
	 */
	public int getProgressScaled(int width) {
		if (this.emcreq == 0) // No crashing allowed.
		{
			return 0;
		}

		int p = (this.progress * width) / this.emcreq;

		if (p > 56) {
			return 56;
		}

		return p;
	}

	private int computeEnergyUsage() {
		return (int) (1000 * EMCAPI.instance().getEMCEntofItem(this.to).getRef());
	}

	/*
	 * private boolean isPartofENet() { return this.worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord) instanceof IEnergyTile || this.worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord) instanceof IEnergyTile ||
	 * this.worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord) instanceof IEnergyTile || this.worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord) instanceof IEnergyTile || this.worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1) instanceof
	 * IEnergyTile || this.worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1) instanceof IEnergyTile; } private void setIC2ENetStatus(boolean net) { this.net = net; try { if (net) { EnergyNet.getForWorld(this.worldObj).addTileEntity(this); } else {
	 * EnergyNet.getForWorld(this.worldObj).removeTileEntity(this); } } catch (Exception e) { e.printStackTrace(); } }
	 * @Override public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) { return true; }
	 * @Override public boolean isAddedToEnergyNet() { return this.net; }
	 * @Override public boolean demandsEnergy() { return this.energy > MAX_ENERGY; }
	 * @Override public int injectEnergy(Direction directionFrom, int amount) { this.energy += amount; int e = 0; if (this.energy > MAX_ENERGY) { e = this.energy - MAX_ENERGY; } return e; }
	 */

	@Override
	public boolean isStackValidForSlot(int slot, ItemStack itemstack) {
		if (slot >= 0 && slot < 28) {
			return EMCAPI.instance().hasEMCEnt(itemstack);
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		int[] res = null;
		switch (var1) {// pl means placeholder. Because Java is kinda stupid and only allows {x, y, z} in array initialisers.
			case 0:
				int[] pl1 = { 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54 };
				res = pl1;
				break;
			case 1:
				int[] pl2 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27 };
				res = pl2;
				break;
			default:
				res = new int[2];
				res[0] = 17;
				res[1] = 18;
				break;// Even though it's not really necessary
		}
		return res;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		if (side == 0) {
			return false;
		} else if (side == 1) {
			if (slot >= 0 && slot < 28) {
				return EMCAPI.instance().hasEMCEnt(itemstack);
			}
		} else {
			if (slot >= 55 && slot < 60) {
				return itemstack.getItem() instanceof EMCUpgrade;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		if (side == 0) {
			return slot >= 28 && slot < 55;
		}
		return false;
	}
}
