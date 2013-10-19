package electronmattercontent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals("CondenserSync")) {
			this.syncCond(packet);
		}
	}

	private void syncCond(Packet250CustomPayload packet) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
		int x = 0, y = 0, z = 0, emc = 0;
		try {
			x = in.readInt();
			y = in.readInt();
			z = in.readInt();
			emc = in.readInt();
		} catch (IOException e) {
			EMC.warn(e.getMessage());
		}
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			TileEntity te = FMLClientHandler.instance().getClient().theWorld.getBlockTileEntity(x, y, z);
			if (!(te instanceof TileEntityMatterCondenser)) {
				EMC.warn("Recieved Packet250CustomPayload, but tile entity is not instanceof TileEntityMatterCondenser!");
				return;
			}
			((TileEntityMatterCondenser)te).progress = emc;
		}
	}

}
