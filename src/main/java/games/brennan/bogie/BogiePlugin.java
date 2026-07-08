package games.brennan.bogie;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import games.brennan.bogie.hytale.StructureRegistry;
import games.brennan.bogie.spike.PlatformCommand;
import games.brennan.bogie.spike.PlatformComponent;
import games.brennan.bogie.spike.PlatformTickSystem;
import games.brennan.bogie.spike.RideCommand;
import games.brennan.bogie.spike.RiderCarrySystem;
import games.brennan.bogie.spike.StopCommand;

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
        // Phase 1 research spike (docs/DESIGN.md): a circling block-entity
        // platform plus rider carry, to answer the movement-primitive and
        // rider-attachment questions.
        PlatformComponent.TYPE = this.getEntityStoreRegistry()
                .registerComponent(PlatformComponent.class, PlatformComponent::new);
        this.getEntityStoreRegistry().registerSystem(new PlatformTickSystem());
        this.getEntityStoreRegistry().registerSystem(new RiderCarrySystem());

        this.getCommandRegistry().registerCommand(new PlatformCommand());
        this.getCommandRegistry().registerCommand(new RideCommand());
        this.getCommandRegistry().registerCommand(new StopCommand());
    }

    public StructureRegistry getStructures() {
        return structures;
    }
}
