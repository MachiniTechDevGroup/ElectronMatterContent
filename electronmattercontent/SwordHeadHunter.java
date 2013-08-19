package electronmattercontent;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class SwordHeadHunter extends ItemSword {
	private boolean charge = true;
	private int cdur = 0;
	private String wielder = null;
	private Icon icon;
	private int ccool = 240;
	private int heads = 0;
	public static final DamageSource hhs = new DamageSourceHead("death.weapon.headhunter");

	public SwordHeadHunter(int par1) {
		super(par1, EMC.mat);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("An ancient sword infected with an endless desire for... HEADS!");
		if (this.wielder != null) {
			par3List.add("This sword has been bound to " + wielder + ".");
			par3List.add("Anybody else who attempts to pick this up shall die.");
			par3List.add(heads + (heads == 1 ? " HEAD!!!" : " HEADS!!!"));
		}
	}

	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.none;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (this.charge && par3EntityPlayer.username.equals(wielder)) {
			par3EntityPlayer.addPotionEffect(new PotionEffect(1, 80, 2));
			par3EntityPlayer.addPotionEffect(new PotionEffect(5, 60, 0));
			cdur = 80;
			this.charge = false;
		}

		return par1ItemStack;
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		par1ItemStack.stackTagCompound = new NBTTagCompound();
		par1ItemStack.stackTagCompound.setString("Owner", par3EntityPlayer.username);
		par1ItemStack.stackTagCompound.setInteger("Head", 0);
		this.wielder = par3EntityPlayer.username;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		if (this.wielder != null) {
			if (!((EntityPlayer) par3Entity).username.equals(wielder)) {
				((EntityLiving) par3Entity).addPotionEffect(new PotionEffect(9, 72000));
				((EntityLiving) par3Entity).addPotionEffect(new PotionEffect(18, 72000));
				((EntityLiving) par3Entity).addPotionEffect(new PotionEffect(20, 72000));
			}

			if (cdur != 0) {
				if (--cdur == 0) {
					((EntityLiving) par3Entity).removePotionEffect(1);
					((EntityLiving) par3Entity).removePotionEffectClient(1);
					this.charge = true;
				} else if (cdur == 20) {
					((EntityLiving) par3Entity).removePotionEffect(5);
					((EntityLiving) par3Entity).removePotionEffectClient(5);
					((EntityLiving) par3Entity).addPotionEffect(new PotionEffect(5, 20, 2));
				}
			}
			if (par1ItemStack.stackTagCompound != null) {
				this.heads = par1ItemStack.stackTagCompound.getInteger("Head");
			}
		} else {
			if (par1ItemStack.stackTagCompound == null) {
				par1ItemStack.stackTagCompound = new NBTTagCompound();
			}
			this.wielder = ((EntityPlayer) par3Entity).username;
			par1ItemStack.stackTagCompound.setInteger("Head", 0);
			par1ItemStack.stackTagCompound.setString("Owner", this.wielder);
		}
	}
}
