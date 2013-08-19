package electronmattercontent;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class DamageSourceHead extends DamageSource {

	public DamageSourceHead(String par1Str) {
		super(par1Str);
	}

	public String getDeathMessage(EntityLiving par1EntityLiving) {
		EntityLiving entityliving1 = par1EntityLiving.func_94060_bK();
		String s = "death.attack." + this.damageType;
		String s1 = s + ".player";
		return entityliving1 != null && StatCollector.func_94522_b(s1) ? StatCollector.translateToLocalFormatted(s1, new Object[] { par1EntityLiving.getTranslatedEntityName(), entityliving1.getTranslatedEntityName() }) : StatCollector
				.translateToLocalFormatted(s, new Object[] { par1EntityLiving.getTranslatedEntityName() });
	}

}
