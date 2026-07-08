package games.brennan.bogie.spike;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import games.brennan.bogie.core.StructureFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shared spike state between commands and tick systems. Phase 1 throwaway:
 * assumes a single world (all access happens on that world's tick thread,
 * apart from the carry toggle).
 */
public final class SpikeState {

    /** Live platform frames, published by PlatformTickSystem each tick. */
    public static final Map<Ref<EntityStore>, StructureFrame> PLATFORMS = new ConcurrentHashMap<>();

    /** Whether RiderCarrySystem applies platform deltas to players. */
    public static final AtomicBoolean CARRY_ENABLED = new AtomicBoolean(false);

    private SpikeState() {
    }

    public static void clear() {
        PLATFORMS.clear();
    }
}
