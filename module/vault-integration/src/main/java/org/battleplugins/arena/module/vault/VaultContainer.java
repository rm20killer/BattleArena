package org.battleplugins.arena.module.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultContainer {
    private final Economy economy;
    private final Permission permission;

    public VaultContainer() {
        this.economy = createEconomy();
        this.permission = createPermission();
    }

    public void editCurrency(Player player, double amount) {
        if (this.economy == null) {
            return;
        }

        this.economy.depositPlayer(player, amount);
    }

    public void editCurrency(Player player, String bank, double amount) {
        if (this.economy == null) {
            return;
        }

        this.economy.depositPlayer(player, bank, amount);
    }

    public void addPermission(Player player, String permission, boolean isTransient) {
        if (this.permission == null) {
            return;
        }

        if (isTransient) {
            this.permission.playerAddTransient(player, permission);
        } else {
            this.permission.playerAdd(player, permission);
        }
    }

    public void removePermission(Player player, String permission, boolean isTransient) {
        if (this.permission == null) {
            return;
        }

        if (isTransient) {
            this.permission.playerRemoveTransient(player, permission);
        } else {
            this.permission.playerRemove(player, permission);
        }
    }

    @Nullable
    private static Economy createEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }

        return rsp.getProvider();
    }

    @Nullable
    private static Permission createPermission() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return null;
        }

        return rsp.getProvider();
    }
}
