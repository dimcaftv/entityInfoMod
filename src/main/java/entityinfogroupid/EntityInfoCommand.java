package entityinfogroupid;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

public class EntityInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("entityInfo")
                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                .executes(context -> execute(context, EntityArgumentType.getEntities(context, "targets")))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets) {

        if (targets.isEmpty()) {
            context.getSource().sendFeedback(Text.literal("No entities found with the specified name or type."), false);
            return 0;
        }

        for (Entity entity : targets) {
            Vec3d pos = entity.getPos();
            Text posInfo = Text.literal(String.format("Position: (%.0f, %.0f, %.0f)", pos.getX(), pos.getY(), pos.getZ())).styled((style) ->
                    style.withColor(Formatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @s " + entity.getUuid().toString()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip"))));

            String worldString = entity.getEntityWorld().getDimension().effects().toString();
            Text worldInfo = Text.literal(String.format("World: %s", worldString.substring(worldString.indexOf(':') + 1)));

            Text typeInfo = Text.translatable(String.format("Type: %s", entity.getDisplayName().getString()));
            Text entityInfo = Texts.join(List.of(posInfo, worldInfo, typeInfo), Text.literal(" "));

            context.getSource().sendFeedback(entityInfo, false);
        }

        return targets.size();

    }
}
