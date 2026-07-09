package games.brennan.bogie.spike;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import games.brennan.bogie.core.Vec3;

/**
 * Marks a spawned block entity as a Bogie spike platform and carries its
 * motion parameters. Mutable by design — Hytale ECS components are mutable;
 * the immutable kinematics live in bogie.core.
 */
public class PlatformComponent implements Component<EntityStore> {

    /** Assigned in BogiePlugin.setup() at registration time. */
    public static ComponentType<EntityStore, PlatformComponent> TYPE;

    /** Spawn point, world space — the platform slides in a straight line from here. */
    public Vec3 origin = Vec3.ZERO;
    /** Horizontal travel direction (unit vector, world space). */
    public Vec3 direction = new Vec3(1, 0, 0);
    /** Blocks per second. */
    public double speed = 0.6;
    /** Distance travelled from origin. */
    public double distance = 0.0;

    @Override
    public Component<EntityStore> clone() {
        PlatformComponent copy = new PlatformComponent();
        copy.origin = origin;
        copy.direction = direction;
        copy.speed = speed;
        copy.distance = distance;
        return copy;
    }
}
