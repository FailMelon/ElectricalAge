package mods.eln.server;

import li.cil.oc.api.network.Node;
import mods.eln.Eln;
import mods.eln.misc.Coordonate;
import mods.eln.misc.Utils;
import mods.eln.node.NodeManager;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import org.lwjgl.Sys;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;

public class ServerEventListener {

    LinkedList<EntityLightningBolt> lightningListNext = new LinkedList<EntityLightningBolt>();
    LinkedList<EntityLightningBolt> lightningList = new LinkedList<EntityLightningBolt>();

    public ServerEventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        if (event.phase != Phase.END) return;

        lightningList = lightningListNext;
        lightningListNext = new LinkedList<EntityLightningBolt>();
    }

    @SubscribeEvent
    public void onNewEntity(EntityConstructing event) {
        if (event.getEntity() instanceof EntityLightningBolt) {
            lightningListNext.add((EntityLightningBolt) event.getEntity());
        }
    }

    public void clear() {
        lightningList.clear();
    }

    public double getLightningClosestTo(Coordonate c) {
        double best = 10000000;
        for (EntityLightningBolt l : lightningList) {
            if (c.world() != l.worldObj) continue;
            double d = l.getDistance(c.x, c.y, c.z);
            if (d < best) best = d;
        }
        return best;
    }


    public String getEaWorldSaveName(World w) {
        return Utils.getMapFolder() + "data/electricalAgeWorld" + w.provider.getDimension() + ".dat";
    }
    public HashSet<Integer> loadedWorlds = new HashSet<Integer>();
    @SubscribeEvent
    public void onWorldLoad(Load e) {
        if (e.getWorld().isRemote) return;
        loadedWorlds.add(e.getWorld().provider.getDimension());
        try {
            FileInputStream fileStream = new FileInputStream(getEaWorldSaveName(e.getWorld()));
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(fileStream);
            readFromEaWorldNBT(nbt);
            fileStream.close();
        } catch (Exception ex) {
            try {
                FileInputStream fileStream = new FileInputStream(getEaWorldSaveName(e.getWorld()) + "back");
                NBTTagCompound nbt = CompressedStreamTools.readCompressed(fileStream);
                readFromEaWorldNBT(nbt);
                fileStream.close();
            } catch (Exception ex2) {
                ElnWorldStorage storage = ElnWorldStorage.forWorld(e.getWorld());
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(Unload e) {
        if (e.getWorld().isRemote) return;
        loadedWorlds.remove(e.getWorld().provider.getDimension());
        try {
            NodeManager.instance.unload(e.getWorld().provider.getDimension());
            Eln.ghostManager.unload(e.getWorld().provider.getDimension());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @SubscribeEvent
    public void onWorldSave(Save e) {
        if (e.getWorld().isRemote) return;
        if(!loadedWorlds.contains(e.getWorld().provider.getDimension())) {
            //System.out.println("I hate you minecraft");
            return;
        }
        try {
            NBTTagCompound nbt = new NBTTagCompound();
            writeToEaWorldNBT(nbt, e.getWorld().provider.getDimension());

//            File oldBackup = new File(getEaWorldSaveName(e.world) + "back");
//            if (oldBackup.exists()) {
//                oldBackup.delete();
//            }
//
//            File oldSave = new File(getEaWorldSaveName(e.world));
//            if (oldSave.exists()) {
//                oldSave.renameTo(oldBackup);
//            }

            FileOutputStream fileStream = new FileOutputStream(getEaWorldSaveName(e.getWorld()));
            CompressedStreamTools.writeCompressed(nbt, fileStream);
            fileStream.flush();
            fileStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //ElnWorldStorage storage = ElnWorldStorage.forWorld(e.world);
        int idx = 0;
        idx++;
    }


    public static void readFromEaWorldNBT(NBTTagCompound nbt) {
        try {
            NodeManager.instance.loadFromNbt(nbt.getCompoundTag("nodes"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Eln.ghostManager.loadFromNBT(nbt.getCompoundTag("ghost"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToEaWorldNBT(NBTTagCompound nbt, int dim) {
        try {
            NodeManager.instance.saveToNbt(Utils.newNbtTagCompund(nbt, "nodes"), dim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Eln.ghostManager.saveToNBT(Utils.newNbtTagCompund(nbt, "ghost"), dim);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
