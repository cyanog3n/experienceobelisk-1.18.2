package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AttractionObeliskEntity extends ExperienceReceivingEntity{

    public AttractionObeliskEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.ATTRACTIONOBELISK_BE.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(level.getGameTime() % 10 == 0){



            int radius = 7;

            AABB area = new AABB(
                    pos.getX() - radius,
                    pos.getY() - radius,
                    pos.getZ() - radius,
                    pos.getX() + radius,
                    pos.getY() + radius,
                    pos.getZ() + radius);


            List<PathfinderMob> list = level.getEntitiesOfClass(PathfinderMob.class, area);
            if(!list.isEmpty()) for(PathfinderMob m : list){
               // m.setTarget();
                m.goalSelector.addGoal(1, new MeleeAttackGoal(m, 1, true));
            }
        }


    }
}
