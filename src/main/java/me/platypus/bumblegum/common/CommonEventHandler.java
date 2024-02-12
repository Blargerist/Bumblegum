package me.platypus.bumblegum.common;

import me.platypus.bumblegum.common.ai.NewBeeWanderGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEventHandler {

	@SubscribeEvent
	public void entityJoinLevelEvent(EntityJoinLevelEvent e) {
		Entity entity = e.getEntity();
		
		if (!entity.level().isClientSide()) {
			if (entity instanceof Bee bee) {
				Goal toRemove = null;
				//Check if the bee has the vanilla wander goal
				for (WrappedGoal wrapped : bee.getGoalSelector().getAvailableGoals()) {
					Goal goal = wrapped.getGoal();
					if (goal instanceof Bee.BeeWanderGoal) {
						toRemove = goal;
						break;
					}
				}
				//If bee has the vanilla wander goal, remove it and add ours
				if (toRemove != null) {
					bee.getGoalSelector().removeGoal(toRemove);
					bee.getGoalSelector().addGoal(8, new NewBeeWanderGoal(bee));
				}
			}
		}
	}
}
