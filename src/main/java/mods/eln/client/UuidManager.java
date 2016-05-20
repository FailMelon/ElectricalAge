package mods.eln.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class UuidManager {
	LinkedList<Pair> eList = new LinkedList<Pair>();
	
	public static class Pair {
		Pair(ArrayList<Integer> uuid,IUuidEntity e) {
			this.uuid = uuid;
			this.e = e;
		}
		ArrayList<Integer> uuid;
		IUuidEntity e;
	}
	
	public UuidManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void add(ArrayList<Integer> uuid,IUuidEntity e){
		eList.add(new Pair(uuid, e));
	}
	
	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END) return;

		Iterator<Pair> i = eList.iterator();

		while(i.hasNext()) {
			Pair p = i.next();
			if(!p.e.isAlive()){
				i.remove();
			}
		}
		//Utils.println(eList.size());
	}
	
	public void kill(int uuid) {
		Iterator<Pair> i = eList.iterator();
		while(i.hasNext()) {
			Pair p = i.next();
			if(p.uuid == null) continue;
			for(Integer pUuid : p.uuid){
				if(pUuid == uuid) {
					p.e.kill();
					i.remove();
				}
			}
		}		
	}
}
