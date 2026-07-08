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

    /** Centre of the circular path, world space. */
    public Vec3 center = Vec3.ZERO;
    public double radius = 3.0;
    /** Radians per second. */
    public double angularSpeed = 0.4;
    public double angle = 0.0;

    @Override
    public Component<EntityStore> clone() {
        PlatformComponent copy = new PlatformComponent();
        copy.center = center;
        copy.radius = radius;
        copy.angularSpeed = angularSpeed;
        copy.angle = angle;
        return copy;
    }
}
