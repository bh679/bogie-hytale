package games.brennan.bogie.spike;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;

/** /bogie_stop — despawns all spike platforms and disables carry. */
public class StopCommand extends AbstractTargetPlayerCommand {

    public StopCommand() {
        super("bogie_stop", "Remove all Bogie spike platforms");
    }

    @Override
    protected void execute(CommandContext context, Ref<EntityStore> sourceRef,
                           Ref<EntityStore> ref, PlayerRef playerRef, World world,
                           Store<EntityStore> store) {
        List<Ref<EntityStore>> refs = List.copyOf(SpikeState.PLATFORMS.keySet());
        int removed = 0;
        for (Ref<EntityStore> platformRef : refs) {
            if (platformRef.isValid()) {
                store.removeEntity(platformRef, RemoveReason.REMOVE);
                removed++;
            }
        }
        SpikeState.clear();
        SpikeState.CARRY_ENABLED.set(false);
        context.sendMessage(Message.raw("Removed " + removed + " Bogie platform(s)."));
    }
}
