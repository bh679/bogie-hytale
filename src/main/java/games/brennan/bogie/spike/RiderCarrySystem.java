package games.brennan.bogie.spike;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import org.joml.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import games.brennan.bogie.core.StructureFrame;
import games.brennan.bogie.core.Vec3;

/**
 * Carries players standing on a platform by the platform's per-tick delta.
 *
 * Research question exercised here: what is the smoothest server-side way to
 * move a player — Player.addLocationChange (relative move, our first
 * candidate) vs writing TransformComponent vs a Teleport component?
 *
 * Deck detection is deck-support style, not coarse AABB (per DESIGN.md
 * lessons): the player's feet must be within the platform's local deck
 * bounds and standing on (or just above) its top surface.
 */
public class RiderCarrySystem extends EntityTickingSystem<EntityStore> {

    // Deck half-extents around the platform, generous so a player standing near
    // it (the block has no collision to stand ON) still counts as a rider.
    private static final double DECK_HALF_XZ = 2.0;
    private static final double DECK_Y_MIN = -1.0;
    private static final double DECK_Y_MAX = 3.0;

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<EntityStore> chunk,
                     Store<EntityStore> store, CommandBuffer<EntityStore> buffer) {
        if (!SpikeState.CARRY_ENABLED.get() || SpikeState.PLATFORMS.isEmpty()) {
            return;
        }
        Player player = chunk.getComponent(index, Player.getComponentType());
        TransformComponent transform = chunk.getComponent(index, TransformComponent.getComponentType());
        if (player == null || transform == null) {
            return;
        }

        Vector3d position = transform.getPosition();
        Vec3 playerPos = new Vec3(position.x(), position.y(), position.z());

        for (StructureFrame frame : SpikeState.PLATFORMS.values()) {
            Vec3 local = frame.worldToLocal(playerPos);
            boolean onDeck = Math.abs(local.x()) <= DECK_HALF_XZ
                    && Math.abs(local.z()) <= DECK_HALF_XZ
                    && local.y() >= DECK_Y_MIN && local.y() <= DECK_Y_MAX;
            if (!onDeck) {
                continue;
            }
            Vec3 delta = frame.velocity().scale(dt);
            player.addLocationChange(chunk.getReferenceTo(index),
                    delta.x(), delta.y(), delta.z(), store);
            return;
        }
    }
}
