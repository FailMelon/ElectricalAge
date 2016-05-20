package mods.eln.eventhandlers;

import mods.eln.Eln;
import mods.eln.achievepackets.AchievePacket;
import mods.eln.wiki.Root;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElnForgeEventsHandler {

    private final static AchievePacket p = new AchievePacket("openWiki");

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public void openGuide(GuiOpenEvent e) {
        if (e.getGui() instanceof Root) {
            Eln.achNetwork.sendToServer(p);
        }
    }
}
