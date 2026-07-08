package games.brennan.bogie.hytale;

import games.brennan.bogie.core.StructureFrame;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side registry of live moving structures, keyed by id. The Hytale
 * adapter layer (this package) owns everything that touches the server API;
 * frames themselves stay engine-agnostic.
 */
public class StructureRegistry {

    private final Map<UUID, StructureFrame> frames = new ConcurrentHashMap<>();

    public UUID register(StructureFrame frame) {
        UUID id = UUID.randomUUID();
        frames.put(id, frame);
        return id;
    }

    public Optional<StructureFrame> get(UUID id) {
        return Optional.ofNullable(frames.get(id));
    }

    public void update(UUID id, StructureFrame frame) {
        frames.computeIfPresent(id, (k, old) -> frame);
    }

    public void remove(UUID id) {
        frames.remove(id);
    }

    public Map<UUID, StructureFrame> snapshot() {
        return Map.copyOf(frames);
    }
}
