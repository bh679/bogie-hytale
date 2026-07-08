package games.brennan.bogie;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import games.brennan.bogie.hytale.StructureRegistry;

import javax.annotation.Nonnull;

/**
 * Bogie — moving-structure physics for Hytale.
 *
 * Server-side plugin entry point. All simulation runs on the Hytale server
 * (Hytale has no client-side game logic), so moving structures work for any
 * vanilla client that joins.
 */
public class BogiePlugin extends JavaPlugin {

    private final StructureRegistry structures = new StructureRegistry();

    public BogiePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // Phase 1 (see docs/DESIGN.md): tick loop that advances each
        // MovingStructure's kinematic frame and republishes block/entity
        // positions. Event + command registration land here as the Hytale
        // API surface for block manipulation is mapped.
    }

    public StructureRegistry getStructures() {
        return structures;
    }
}
