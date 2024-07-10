package org.battleplugins.arena.module.vault;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class RemovePermissionAction extends EventAction {
    private static final String PERMISSION_KEY = "permission";
    private static final String TRANSIENT_KEY = "transient";

    public RemovePermissionAction(Map<String, String> params) {
        super(params, PERMISSION_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(VaultIntegration.ID)) {
            return;
        }

        Optional<VaultIntegration> moduleOpt = arenaPlayer.getArena().getPlugin()
                .<VaultIntegration>module(VaultIntegration.ID)
                .map(module -> module.initializer(VaultIntegration.class));

        // No vault module (should never happen)
        if (moduleOpt.isEmpty()) {
            return;
        }

        VaultIntegration module = moduleOpt.get();
        String permission = this.get(PERMISSION_KEY);
        boolean isTransient = Boolean.parseBoolean(this.getOrDefault(TRANSIENT_KEY, "true"));
        module.getVaultContainer().removePermission(arenaPlayer.getPlayer(), permission, isTransient);
    }
}
