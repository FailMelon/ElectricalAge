package mods.eln.misc;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.ArrayList;

public class TileEntityDestructor {

	ArrayList<TileEntity> destroyList = new ArrayList<TileEntity>();
	
	public TileEntityDestructor() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void clear() {
		destroyList.clear();
	}

	public void add(TileEntity tile) {
		destroyList.add(tile);
	}
	
	@SubscribeEvent
	public void tick(ServerTickEvent event) {
		if(event.phase != Phase.START) return;
		for(TileEntity t : destroyList) {
			if(t.getWorld() != null && t.getWorld().getTileEntity(t.getPos()) == t) {
				t.getWorld().setBlockToAir(t.getPos());
				Utils.println("destroy light at " + t.getPos());
			}
		}
		destroyList.clear();
	}
}
