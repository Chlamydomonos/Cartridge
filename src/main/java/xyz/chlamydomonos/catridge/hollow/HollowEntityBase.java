package xyz.chlamydomonos.catridge.hollow;

import com.geckolib.animatable.GeoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class HollowEntityBase extends PathfinderMob implements GeoEntity {
    protected HollowEntityBase(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
}
