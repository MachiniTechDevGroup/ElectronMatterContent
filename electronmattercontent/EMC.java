package electronmattercontent;

import static electronmattercontent.EMCAPI.instance;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapelessOreRecipe;
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
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class EMC implements IConfigureNEI {
	@Instance("modEMC")
	public static EMC instance;
	@SidedProxy(clientSide = "electronmattercontent.ClientProxy", serverSide = "electronmattercontent.CommonProxy")
	public static CommonProxy proxy;
	public static BlockMatterCondenser mblk = new BlockMatterCondenser(2900);
	public static SwordHeadHunter swrd;
	public static Configuration config;
	public static boolean IC2 = false;
	public static EnumToolMaterial mat;
	public static EMCUpgrade up = new EMCUpgrade(2902 - 256);
	private static Logger log;

	@PreInit
	public void pre(FMLPreInitializationEvent event) {
		log = Logger.getLogger("ElectronMatterContent");
		log.setParent(FMLLog.getLogger());
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		IC2 = config.get("Energy", "IC2", false).getBoolean(false);
		config.save();
	}

	@Init
	public void go(FMLInitializationEvent event) {
		addDefEMC();
		mat = EnumHelper.addToolMaterial("HeadHunter", 2, 256, 6f, 4, 20);
		mblk.setUnlocalizedName("Matter");
		swrd = new SwordHeadHunter(2901 - 256);
		swrd.setUnlocalizedName("Sword");
		GameRegistry.registerItem(swrd, "head");
		LanguageRegistry.addName(swrd, "Head Hunter's Sword");
		GameRegistry.registerBlock(mblk, "matterblock");
		GameRegistry.registerTileEntity(TileEntityMatterCondenser.class,
				"modEMC");
		LanguageRegistry.addName(mblk, "Electron Rearranger");
		NetworkRegistry.instance()
				.registerGuiHandler(instance, new GuiHelper());
		GameRegistry.addShapedRecipe(new ItemStack(swrd), "sdc", "wdz", "ptp", Character.valueOf('s'), new ItemStack(397, 1, 0), Character.valueOf('c'), new ItemStack(397, 1, 4), Character.valueOf('w'), new ItemStack(397, 1, 1), Character.valueOf('z'), new ItemStack(397, 1, 2), Character.valueOf('p'), new ItemStack(397, 1, 3), Character.valueOf('d'), Item.diamond, Character.valueOf('t'), Item.stick);
	}
	@PostInit
	public void somethingOrOther(FMLPostInitializationEvent event) {
		addModEMC();
		new Thread(new AutoEMCDeterminer()).run();
		//EMCAPI.printAllEMCValues();
		//println("Fence:" + EMCAPI.hasEMC(new ItemStack(Block.fence)));
		MinecraftForge.EVENT_BUS.register(new HeadHunterDeathListener());
	}

	private void addModEMC() {
		instance().addEMCtoOreDictName("ingotCopper", new EMCEntry(85, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotTin", new EMCEntry(256, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotLead", new EMCEntry(512, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotNickel", new EMCEntry(512, EMCUpgrade.SMELT));
		instance().addEMCtoOreDictName("ingotAluminium", new EMCEntry(128, EMCUpgrade.SMELT));
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
		instance().addEMCtoItem(Item.dyePowder.itemID, 4, new EMCEntry(768));//Lapis
		instance().addEMCtoItem(Item.lightStoneDust, new EMCEntry(128), false);//Glowstone. lightStoneDust? WTF?
		instance().addEMCtoItem(Item.skull, new EMCEntry(512), false);
	}

	public static void println(String s) {
		//System.out.println("[ElectronMatterContent] " + s);
		log.info(s);
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
}
