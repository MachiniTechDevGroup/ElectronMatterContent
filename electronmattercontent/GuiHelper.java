package electronmattercontent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHelper implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileEntityMatterCondenser) {
			return new ContainerMatterCondenser((TileEntityMatterCondenser) tile, player.inventory);
		} else if (tile instanceof TileEntityHugeChest) {
			return new ContainerHugeChest((TileEntityHugeChest) tile, player.inventory);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileEntityMatterCondenser) {
			return new GuiMatterCondenser((TileEntityMatterCondenser) tile, player.inventory);
		} else if (tile instanceof TileEntityHugeChest) {
			return new GuiHugeChest((TileEntityHugeChest) tile, player.inventory);
		}

		return null;
	}
}
