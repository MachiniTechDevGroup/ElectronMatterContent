package electronmattercontent;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EMCUpgrade extends Item {
	public static final String[] upnames = new String[] { "EMC Storage", "Smelting", "Compressing", "Grinding", "Freezing" };
	public static final int UP_COUNT = upnames.length;

	public static final int STORAGE = 0;
	public static final int SMELT = 1;
	public static final int COMPRESS = 2;
	public static final int GRIND = 3;
	public static final int FREEZE = 4;
	public static final int NONE = -1;

	private Icon[] icons = new Icon[UP_COUNT];

	public EMCUpgrade(int id) {
		super(id);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setUnlocalizedName("emcupgrade");

		for (int i = 0; i < EMCUpgrade.UP_COUNT; i++) {
			LanguageRegistry.addName(new ItemStack(this, 1, i), upnames[i] + " Upgrade");
			GameRegistry.registerItem(this, this.getUnlocalizedName(new ItemStack(this, 1, i)).substring(5));
		}
		this.setMaxStackSize(16);
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < EMCUpgrade.UP_COUNT; i++) {
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		return this.getUnlocalizedName() + is.getItemDamage();
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		for (int i = 0; i < UP_COUNT; i++) {
			this.icons[i] = par1IconRegister.registerIcon("EMC:" + this.getUnlocalizedName().substring(5) + i);
		}
	}
}
