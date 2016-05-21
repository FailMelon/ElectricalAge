package mods.eln.node.simple;

import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.IOException;

import mods.eln.Eln;
import mods.eln.misc.Coordonate;
import mods.eln.misc.DescriptorManager;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.INodeEntity;
import mods.eln.node.NodeEntityClientSender;
import mods.eln.node.NodeManager;
import mods.eln.server.DelayedBlockRemove;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SimpleNodeEntity extends TileEntity implements INodeEntity{

	private SimpleNode node;

	public SimpleNode getNode() {
		if (worldObj.isRemote){
			Utils.fatal();
			return null;
		}
		if(this.worldObj == null) return null;
		if (node == null){ 
			node = (SimpleNode) NodeManager.instance.getNodeFromCoordonate(new Coordonate(getPos(), this.worldObj));
			if(node == null){
				DelayedBlockRemove.add(new Coordonate(getPos(), this.worldObj));
				return null;
			}
		}
		return node;
	}

	
	
	//***************** Wrapping **************************
	/*
	public void onBlockPlacedBy(Direction front, EntityLivingBase entityLiving, int metadata) {
	
	}
*/

	public void onBlockAdded(){
		/*if (!worldObj.isRemote){
			if (getNode() == null) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}
		}*/
	}

	public void onBreakBlock(){
		if (!worldObj.isRemote){
			if (getNode() == null) return;
			getNode().onBreakBlock();
		}
	}

	public void onChunkUnload(){
		super.onChunkUnload();
		if (worldObj.isRemote){
			destructor();
		}
	}

	// client only
	public void destructor(){

	}

	@Override
	public void invalidate() {
		if (worldObj.isRemote){
			destructor();
		}
		super.invalidate();
	}

	public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, BlockPos pos){
		if (!worldObj.isRemote){
			if (getNode() == null) return false;
			getNode().onBlockActivated(entityPlayer, side, pos);
			return true;
		}
		return true;
	}

	public void onNeighborBlockChange(){
		if (!worldObj.isRemote){
			if (getNode() == null) return;
			getNode().onNeighborBlockChange();
		}
	}


	//***************** Descriptor **************************
	public Object getDescriptor(){
		SimpleNodeBlock b = (SimpleNodeBlock) getBlockType();
		return DescriptorManager.get(b.descriptorKey);
	}
	
	
	
	
	
	
	//***************** Network **************************
	
	public Direction front;
	@Override
	public void serverPublishUnserialize(DataInputStream stream) {
		try {
			if(front != (front = Direction.fromInt(stream.readByte()))){
				markDirty();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serverPacketUnserialize(DataInputStream stream) {

	}
	
    @Override
    public Packet getDescriptionPacket()
    {	
    	SimpleNode node = getNode(); 
    	if(node == null){
    		Utils.println("ASSERT NULL NODE public Packet getDescriptionPacket() nodeblock entity");
    		return null;
    	}
    	PacketBuffer pktbuf = new PacketBuffer(Unpooled.buffer());
    	pktbuf.writeBytes(node.getPublishPacket().toByteArray());
    	return new SPacketCustomPayload(Eln.channelName, pktbuf);
    }

    
    public NodeEntityClientSender sender = new NodeEntityClientSender(this, getNodeUuid());
    
    
    
    
    //*********************** GUI ***************************
	@Override
	public Container newContainer(Direction side,EntityPlayer player)
	{
		return null;
	}
	@Override
    @SideOnly(Side.CLIENT)
	public GuiScreen newGuiDraw(Direction side,EntityPlayer player)
	{
		return null;
	}
	
	
}
