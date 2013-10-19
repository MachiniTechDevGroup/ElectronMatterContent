package electronmattercontent;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.UniversalElectricity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class GuiHugeChest extends GuiContainer {

	private TileEntityHugeChest entity;
	public GuiHugeChest(TileEntityHugeChest entity, InventoryPlayer inv) {
		super(new ContainerHugeChest(entity, inv));
		this.ySize = 248;
		this.xSize = 352;
		this.entity = entity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/mods/EMC/textures/gui/GuiHugeChest.png");
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		int height = this.entity.getProgressScaled(69);
		this.drawTexturedModalRect(x + 332, y + 84 - height, 353, 60 - height, 16, height);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		fontRenderer.drawString(entity.getInvName(), 44, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 95, this.ySize - 96 + 5, 4210752);
	}
}
