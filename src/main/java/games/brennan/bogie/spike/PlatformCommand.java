package games.brennan.bogie.spike;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import games.brennan.bogie.core.Vec3;

/**
 * /bogie_platform <block> — spawns a block entity two blocks in front of the
 * player and sets it circling. Mirrors the assembly used by the built-in
 * SpawnBlockCommand.
 */
public class PlatformCommand extends AbstractTargetPlayerCommand {

    private final RequiredArg<String> blockArg =
            this.withRequiredArg("block", "Block type to use as the platform", ArgTypes.BLOCK_TYPE_KEY);

    public PlatformCommand() {
        super("bogie_platform", "Spawn a circling Bogie spike platform");
    }

    @Override
    protected void execute(CommandContext context, Ref<EntityStore> sourceRef,
                           Ref<EntityStore> ref, PlayerRef playerRef, World world,
                           Store<EntityStore> store) {
        TransformComponent playerTransform = store.getComponent(ref, TransformComponent.getComponentType());
        if (playerTransform == null) {
            context.sendMessage(Message.raw("Could not resolve player position."));
            return;
        }

        String blockType = this.blockArg.get(context);
        Vector3d playerPos = playerTransform.getPosition();
        Vector3d spawnPos = new Vector3d(playerPos.getX() + 2.0, playerPos.getY(), playerPos.getZ());

        TimeResource time = (TimeResource) store.getResource(TimeResource.getResourceType());
        Holder<EntityStore> holder = BlockEntity.assembleDefaultBlockEntity(time, blockType, spawnPos);

        PlatformComponent platform = new PlatformComponent();
        platform.center = new Vec3(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        holder.addComponent(PlatformComponent.TYPE, platform);

        store.addEntity(holder, AddReason.SPAWN);
        context.sendMessage(Message.raw(
                "Bogie platform spawned (" + blockType + "), circling you at radius "
                        + platform.radius + ". /bogie_ride to toggle carry, /bogie_stop to clear."));
    }
}
