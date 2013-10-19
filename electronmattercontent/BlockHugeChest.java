package electronmattercontent;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHugeChest extends BlockChest {

	protected BlockHugeChest(int par1) {
		super(par1, 0);
	}
	@Override
	public void unifyAdjacentChests(World world, int par2, int par3, int par4) {
		
	}
	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityHugeChest();
	}
	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		if (par5EntityPlayer.isSneaking()) {
			return false;
		}

		if (!world.isRemote) {
			TileEntityHugeChest te = (TileEntityHugeChest) world.getBlockTileEntity(par2, par3, par4);

			if (te != null) {
				par5EntityPlayer.openGui(EMC.instance, 1, world, par2, par3, par4);
			}
		}

		return true;
	}
}
