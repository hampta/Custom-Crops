/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.bukkit.integration;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.integration.IntegrationManager;
import net.momirealms.customcrops.api.integration.ItemProvider;
import net.momirealms.customcrops.api.integration.LevelerProvider;
import net.momirealms.customcrops.api.integration.SeasonProvider;
import net.momirealms.customcrops.bukkit.integration.item.*;
import net.momirealms.customcrops.bukkit.integration.level.*;
import net.momirealms.customcrops.bukkit.integration.papi.CustomCropsPapi;
import net.momirealms.customcrops.bukkit.integration.season.AdvancedSeasonsProvider;
import net.momirealms.customcrops.bukkit.integration.season.RealisticSeasonsProvider;
import net.momirealms.customcrops.bukkit.item.BukkitItemManager;
import net.momirealms.customcrops.bukkit.world.BukkitWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class BukkitIntegrationManager implements IntegrationManager {

    private final BukkitCustomCropsPlugin plugin;
    private final HashMap<String, LevelerProvider> levelerProviders = new HashMap<>();

    public BukkitIntegrationManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        try {
            this.load();
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to load integrations", e);
        }
    }

    @Override
    public void disable() {
        this.levelerProviders.clear();
    }

    @Override
    public void load() {
        if (isHooked("MMOItems")) {
            registerItemProvider(new MMOItemsItemProvider());
        }
        if (isHooked("Zaphkiel")) {
            registerItemProvider(new ZaphkielItemProvider());
        }
        if (isHooked("NeigeItems")) {
            registerItemProvider(new NeigeItemsItemProvider());
        }
        if (isHooked("CustomFishing", "2.2", "2.3", "2.4")) {
            registerItemProvider(new CustomFishingItemProvider());
        }
        if (isHooked("MythicMobs", "5")) {
            registerItemProvider(new MythicMobsItemProvider());
        }
        if (isHooked("EcoJobs")) {
            registerLevelerProvider(new EcoJobsLevelerProvider());
        }
        if (isHooked("EcoSkills")) {
            registerLevelerProvider(new EcoSkillsLevelerProvider());
        }
        if (isHooked("Jobs")) {
            registerLevelerProvider(new JobsRebornLevelerProvider());
        }
        if (isHooked("MMOCore")) {
            registerLevelerProvider(new MMOCoreLevelerProvider());
        }
        if (isHooked("mcMMO")) {
            registerLevelerProvider(new McMMOLevelerProvider());
        }
        if (isHooked("AureliumSkills")) {
            registerLevelerProvider(new AureliumSkillsProvider());
        }
        if (isHooked("AuraSkills")) {
            registerLevelerProvider(new AuraSkillsLevelerProvider());
        }
        if (isHooked("RealisticSeasons")) {
            registerSeasonProvider(new RealisticSeasonsProvider());
        } else if (isHooked("AdvancedSeasons", "1.4", "1.5", "1.6")) {
            registerSeasonProvider(new AdvancedSeasonsProvider());
        }
        if (isHooked("Vault")) {
            VaultHook.init();
        }
        if (isHooked("PlaceholderAPI")) {
            new CustomCropsPapi(plugin).load();
        }
    }

    private boolean isHooked(String hooked) {
        if (Bukkit.getPluginManager().getPlugin(hooked) != null) {
            plugin.getPluginLogger().info(hooked + " hooked!");
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean isHooked(String hooked, String... versionPrefix) {
        Plugin p = Bukkit.getPluginManager().getPlugin(hooked);
        if (p != null) {
            String ver = p.getDescription().getVersion();
            for (String prefix : versionPrefix) {
                if (ver.startsWith(prefix)) {
                    plugin.getPluginLogger().info(hooked + " hooked!");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean registerLevelerProvider(@NotNull LevelerProvider leveler) {
        if (levelerProviders.containsKey(leveler.identifier())) return false;
        levelerProviders.put(leveler.identifier(), leveler);
        return true;
    }

    @Override
    public boolean unregisterLevelerProvider(@NotNull String id) {
        return levelerProviders.remove(id) != null;
    }

    @Override
    @Nullable
    public LevelerProvider getLevelerProvider(String plugin) {
        return levelerProviders.get(plugin);
    }

    @Override
    public void registerSeasonProvider(@NotNull SeasonProvider season) {
        ((BukkitWorldManager) plugin.getWorldManager()).seasonProvider(season);
    }

    @Override
    public boolean registerItemProvider(@NotNull ItemProvider item) {
        return ((BukkitItemManager) plugin.getItemManager()).registerItemProvider(item);
    }

    @Override
    public boolean unregisterItemProvider(@NotNull String id) {
        return ((BukkitItemManager) plugin.getItemManager()).unregisterItemProvider(id);
    }
}