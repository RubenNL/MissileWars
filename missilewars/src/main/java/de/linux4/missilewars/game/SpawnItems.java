/*******************************************************************************
 * Copyright (C) 2019 Linux4
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.linux4.missilewars.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

import de.linux4.missilewars.MissileWars;

public class SpawnItems {

	private MissileWars plugin;
	private Game game;

	public SpawnItems(Game game, MissileWars plugin) {
		this.game = game;
		this.plugin = plugin;
	}
	
	public static void removeFromInv(Player player) {
		if (player.getGameMode() != GameMode.CREATIVE) {
			final ItemStack item = player.getInventory().getItemInMainHand();
			final int a = item.getAmount();
			if (a <= 1) {
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			} else {
				item.setAmount(a - 1);
				player.getInventory().setItemInMainHand(item);
			}
		}
	}

	public void spawnFireball(Player player) {
		final Fireball fireball = player.launchProjectile(Fireball.class);
		fireball.setVelocity(player.getLocation().getDirection().multiply(2));
		fireball.setBounce(false);
		fireball.setIsIncendiary(true);
		fireball.setCustomName("§6Fireball");
		fireball.setCustomNameVisible(false);
		fireball.setShooter(player);
		removeFromInv(player);
	}

	public void spawnShield(Player player) {
		final Snowball shield = player.launchProjectile(Snowball.class);
		removeFromInv(player);
		shield.setCustomName("§1Shield");
		shield.setCustomNameVisible(false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if (shield != null) {
					switch (game.getPlayerTeam(player)) {
					case GREEN:
						MissileWars.getMissileCommands().greenShield(shield);
						break;
					case RED:
						MissileWars.getMissileCommands().redShield(shield);
						break;
					default:
						break;
					}
				}
			}
		}, 20L);
	}
}
