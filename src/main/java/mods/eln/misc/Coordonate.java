package mods.eln.misc;

import mods.eln.node.NodeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Coordonate implements INBTTReady {

	public int x, y, z, dimention;
	
	public Coordonate() {
		x = 0;
		y= 0;
		z = 0;
		dimention = 0;
	}

	public Coordonate(Coordonate coord) {
		x = coord.x;
		y= coord.y;
		z = coord.z;
		dimention = coord.dimention;
	}
	
	public Coordonate(NBTTagCompound nbt, String str) {
		readFromNBT(nbt, str);
	}
	
	@Override
	public int hashCode() {
		return (x + y) * 0x10101010 + z;
	}
	
	
	public int worldDimension() {
		return dimention;
	}
	
	private World w = null;

	public World world() {
	//	Side sideCS = FMLCommonHandler.instance().getEffectiveSide();
	//	if (sideCS == Side.CLIENT) return null;

		//Minecraft m = Minecraft.getMinecraft();
		//if(FMLCommonHandler.instance().getSidedDelegate().)
		//WorldManager
		//Minecraft.getMinecraft().
		//World
		//Minecraft m = Minecraft.getMinecraft();
		
		
		/*if(w == null) *///w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(worldDimension());

		if(w == null) {
			return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(worldDimension());
		}
		return w;
	}
	
	public Coordonate(NodeBlockEntity entity) {
		x = entity.getPos().getX();
		y = entity.getPos().getY();
		z = entity.getPos().getZ();
		dimention = entity.getWorld().provider.getDimension();
	}

	public Coordonate(int x, int y, int z, int dimension) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimention = dimension;
	}
	
	public Coordonate(BlockPos pos, int dimension) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.dimention = dimension;
	}

	public Coordonate(int x, int y, int z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimention = world.provider.getDimension();
		if(world.isRemote)
			this.w = world;
	}
	
	public Coordonate(BlockPos pos, World world) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.dimention = world.provider.getDimension();
		if(world.isRemote)
			this.w = world;
	}

	public Coordonate(TileEntity entity) {
		this.x = entity.getPos().getX();
		this.y = entity.getPos().getY();
		this.z = entity.getPos().getZ();
		this.dimention = entity.getWorld().provider.getDimension();
		if(entity.getWorld().isRemote)
			this.w = entity.getWorld();
	}
	
	public Coordonate newWithOffset(int x, int y, int z) {
		return new Coordonate(this.x + x, this.y + y, this.z + z, dimention);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Coordonate)) return false;
		Coordonate id = (Coordonate) obj;
		return id.x == x && id.y == y && id.z == z && id.dimention == dimention;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String str) {
		x = nbt.getInteger(str + "x");
		y = nbt.getInteger(str + "y");
		z = nbt.getInteger(str + "z");
		dimention = nbt.getInteger(str + "d");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String str) {
		nbt.setInteger(str + "x", x);
		nbt.setInteger(str + "y", y);
		nbt.setInteger(str + "z", z);
		nbt.setInteger(str + "d", dimention);
	}

	@Override
	public String toString() {
		return "X : " + x + " Y : " + y + " Z : " + z + " D : " + dimention;
	}

	public void move(Direction dir) {
		switch(dir) {
			case XN:
				x--;
				break;
			case XP:
				x++;
				break;
			case YN:
				y--;
				break;
			case YP:
				y++;
				break;
			case ZN:
				z--;
				break;
			case ZP:
				z++;
				break;
			default:
				break;
		}
	}
	
	public Block getBlock() {
		return world().getBlockState(getBlockPos()).getBlock();
	}

	public BlockPos getBlockPos() {
		return new BlockPos(x, y, z);
	}
	
	public IBlockState getBlockState() {
		return world().getBlockState(getBlockPos());
	}
	
	public static AxisAlignedBB getAxisAlignedBB(Coordonate a, Coordonate b) 
	{
		BlockPos start = new BlockPos(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
		BlockPos end = new BlockPos(Math.max(a.x, b.x) + 1.0,  Math.max(a.y, b.y) + 1.0, Math.max(a.z, b.z) + 1.0);
		return new AxisAlignedBB(start, end);
	}

	public  AxisAlignedBB getAxisAlignedBB(int ray) 
	{
		BlockPos start = new BlockPos(x - ray, y - ray, z - ray);
		BlockPos end = new BlockPos(x + ray + 1, y + ray + 1, z + ray + 1);
		return new AxisAlignedBB(start, end);
	}

	public double distanceTo(Entity e) {
		return Math.abs(e.posX - (x + 0.5)) +  Math.abs(e.posY - (y + 0.5)) + Math.abs(e.posZ - (z + 0.5));
	}
	
	/*public void setBlock(int id, int meta) {
		world().setBlock(x, y, z, id, meta, 2);
	}*/

	public int getMeta() 
	{

		return world().getBlockState(getBlockPos()).getBlock().getMetaFromState(world().getBlockState(getBlockPos()));
	}

	public boolean getBlockExist() {
		World w = DimensionManager.getWorld(dimention);
		if(w == null) return false;
		return w.isBlockLoaded(new BlockPos(x, y, z));
	}
	
	public boolean getWorldExist() {
		return DimensionManager.getWorld(dimention) != null;
	}
	
	public void copyTo(double[] v) {
		v[0] = x + 0.5;
		v[1] = y + 0.5;
		v[2] = z + 0.5;
		
	}

	public void setPosition(double[] vp) {
		this.x = (int) vp[0];
		this.y = (int) vp[1];
		this.z = (int) vp[2];
	}
	
	public void setPosition(Vec3d vp) {
		this.x = (int) vp.xCoord;
		this.y = (int) vp.yCoord;
		this.z = (int) vp.zCoord;
	}

	public TileEntity getTileEntity() {
		return world().getTileEntity(new BlockPos(x, y, z));
	}

	public void invalidate() {
		x = -1;
		y = -1;
		z = -1;
		dimention = -5123;
	}
	
	public boolean isValid() {
		return dimention != -5123;
	}

	public double trueDistanceTo(Coordonate c) {
		int dx = x - c.x;
		int dy = y - c.y;
		int dz = z - c.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz) ;
	}

	public void setDimention(int dimention) {
		this.dimention = dimention;
		w = null;
	}

	public void copyFrom(Coordonate c) {
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
		this.dimention = c.dimention;
	}

	public void applyTransformation(Direction front, Coordonate coordonate) {
		front.rotateFromXN(this);
		x += coordonate.x;
		y += coordonate.y;
		z += coordonate.z;
	}

	public void setWorld(World worldObj) {
		if(worldObj.isRemote)
			w = worldObj;
		dimention = worldObj.provider.getDimension();
	}

	public void setMetadata(int meta) 
	{	
		world().setBlockState(getBlockPos(), world().getBlockState(getBlockPos()), meta);
	}

	public void setBlock(Block b) 
	{
		world().setBlockState(getBlockPos(), b.getDefaultState());
	}
}
