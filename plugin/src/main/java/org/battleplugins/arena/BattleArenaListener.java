package org.battleplugins.arena;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.battleplugins.arena.editor.ArenaEditorWizard;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

class BattleArenaListener implements Listener {
    private final BattleArena plugin;

    public BattleArenaListener(BattleArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        // There is logic called later, however by this point all plugins
        // using the BattleArena API should have been loaded. As modules will
        // listen for this event to register their behavior, we need to ensure
        // they are fully initialized so any references to said modules in
        // arena config files will be valid.
        new BattleArenaPostInitializeEvent(this.plugin).callEvent();

        this.plugin.postInitialize();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
        if (message.equalsIgnoreCase("cancel")) {
            ArenaEditorWizard.wizardContext(event.getPlayer()).ifPresent(ctx -> {
                event.setCancelled(true);

                ctx.getWizard().onCancel(ctx);
            });
        }
    }
}
