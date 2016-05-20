package mods.eln.node;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.lwjgl.opengl.GL11;

import mods.eln.Eln;
import mods.eln.misc.Utils;
import mods.eln.sim.ElectricalConnection;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalConnection;
import mods.eln.sim.ThermalLoad;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class NodeServer {

	public NodeServer()
	{
		MinecraftForge.EVENT_BUS.register(this);

	}

	public void init()
	{
		//	NodeBlockEntity.nodeAddedList.clear();
	}

	public void stop()
	{
		//	NodeBlockEntity.nodeAddedList.clear();
	}

	public int counter = 0;

	@SubscribeEvent
	public void tick(ServerTickEvent event) {
		if(event.phase != Phase.START) return;
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		if (server != null)
		{

			for (NodeBase node : NodeManager.instance.getNodeList())
			{
				if (node.getNeedPublish())
				{
					node.publishToAllPlayer();
				}
			}

			for (Object obj : server.getConfigurationManager().playerEntityList)
			{
				EntityPlayerMP player = (EntityPlayerMP) obj;

				NodeBase openContainerNode = null;
				INodeContainer container = null;
				if (player.openContainer != null && player.openContainer instanceof INodeContainer)
				{
					container = ((INodeContainer) player.openContainer);
					openContainerNode = container.getNode();
				}

				for (NodeBase node : NodeManager.instance.getNodeList())
				{

					if (node == openContainerNode)
					{
						if ((counter % (1 + container.getRefreshRateDivider())) == 0)
							node.publishToPlayer(player);
					}
				}
			}

			counter++;
		}

	}


}
