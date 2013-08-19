package electronmattercontent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiMatterCondenser extends GuiContainer {
	private TileEntityMatterCondenser entity;

	public GuiMatterCondenser(TileEntityMatterCondenser entity, InventoryPlayer inventory) {
		super(new ContainerMatterCondenser(entity, inventory));
		this.ySize = 254;
		this.xSize = 198;
		this.entity = entity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/mods/EMC/textures/gui/Matter.png");
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		this.drawTexturedModalRect(x + 135, y + 17, 198, 0, this.entity.getProgressScaled(56), 15);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		fontRenderer.drawString(entity.getInvName(), 44, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 5, 4210752);
		fontRenderer.drawString("EMC: " + this.entity.progress, 28, 20, 4210752);

		if (EMC.IC2) {
			fontRenderer.drawString("EU: " + this.entity.energy, 80, 20, 4210752);
		}
	}
}
