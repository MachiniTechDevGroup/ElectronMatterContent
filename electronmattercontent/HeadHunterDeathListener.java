package electronmattercontent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class HeadHunterDeathListener {
	@ForgeSubscribe
	public void onDeath(LivingDeathEvent event) {
		Entity src = event.source.getSourceOfDamage();
		if (src instanceof EntityPlayer) {
			if (((EntityPlayer)src).getCurrentEquippedItem() != null && ((EntityPlayer)src).getCurrentEquippedItem().getItem() instanceof SwordHeadHunter) {
				ItemStack toinc = ((EntityPlayer)src).getCurrentEquippedItem();
				if (toinc.stackTagCompound != null) {
					toinc.stackTagCompound.setInteger("Head", toinc.stackTagCompound.getInteger("Head") + 1);
				}
			}
		}
	}
}
