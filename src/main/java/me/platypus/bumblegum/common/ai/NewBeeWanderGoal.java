package me.platypus.bumblegum.common.ai;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class NewBeeWanderGoal extends Goal {
    private static final int WANDER_THRESHOLD = 8;
    
    private final Bee bee;

    public NewBeeWanderGoal(Bee bee) {
    	this.bee = bee;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
       return bee.getNavigation().isDone() && bee.getRandom().nextInt(10) == 0;
    }

    public boolean canContinueToUse() {
       return bee.getNavigation().isInProgress();
    }

    public void start() {
       Vec3 vec3 = this.findPos();
       if (vec3 != null) {
    	   bee.getNavigation().moveTo(bee.getNavigation().createPath(BlockPos.containing(vec3), 1), 1.0D);
       }

    }

    @Nullable
    private Vec3 findPos() {
       Vec3 vec3;
       //If bee is 8 blocks (vanilla 22) or more from hive, direct pathing search back towards it
       if (bee.isHiveValid() && !bee.closerThan(bee.getHivePos(), WANDER_THRESHOLD)) {
          Vec3 vec31 = Vec3.atCenterOf(bee.getHivePos());
          vec3 = vec31.subtract(bee.position()).normalize();
       } else {
          vec3 = bee.getViewVector(0.0F);
       }

       //Try to find a position forward
       Vec3 vec = findPos(vec3);
       
       //If there's nowhere to go forward, try to find a position backwards
       if (vec == null) {
    	   vec = findPos(vec3.reverse());
       }
       return vec;
    }
    
    private Vec3 findPos(Vec3 vec3) {
    	//Try to find a hover position
    	Vec3 vec32 = HoverRandomPos.getPos(bee, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
        
    	//If no position, try to find somewhere in the air
        if (vec32 == null) {
            vec32 = AirAndWaterRandomPos.getPos(bee, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
        }
        //If still no position, fail
        if (vec32 == null) {
        	return null;
        }
        
        int worldHeight = bee.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int)vec32.x(), (int)vec32.z());
        
        //If position is in an empty column, fail
        if (worldHeight <= bee.level().getMinBuildHeight()) {
      	  return null;
        }
        //If position is below the lowest cubic chunk with blocks, try moving up 8 blocks (the furthest the pathing would've been searching?). If position is not solid, return it. Otherwise, fail.
        if (vec32.y() < findLowestSectionHeightWithBlocks(bee.level(), (int)vec32.x(), (int)vec32.z())) {
        	BlockPos pos = new BlockPos((int)vec32.x(), (int)vec32.y()+8, (int)vec32.z());
        	
        	if (!GoalUtils.isSolid(bee, pos)) {
        		return new Vec3(pos.getX(), pos.getY(), pos.getZ());
        	}
        	return null;
        }
        //Position failed to fail, so return it
        return vec32;
    }
    
    /*
     * Searches the chunk for the highest cubic section containing blocks other than air and returns the height, otherwise returning the max build height for the world
     */
    private int findLowestSectionHeightWithBlocks(Level level, int x, int z) {
		LevelChunk chunk = bee.level().getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
		
		for (int i = 0; i < chunk.getSectionsCount(); i++) {
			LevelChunkSection section = chunk.getSection(i);
			
			if (!section.hasOnlyAir()) {
				return SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(i));
			}
		}
		return level.getMaxBuildHeight();
    }
 }