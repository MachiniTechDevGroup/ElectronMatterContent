package electronmattercontent;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMatterCondenser extends BlockContainer {
	private static boolean keepInventory;
	private Random rand = new Random();
	private Icon icon;
	private Icon front;
	private Icon top;
	private Icon bottom;
	private Icon side;

	protected BlockMatterCondenser(int par1) {
		super(par1, Material.iron);
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setResistance(15f);
		this.setHardness(2f);
		this.setTickRandomly(true);
		this.setStepSound(soundMetalFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 2);
		LanguageRegistry.addName(this, "Electron Matter Content Condenser");
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityMatterCondenser();
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		if (par5EntityPlayer.isSneaking()) {
			return false;
		}

		if (!world.isRemote) {
			TileEntityMatterCondenser te = (TileEntityMatterCondenser) world.getBlockTileEntity(par2, par3, par4);

			if (te != null) {
				par5EntityPlayer.openGui(EMC.instance, 0, world, par2, par3, par4);
			}
		}

		return true;
	}

	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		super.onBlockAdded(par1World, par2, par3, par4);
		this.setDefaultDirection(par1World, par2, par3, par4);
	}

	private void setDefaultDirection(World par1World, int par2, int par3, int par4) {
		if (!par1World.isRemote) {
			int l = par1World.getBlockId(par2, par3, par4 - 1);
			int i1 = par1World.getBlockId(par2, par3, par4 + 1);
			int j1 = par1World.getBlockId(par2 - 1, par3, par4);
			int k1 = par1World.getBlockId(par2 + 1, par3, par4);
			byte b0 = 3;

			if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
				b0 = 3;
			}

			if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
				b0 = 2;
			}

			if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
				b0 = 5;
			}

			if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
				b0 = 4;
			}

			par1World.setBlockMetadataWithNotify(par2, par3, par4, b0, 2);
		}
	}

	public static void update(boolean active, World par1World, int xCoord, int yCoord, int zCoord) {
		int l = par1World.getBlockMetadata(xCoord, yCoord, zCoord);
		TileEntity tileentity = par1World.getBlockTileEntity(xCoord, yCoord, zCoord);
		par1World.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, l, 2);

		if (tileentity != null) {
			tileentity.validate();
			par1World.setBlockTileEntity(xCoord, yCoord, zCoord, tileentity);
		}
	}

	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		if (!keepInventory) {
			TileEntityMatterCondenser tileentity = (TileEntityMatterCondenser) par1World.getBlockTileEntity(par2, par3, par4);

			if (tileentity != null) {
				for (int j1 = 0; j1 < tileentity.getSizeInventory(); ++j1) {
					ItemStack itemstack = tileentity.getStackInSlot(j1);

					if (itemstack != null) {
						float f = this.rand.nextFloat() * 0.8F + 0.1F;
						float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
						float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

						while (itemstack.stackSize > 0) {
							int k1 = this.rand.nextInt(21) + 10;

							if (k1 > itemstack.stackSize) {
								k1 = itemstack.stackSize;
							}

							itemstack.stackSize -= k1;
							EntityItem entityitem = new EntityItem(par1World, (double) ((float) par2 + f), (double) ((float) par3 + f1), (double) ((float) par4 + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));

							if (itemstack.hasTagCompound()) {
								entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
							}

							float f3 = 0.05F;
							entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
							entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
							entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
							par1World.spawnEntityInWorld(entityitem);
						}
					}
				}
			}
		}

		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
		int l = MathHelper.floor_double((double) (par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (l == 0) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
		}

		if (l == 1) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
		}

		if (l == 2) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
		}

		if (l == 3) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
		}

		if (par6ItemStack.hasDisplayName()) {
			((TileEntityMatterCondenser) par1World.getBlockTileEntity(par2, par3, par4)).setName(par6ItemStack.getDisplayName());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister reg) {
		front = reg.registerIcon("EMC:matterfront");
		top = reg.registerIcon("EMC:mattertop");
		bottom = reg.registerIcon("EMC:matterbottom");
		side = reg.registerIcon("EMC:matterside");
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		if (par1 == 3 && par2 == 0) {
			return front;
		}
		return par1 == 0 ? bottom : (par1 == 1 ? top : (par1 != par2 ? side : front));
	}

}
