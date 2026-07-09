package games.brennan.bogie.spike;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import org.joml.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import games.brennan.bogie.core.StructureFrame;
import games.brennan.bogie.core.Vec3;

/**
 * Advances every PlatformComponent entity along its circular path each world
 * tick and publishes the resulting StructureFrame for the rider system.
 *
 * Research question exercised here: does per-tick TransformComponent
 * .setPosition stream smoothly to clients (vs teleportPosition snapping)?
 */
public class PlatformTickSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public Query<EntityStore> getQuery() {
        return PlatformComponent.TYPE;
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<EntityStore> chunk,
                     Store<EntityStore> store, CommandBuffer<EntityStore> buffer) {
        PlatformComponent platform = chunk.getComponent(index, PlatformComponent.TYPE);
        TransformComponent transform = chunk.getComponent(index, TransformComponent.getComponentType());
        if (platform == null || transform == null) {
            return;
        }

        platform.distance += platform.speed * dt;
        Vec3 next = platform.origin.add(platform.direction.scale(platform.distance));

        Vector3d current = transform.getPosition();
        Vec3 velocity = dt > 0
                ? next.subtract(new Vec3(current.x(), current.y(), current.z())).scale(1.0 / dt)
                : Vec3.ZERO;

        transform.setPosition(new Vector3d(next.x(), next.y(), next.z()));

        // Heartbeat (~1/s): 'current' is the transform BEFORE our write. If it
        // drifts from our computed path while a player rides, something else
        // (client authority / HandleMountInput) is also writing the transform.
        platform.logAccumulator += dt;
        if (platform.logAccumulator >= 1.0) {
            platform.logAccumulator = 0;
            double offPath = next.subtract(new Vec3(current.x(), current.y(), current.z())).length();
            System.out.printf("[Bogie] platform dist=%.1f path=(%.1f, %.1f, %.1f) actual=(%.1f, %.1f, %.1f) offPath=%.2f%n",
                    platform.distance, next.x(), next.y(), next.z(),
                    current.x(), current.y(), current.z(), offPath);
        }

        SpikeState.PLATFORMS.put(chunk.getReferenceTo(index),
                new StructureFrame(next, 0, velocity));
    }
}
