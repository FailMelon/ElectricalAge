package mods.eln.entity;

import mods.eln.misc.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDummyContainer;

public class ReplicatorEntity extends EntityMob {

	private ReplicatoCableAI replicatorIa = new ReplicatoCableAI(this);
	public ReplicatorEntity(World par1World) {
		super(par1World);
	//	Utils.println("new replicator");
        this.setSize(0.3F, 0.7F);
		
		int p = 0;
		
        this.tasks.addTask(p++, new EntityAISwimming(this));
       // this.tasks.addTask(p++, new EntityAIBreakDoor(this));
        this.tasks.addTask(p++, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(p++, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
        this.tasks.addTask(p++, new EntityAIAttackOnCollide(this, ReplicatorEntity.class, 1.0D, true));
        this.tasks.addTask(p++, replicatorIa);
        this.tasks.addTask(p++, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(p++, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.tasks.addTask(p++, new ConfigurableAiWander(this, 1.0D,20));
        this.tasks.addTask(p, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(p++, new EntityAILookIdle(this));
        p = 1;
        this.targetTasks.addTask(p++, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(p, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(p, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
        this.targetTasks.addTask(p++, new ReplicatorHungryAttack(this, ReplicatorEntity.class, 0, false));
      //  this.targetTasks.addTask(p++, new EntityAINearestAttackableTarget(this, ReplicatorEntity.class, 0, false));
       // this.targetTasks.addTask(p++, replicatorIa);

	}
	
	
	@Override
	public boolean attackEntityAsMob(Entity e) {
		// TODO Auto-generated method stub
		if(e instanceof ReplicatorEntity){
			this.hunger -= 0.2;
			((ReplicatorEntity)e).hunger += 0.2;	
		//	System.out.print("ATTAQUE");
		}
		return super.attackEntityAsMob(e);
	}
	
	
	double hungerTime = 10*60;
	//double hungerTime = 20;
	double hungerToEnergy = 10.0*hungerTime; 
	double hungerToDuplicate = -1;
	double hungerToCanibal = 0.6;
	
	@Override
	protected void updateAITick() {
		// TODO Auto-generated method stub
		super.updateAITick();
//		System.out.print(hunger + " ");
		hunger += 0.05/hungerTime;
		if(hunger > 1){
			if(Math.random() < 0.05/5)
				attackEntityFrom(DamageSource.starve, 1);
		} 
		if(hunger < hungerToDuplicate){
			ReplicatorEntity chicken = new ReplicatorEntity(this.worldObj);
			EntityLiving entityliving = (EntityLiving)chicken;
			entityliving.setLocationAndAngles(this.posX,this.posY,this.posZ,0f,0f);
            entityliving.rotationYawHead = entityliving.rotationYaw;
            entityliving.renderYawOffset = entityliving.rotationYaw;
            worldObj.spawnEntityInWorld(entityliving);
            entityliving.playLivingSound();
            hunger = 0;
		}
	}
	
	void eatElectricity(double e){
		hunger = hunger - e/hungerToEnergy;
	}
	
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0D);
       // this.getAttributeMap().func_111150_b(field_110186_bp).setAttribute(this.rand.nextDouble() * ForgeDummyContainer.zombieSummonBaseChance);
    }

    
    protected boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.silverfish.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.silverfish.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.silverfish.death";
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    } 
    
    protected int getDropItemId()
    {
        return Item.rottenFlesh.itemID;
    }
    
    
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEFINED;
    }
    
    double hunger = (Math.random()-0.5)*0.3;
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
    	super.writeEntityToNBT(nbt);
    	nbt.setDouble("ElnHunger",hunger);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
    	super.readEntityFromNBT(nbt);
    	
    	hunger = nbt.getDouble("ElnHunger");
    }
	
}
