package games.brennan.bogie.spike;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

/** /bogie_ride — toggles carrying players standing on spike platforms. */
public class RideCommand extends CommandBase {

    public RideCommand() {
        super("bogie_ride", "Toggle Bogie platform rider carry");
    }

    @Override
    protected void executeSync(CommandContext context) {
        boolean enabled = !SpikeState.CARRY_ENABLED.get();
        SpikeState.CARRY_ENABLED.set(enabled);
        context.sendMessage(Message.raw("Bogie rider carry " + (enabled ? "ENABLED" : "disabled") + "."));
    }
}
