package mods.eln.node;

import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class NodeBlock extends Block {//BlockContainer
	public int blockItemNbr;
	Class tileEntityClass;
	public NodeBlock ( Material material,Class tileEntityClass,int blockItemNbr) {
		super(material);
		setBlockName("NodeBlock");
		this.tileEntityClass = tileEntityClass;
		useNeighborBrightness = true;
		this.blockItemNbr = blockItemNbr;
		setHardness(1.0f);
		setResistance(1.0f);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) 
	{
		return 1.0f;
	}



	@Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
		NodeBlockEntity entity = (NodeBlockEntity) blockAccess.getTileEntity(pos);
    	return entity.isProvidingWeakPower(Direction.from(side));
    }
	
	@Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
		NodeBlockEntity entity = (NodeBlockEntity) world.getTileEntity(pos);
    	return entity.canConnectRedstone(Direction.XN);
    }
	 
    @Override
	public boolean canProvidePower(IBlockState state) 
    {		
		return super.canProvidePower(state);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
	  return true;
	}
	
	// 1.7.10: Old render code
	/*
	@Override
	public boolean renderAsNormalBlock() 
	{
	  return false;
	}*/
	
    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }
	
	// Old 1.7.10 code just incase this is wrong.
	/*
	@Override
	public int getRenderType() {
	  return -1;
	}*/

	@Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		final TileEntity entity = world.getTileEntity(pos);
		if (entity == null || !(entity instanceof NodeBlockEntity)) return 0;
		NodeBlockEntity tileEntity = (NodeBlockEntity) entity;
		return tileEntity.getLightValue();
    }	
	
    
    //client server
    public boolean onBlockPlacedBy(World world, int x, int y, int z, Direction front, EntityLivingBase entityLiving, int metadata)
    {
    	NodeBlockEntity tileEntity = (NodeBlockEntity) world.getTileEntity(new BlockPos(x, y, z));

		tileEntity.onBlockPlacedBy(front, entityLiving, metadata);
		return true;
	}
    
    //server   
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
    	if(worldIn.isRemote == false)
    	{
    		NodeBlockEntity entity = (NodeBlockEntity) worldIn.getTileEntity(pos);
    		entity.onBlockAdded();
    	}   	
    }
    

    
    //server
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
    	//if(par1World.isRemote == false)
    	{
    		NodeBlockEntity entity = (NodeBlockEntity) worldIn.getTileEntity(pos);
	    	entity.onBreakBlock();
	        super.breakBlock(worldIn, pos, state);
    	}
    }
    
    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) 
    {
    	if(Utils.isRemote(worldIn) == false)
    	{
    		NodeBlockEntity entity = (NodeBlockEntity) worldIn.getTileEntity(pos);
	    	entity.onNeighborBlockChange();
    	}
    }

	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getBlock().getMetaFromState(state);
	}
	
	//@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
		for (int ix = 0; ix < blockItemNbr; ix++)
			list.add(new ItemStack(this, 1, ix));
    }

   //client server
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {   
    	NodeBlockEntity entity = (NodeBlockEntity) worldIn.getTileEntity(pos);
//    	entityPlayer.openGui( Eln.instance, 0,world,x ,y, z);
    	return entity.onBlockActivated(playerIn, Direction.from(side), pos);
    }

	@Override
	public boolean hasTileEntity(IBlockState state) 
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		try {
			return (TileEntity) tileEntityClass.getConstructor().newInstance();
		} catch (InstantiationException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
		} catch (SecurityException e) {
			
			e.printStackTrace();
		}
		while(true);
	}


    
}




