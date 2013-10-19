package electronmattercontent;

import static electronmattercontent.EMCAPI.instance;
import static ic2.api.item.Items.getItem;

import ic2.api.item.Items;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "modEMC", name = "Electron Matter Content", version = "1.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = "CondenserSync")
public class EMC implements IConfigureNEI {
	@Instance("modEMC")
	public static EMC instance;
	@SidedProxy(clientSide = "electronmattercontent.ClientProxy", serverSide = "electronmattercontent.CommonProxy")
	public static CommonProxy proxy;
	public static BlockMatterCondenser mblk = new BlockMatterCondenser(2900);
	public static BlockHugeChest hchst = new BlockHugeChest(2903);
	public static SwordHeadHunter swrd;
	public static Configuration config;
	public static boolean IC2 = false;
	public static boolean BC = false;
	public static boolean UE = false;
	public static boolean HARD_MODE = false;
	public static EnumToolMaterial mat;
	public static EMCUpgrade up;
	public static final AchievementHandler ahand = new AchievementHandler();
	public static Achievement achmat;
	private static Logger log;

	@PreInit
	public void pre(FMLPreInitializationEvent event) {
		log = Logger.getLogger("ElectronMatterContent");
		log.setParent(FMLLog.getLogger());
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		IC2 = config.get("Energy", "Industrial Craft 2", false).getBoolean(false);
		BC = config.get("Energy", "Buildcraft", false).getBoolean(false);
		UE = config.get("Energy", "Universal Electricity", false).getBoolean(false);
		HARD_MODE = isModLoaded("GT_Addon") && config.get("Difficulty", "Hard(GregTech)", true).getBoolean(true);
		config.save();
	}

	@Init
	public void go(FMLInitializationEvent event) {
		addDefEMC();
		mat = EnumHelper.addToolMaterial("HeadHunter", 2, 256, 6f, 4, 20);
		mblk.setUnlocalizedName("Matter");
		up = new EMCUpgrade(config.get("ID", "EMC Upgrades", 2902).getInt(2902) - 256);
		swrd = new SwordHeadHunter(config.get("ID", "Sword", 2901).getInt(2901) - 256);
		swrd.setUnlocalizedName("Sword");
		GameRegistry.registerItem(swrd, "head");
		LanguageRegistry.addName(swrd, "Head Hunter's Sword");
		GameRegistry.registerBlock(mblk, "matterblock");
		GameRegistry.registerBlock(hchst, "hugechest");
		GameRegistry.registerTileEntity(TileEntityMatterCondenser.class, "modEMCCond");
		GameRegistry.registerTileEntity(TileEntityHugeChest.class, "modEMCChst");
		LanguageRegistry.addName(mblk, "Electron Rearranger");
		LanguageRegistry.addName(hchst, "Huge Chest");
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiHelper());
		//NetworkRegistry.instance().registerChannel(new PacketHandler(), "CondenserSync");
		// GameRegistry.addShapedRecipe(new ItemStack(swrd), "sic", "wdz", "ptp", Character.valueOf('s'), new ItemStack(397, 1, 0), Character.valueOf('c'), new ItemStack(397, 1, 4), Character.valueOf('w'), new ItemStack(397, 1, 1), Character.valueOf('z'),
		// new ItemStack(397, 1, 2), Character.valueOf('p'), new ItemStack(397, 1, 3), Character.valueOf('d'), Item.diamond, Character.valueOf('t'), Item.stick, Character.valueOf('i'), Item.ingotIron);
	}

	@PostInit
	public void somethingOrOther(FMLPostInitializationEvent event) {
		addModEMC();
		new Thread(new AutoEMCDeterminer()).run();
		// EMCAPI.printAllEMCValues();
		// println("Fence:" + EMCAPI.hasEMC(new ItemStack(Block.fence)));
		MinecraftForge.EVENT_BUS.register(new HeadHunterDeathListener());
		if (HARD_MODE) {
			becomeHardMode();
		}
		GameRegistry.registerCraftingHandler(ahand);
		achmat = new Achievement(config.get("Misc", "Achievement ID", 256).getInt(256), "CondMat", -1, 8, mblk, AchievementList.portal).registerAchievement();
		LanguageRegistry.instance().addStringLocalization("achievement.CondMat", "en_US", "Condesed Condenser!");
		LanguageRegistry.instance().addStringLocalization("achievement.CondMat.desc", "en_US", "Just HOW condensed can something be?");
	}

	private void addModEMC() {
		instance().addEMCtoOreDictName("ingotCopper", new EMCEntry(128, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotTin", new EMCEntry(128, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotLead", new EMCEntry(512, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotNickel", new EMCEntry(512, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotAluminium", new EMCEntry(128, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotSilver", new EMCEntry(512, EMCUpgrade.SMELT));
		if (isModLoaded("IC2")) {
			// Rubber/Resin
			instance().addEMCtoItem(getItem("resin"), new EMCEntry(24), true);
			instance().addEMCtoItem(getItem("rubber"), new EMCEntry(24, .75f), true);
			// IC2 cables
			instance().addEMCtoItem(getItem("insulatedCopperCableItem"), new EMCEntry(88), true);
			instance().addEMCtoItem(getItem("copperCableItem"), new EMCEntry(64), true);

			instance().addEMCtoItem(getItem("tinCableItem"), new EMCEntry(64), true);

			instance().addEMCtoItem(getItem("glassFiberCableItem"), new EMCEntry(2081), true);

			instance().addEMCtoItem(getItem("goldCableItem"), new EMCEntry(512), true);
			instance().addEMCtoItem(getItem("insulatedGoldCableItem"), new EMCEntry(536), true);
			instance().addEMCtoItem(getItem("doubleInsulatedGoldCableItem"), new EMCEntry(560), true);

			instance().addEMCtoItem(getItem("ironCableItem"), new EMCEntry(64), true);
			instance().addEMCtoItem(getItem("insulatedIronCableItem"), new EMCEntry(88), true);
			instance().addEMCtoItem(getItem("doubleInsulatedIronCableItem"), new EMCEntry(112), true);
			instance().addEMCtoItem(getItem("trippleInsulatedIronCableItem"), new EMCEntry(136), true);// WTF? "tripple" is this why IC2 hasn't been updated to 1.5 officially?
			// Misc
			instance().addEMCtoItem(getItem("scaffold"), new EMCEntry(2), true);
			instance().addEMCtoItem(getItem("cell"), new EMCEntry(64), true);
			instance().addEMCtoItem(getItem("lavaCell"), new EMCEntry(128), true);
		}
	}

	private static void addDefEMC() {
		instance().addEMCtoItem(Block.cobblestone, new EMCEntry(1), false);
		instance().addEMCtoItem(Block.wood, new EMCEntry(32), false);
		instance().addEMCtoItem(Item.coal.itemID, 0, new EMCEntry(128));
		instance().addEMCtoItem(Item.coal.itemID, 1, new EMCEntry(32, .75f));
		instance().addEMCtoItem(Item.ingotIron, new EMCEntry(256), false);
		instance().addEMCtoItem(Item.ingotGold, new EMCEntry(2048), false);
		instance().addEMCtoItem(Item.diamond, new EMCEntry(8192), false);
		instance().addEMCtoItem(Block.cloth, new EMCEntry(48), false);
		instance().addEMCtoItem(Item.reed, new EMCEntry(24), false);
		instance().addEMCtoItem(Item.silk, new EMCEntry(12), false);
		instance().addEMCtoItem(Item.redstone, new EMCEntry(64), false);
		instance().addEMCtoItem(Block.obsidian, new EMCEntry(64), false);
		instance().addEMCtoItem(Item.netherStar, new EMCEntry(5000000), false);
		instance().addEMCtoItem(Item.dyePowder.itemID, 4, new EMCEntry(768));// Lapis
		instance().addEMCtoItem(Item.lightStoneDust, new EMCEntry(128), false);// Glowstone. lightStoneDust? WTF?
		instance().addEMCtoItem(Item.skull, new EMCEntry(512), false);
		instance().addEMCtoItem(Item.appleRed, new EMCEntry(64), false);
		instance().addEMCtoItem(Item.wheat, new EMCEntry(24), false);
		instance().addEMCtoItem(Block.dragonEgg, new EMCEntry(589824), false);

		// OreDict
		instance().addEMCtoOreDictName("plankWood", new EMCEntry(8));
		instance().addEMCtoOreDictName("stickWood", new EMCEntry(4));
		instance().addEMCtoOreDictName("slabWood", new EMCEntry(4));
		instance().addEMCtoOreDictName("treeSapling", new EMCEntry(32));
		instance().addEMCtoOreDictName("treeLeaves", new EMCEntry(1));
	}

	public static void println(String s) {
		// System.out.println("[ElectronMatterContent] " + s);
		log.info(s);
	}

	public static void warn(String s) {
		log.warning(s);
	}

	public static void becomeHardMode() {
		try {
			Class gtapi = Class.forName("gregtechmod.api.GregTech_API");
			Class gtmetaitem = Class.forName("gregtechmod.common.items.GT_MetaItem_Abstract");
			Method getstacks = gtmetaitem.getDeclaredMethod("getStackList", ((Class[]) null));
			Method centrec = gtapi.getDeclaredMethod("addCentrifugeRecipe", ItemStack.class, Integer.class, ItemStack.class, ItemStack.class, ItemStack.class, ItemStack.class, Integer.class);
			Method getitem = gtmetaitem.getDeclaredMethod("getUnunifiedStack", Integer.class, Integer.class);
			// centrec.invoke(null, GameRegistry.findItemStack("GT_Addon", "mRuby", 8), 3, GameRegistry.findItemStack("GT_Addon", "ingotAluminium", 2), (Items.getItem("airCell").stackSize = 2), GameRegistry.findItemStack("GT_Addon", "blah", 1));
			for (ItemStack is : (ItemStack[]) getstacks.invoke(null, ((Object[]) null))) {
				System.out.println(is);
			}
		} catch (Exception e) {
			warn(e.getMessage());
		}
		List rec = CraftingManager.getInstance().getRecipeList();
	}

	public static boolean isModLoaded(String modid) {
		return Loader.isModLoaded(modid);
	}

	@Override
	public void loadConfig() {
		EMC.println("NEI ToolTip Handler Module Initialised");
		GuiContainerManager.addTooltipHandler(new EMCToolTip());
	}

	@Override
	public String getName() {
		return "Electron Matter Content";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public static ItemStack getGregtechItem(int aIndex, int aAmount, int aMeta) {
		try {
			return (ItemStack) Class.forName("gregtechmod.GT_Mod").getMethod("getGregTechItem", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE })
					.invoke(null, new Object[] { Integer.valueOf(aIndex), Integer.valueOf(aAmount), Integer.valueOf(aMeta) });
		} catch (Exception e) {
		}
		return null;
	}
}
