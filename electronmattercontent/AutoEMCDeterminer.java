package electronmattercontent;

import static electronmattercontent.EMCAPI.instance;

import ic2.api.recipe.Recipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class AutoEMCDeterminer implements Runnable {
	private static CraftingManager craft = CraftingManager.getInstance();// No need to be public,
	private static List<IRecipe> rec;// Easily accessible otherways
	public static Map fMap = FurnaceRecipes.smelting().getSmeltingList();
	public static Map fMapMeta = FurnaceRecipes.smelting().getMetaSmeltingList();
	public static AutoEMCDeterminer instance = new AutoEMCDeterminer();

	public void initEMCBasedOnCrafting() {
		rec = craft.getRecipeList();
		for (int iter = 1; iter <= 5; iter++) {
			EMC.println("--------------------------------Round " + iter + "/5--------------------------------");
			for (int i = 1; i <= 5; i++) {
				EMC.println("Scanning every crafting recipe, we are in the " + i + "/5 loop, just to be sure. (I doubt some recipe has +10 different ingredients which need to be crafted)");
				for (int j = 0; j < rec.size(); j++) {
					IRecipe recipe = rec.get(j);
					if (recipe.getRecipeOutput() != null && recipe.getRecipeOutput().stackSize != 0) {
					} else {// Skip.
						continue;
					}
					if (recipe instanceof ShapedRecipes) {// Handles ShapedRecipes LIKE A BAWS
						if (instance().hasEMCEnt((recipe.getRecipeOutput()))) {// Already got one.
							continue;
						}
						// EMC.println("Shaped.");
						int emc = 0;
						boolean add = true;// If all components have EMC
						for (ItemStack s : ((ShapedRecipes) recipe).recipeItems) {
							if (s != null) {
								if (instance().hasEMCEnt(s)) {
									emc += instance().getEMCEntofItem(s).getEMC();
								} else {
									add = false;
									continue;// Found no emc here, move along people
								}
							}
						}
						if (add) {
							instance().addEMCtoItem(recipe.getRecipeOutput(), new EMCEntry(emc / recipe.getRecipeOutput().stackSize), true);// Lazy. Ref will not change as of now
						}
					} else if (recipe instanceof ShapelessRecipes) {
						if (instance().hasEMCEnt(recipe.getRecipeOutput())) {
							continue;
						}
						// EMC.println("Shapeless.");
						int emc = 0;
						boolean add = true;// If all components have EMC
						for (ItemStack s : ((List<ItemStack>) ((ShapelessRecipes) recipe).recipeItems)) {
							if (s != null) {
								if (instance().hasEMCEnt(s)) {
									emc += instance().getEMCEntofItem(s).getEMC();
								} else {
									add = false;
									continue;// Found no emc here, move along people
								}
							}
						}
						if (add) {
							instance().addEMCtoItem(recipe.getRecipeOutput(), new EMCEntry(emc / recipe.getRecipeOutput().stackSize), true);// Lazy. Ref will not change as of now
						}
					} else if (recipe instanceof ShapedOreRecipe) {
						if (instance().hasEMCEnt(recipe.getRecipeOutput())) {
							continue;
						}
						// EMC.println("Shaped Ore Recipe.");
						int emc = 0;
						boolean add = true;// If all components have EMC
						for (Object s : ((ShapedOreRecipe) recipe).getInput()) {
							if (s != null) {
								if (s instanceof ItemStack) {
									ItemStack stack = (ItemStack) s;
									if (instance().hasEMCEnt(stack)) {
										emc += instance().getEMCEntofItem(stack).getEMC();
									} else {
										add = false;
										continue;
									}
								} else if (s instanceof ArrayList) {// Grabs the first one available. In this case, one should use addEMCtoOreDictName
									if (((ArrayList) s).size() == 0) {
										EMC.warn("Found a ShapedOreRecipe, but ArrayList's size is zero!");
										continue;
									}
									try {
										ItemStack stack = (ItemStack) ((ArrayList) s).get(0);// Always seems to be ItemStack. REPORT CRASHES
										if (instance().hasEMCEnt(stack)) {
											emc += instance().getEMCEntofItem(stack).getEMC();
										} else {
											add = false;
											continue;
										}
									} catch (Exception e) {
										EMC.warn(e.getMessage());
										EMC.warn("Invalid ShapedOreRecipe!!");
									}
								} else if (s instanceof String) {
									if (instance().hasOreDictEMCEnt((String) s)) {
										emc += instance().getEMCEntofOreDictName((String) s).getEMC();
									} else {
										add = false;
										continue;
									}
								}
							}
						}
						if (add) {
							instance().addEMCtoItem(recipe.getRecipeOutput(), new EMCEntry(emc / recipe.getRecipeOutput().stackSize), true);// Lazy. Ref will not change as of now
						}
					} else if (recipe instanceof ShapelessOreRecipe) {
						if (instance().hasEMCEnt(recipe.getRecipeOutput())) {
							continue;
						}
						// EMC.println("Shapeless Ore Recipe.");
						int emc = 0;
						boolean add = true;// If all components have EMC
						for (Object s : ((ShapelessOreRecipe) recipe).getInput().toArray(new Object[recipe.getRecipeSize()])) {
							if (s != null) {
								if (s instanceof ItemStack) {
									ItemStack stack = (ItemStack) s;
									if (instance().hasEMCEnt(stack)) {
										emc += instance().getEMCEntofItem(stack).getEMC();
									} else {
										add = false;
										continue;
									}
								} else if (s instanceof ArrayList) {// Grabs the first one available. In this case, one should use addEMCtoOreDictName
									if (((ArrayList) s).size() == 0) {
										EMC.warn("Found a ShapedOreRecipe, but ArrayList's size is zero!");
										continue;
									}
									try {
										ItemStack stack = (ItemStack) ((ArrayList) s).get(0);// Always seems to be ItemStack. REPORT CRASHES
										if (instance().hasEMCEnt(stack)) {
											emc += instance().getEMCEntofItem(stack).getEMC();
										} else {
											add = false;
											continue;
										}
									} catch (Exception e) {
										EMC.warn(e.getMessage());
										EMC.warn("Invalid ShapedOreRecipe!!");
									}
								} else if (s instanceof String) {
									if (instance().hasOreDictEMCEnt((String) s)) {
										emc += instance().getEMCEntofOreDictName((String) s).getEMC();
									} else {
										add = false;
										continue;
									}
								}
							}
						}
						if (add) {
							instance().addEMCtoItem(recipe.getRecipeOutput(), new EMCEntry(emc / recipe.getRecipeOutput().stackSize), true);// Lazy. Ref will not change as of now
						}
					}

					if (EMC.isModLoaded("IC2")) {
						try {//Advanced IC2 Recipes, both feature ItemStacks and OreDictionary Integration
							if (instance().hasEMCEnt(recipe.getRecipeOutput())) {
								continue;
							}
							Class ic2rec = Class.forName("ic2.core.AdvRecipe");
							Class ic2shprec = Class.forName("ic2.core.AdvShapelessRecipe");
							Object[] in;
							if (recipe.getClass() == ic2rec) {
								Field input = ic2rec.getDeclaredField("input");
								in = (Object[]) input.get(recipe);
								int emc = 0;
								boolean add = true;
								for (Object o : in) {
									if (o != null) {
										if (o instanceof ItemStack) {
											if (instance().hasEMCEnt((ItemStack) o)) {
												emc += instance().getEMCEntofItem((ItemStack) o).getEMC();
											} else {
												add = false;
												continue;
											}
										} else if (o instanceof String) {
											if (instance().hasOreDictEMCEnt((String) o)) {
												emc += instance().getEMCEntofOreDictName((String) o).getEMC();
											} else {
												add = false;
												continue;
											}
										}
									}
								}
								if (add) {
									instance().addEMCtoItem(recipe.getRecipeOutput(), new EMCEntry(emc / recipe.getRecipeOutput().stackSize), true);// Lazy. Ref will not change as of now
								}
							} else if (recipe.getClass() == ic2shprec) {
								Field input = ic2shprec.getDeclaredField("input");
								in = (Object[]) input.get(recipe);
								int emc = 0;
								boolean add = true;
								for (Object o : in) {
									if (o != null) {
										if (o instanceof ItemStack) {
											if (instance().hasEMCEnt((ItemStack) o)) {
												emc += instance().getEMCEntofItem((ItemStack) o).getEMC();
											} else {
												add = false;
												continue;
											}
										} else if (o instanceof String) {
											if (instance().hasOreDictEMCEnt((String) o)) {
												emc += instance().getEMCEntofOreDictName((String) o).getEMC();
											} else {
												add = false;
												continue;
											}
										}
									}
								}
								if (add) {
									instance().addEMCtoItem(recipe.getRecipeOutput(), new EMCEntry(emc / recipe.getRecipeOutput().stackSize), true);// Lazy. Ref will not change as of now
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {//Compressor (macerator and extractor break EMCs anyways, and electric furnace...well...it's pointless.)
							Map<ItemStack, ItemStack> com = Recipes.compressor.getRecipes();
							Iterator comiter = com.keySet().iterator();
							while (comiter.hasNext()) {
								ItemStack before = (ItemStack) comiter.next();
								ItemStack after = com.get(before);
								if (EMCAPI.instance().hasEMCEnt(before)) {
									int emc = EMCAPI.instance().getEMCEntofItem(before).getEMC() * before.stackSize;
									EMCAPI.instance().addEMCtoItem(after, new EMCEntry(emc, EMCAPI.instance().getEMCEntofItem(before).getRef()), true);
								} else {
									continue;//Nope.
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
			EMC.println("Phew. On to the Furnace Recipes.");
			for (int i = 1; i <= 3; i++) {
				EMC.println("Scanning every Furnace recipe, we are in the " + i + "/3 loop, just to be sure. (I doubt some recipe has +10 different ingredients which need to be crafted)");
				Iterator furn = fMap.keySet().iterator();
				Iterator furnmeta = fMapMeta.keySet().iterator();
				while (furn.hasNext()) {
					Integer id = (Integer) furn.next();
					ItemStack nosmelt = new ItemStack(id.intValue(), 1, -1);
					ItemStack res = (ItemStack) fMap.get(id);
					if (instance().hasEMCEnt(nosmelt) && !instance().hasEMCEnt(res)) {
						instance().addEMCtoItem(res, new EMCEntry(instance().getEMCEntofItem(nosmelt).getEMC(), instance().getEMCEntofItem(nosmelt).getRef() + .25f, EMCUpgrade.SMELT), false);
					}
				}
				while (furnmeta.hasNext()) {
					List<Integer> list = (List<Integer>) furnmeta.next();
					ItemStack nosmelt = new ItemStack(list.get(0).intValue(), 1, list.get(1).intValue());
					ItemStack res = (ItemStack) fMapMeta.get(Arrays.asList(list.get(0), list.get(1)));
					if (instance().hasEMCEnt(nosmelt) && !instance().hasEMCEnt(res)) {
						instance().addEMCtoItem(res, new EMCEntry(instance().getEMCEntofItem(nosmelt).getEMC(), instance().getEMCEntofItem(nosmelt).getRef() + .25f, EMCUpgrade.SMELT), false);
					}
				}
			}
		}
	}

	@Override
	public void run() {
		EMC.println("Initializing automatic EMC determination tool.");
		EMC.println("INGENIOUSLY written by getmemoney >:D");
		instance.initEMCBasedOnCrafting();
	}
}
