package games.brennan.bogie.spike;

import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.protocol.MountController;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * /bogie_ride — mount (or dismount) the executing player onto a Bogie platform
 * using Hytale's native mount system.
 *
 * Phase 1 findings:
 * - Player movement is client-authoritative, so nudging the player's position
 *   server-side (Player.addLocationChange) is overridden by the client.
 * - MountedComponent is the real primitive: MountSystems.PlayerMount sets the
 *   player's PlayerInput.mountId to the mount's NetworkId and the CLIENT
 *   attaches and follows the mount as it moves.
 * - Controller choice matters (per HandleMountInput bytecode): Minecart routes
 *   player input INTO the mount (player drives it); BlockMount is chair
 *   semantics — passive rider, server drives the mount, movement input after a
 *   600ms grace dismounts. Bogie wants BlockMount.
 */
public class RideCommand extends AbstractTargetPlayerCommand {

    public RideCommand() {
        super("bogie_ride", "Mount/dismount the Bogie platform (native mount system)");
    }

    @Override
    protected void execute(CommandContext context, Ref<EntityStore> sourceRef,
                           Ref<EntityStore> ref, PlayerRef playerRef, World world,
                           Store<EntityStore> store) {
        // Toggle: already mounted → dismount.
        if (store.getComponent(ref, MountedComponent.getComponentType()) != null) {
            store.removeComponentIfExists(ref, MountedComponent.getComponentType());
            context.sendMessage(Message.raw("Dismounted from Bogie platform."));
            return;
        }

        Ref<EntityStore> platformRef = null;
        for (Ref<EntityStore> candidate : SpikeState.PLATFORMS.keySet()) {
            if (candidate.isValid()) {
                platformRef = candidate;
                break;
            }
        }
        if (platformRef == null) {
            context.sendMessage(Message.raw("No Bogie platform to mount. Run /bogie_platform first."));
            return;
        }

        MountedComponent mounted =
                new MountedComponent(platformRef, new Rotation3f(), MountController.BlockMount);
        store.addComponent(ref, MountedComponent.getComponentType(), mounted);
        context.sendMessage(Message.raw(
                "Mounted to Bogie platform (passenger mode) — don't touch WASD; movement keys dismount. /bogie_ride to dismount."));
    }
}
