package mods.eln.sixnode.powersocket;

import mods.eln.gui.ISlotSkin.SlotSkin;
import mods.eln.misc.BasicContainer;
import mods.eln.node.six.SixNodeItemSlot;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class PowerSocketContainer extends BasicContainer {

	//TODO TBD?>
	public static final int cableSlotId = 0;
	
	public PowerSocketContainer(EntityPlayer player, IInventory inventory) {
		super(player, inventory, new Slot[]{});
	}
}
