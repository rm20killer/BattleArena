package org.battleplugins.arena.module.vault;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class EditCurrencyAction extends EventAction {
    private static final String BANK_KEY = "bank";
    private static final String AMOUNT_KEY = "amount";

    public EditCurrencyAction(Map<String, String> params) {
        super(params, AMOUNT_KEY);
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
        double amount = Double.parseDouble(this.get(AMOUNT_KEY));
        String bank = this.getOrDefault(BANK_KEY, null);
        if (bank != null) {
            module.getVaultContainer().editCurrency(arenaPlayer.getPlayer(), bank, amount);
        } else {
            module.getVaultContainer().editCurrency(arenaPlayer.getPlayer(), amount);
        }
    }
}
