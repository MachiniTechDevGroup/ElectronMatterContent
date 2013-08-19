package electronmattercontent;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.forge.IContainerTooltipHandler;

public class EMCToolTip implements IContainerTooltipHandler {
	@Override
	public List<String> handleTooltipFirst(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
		return currenttip;
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
		if (EMCAPI.instance().hasEMCEnt(itemstack)) {
			currenttip.add(EMCAPI.instance().getEMCEntofItem(itemstack).getEMC() + " EMC");
		}

		return currenttip;
	}
}
