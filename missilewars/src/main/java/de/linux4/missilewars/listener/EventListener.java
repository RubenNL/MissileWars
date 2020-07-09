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
package de.linux4.missilewars.listener;

import de.linux4.missilewars.game.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import de.linux4.missilewars.MissileWars;
import de.linux4.missilewars.game.Game.PlayerTeam;

import static de.linux4.missilewars.game.Game.PlayerTeam.GREEN;
import static de.linux4.missilewars.game.Game.PlayerTeam.RED;

public class EventListener implements Listener {

	private Game game;
	private static final String prefix = MissileWars.PREFIX;
	private ItemStack fireball;
	private ItemStack tomahawk;
	private ItemStack juggernaut;
	private ItemStack shieldBuster;
	private ItemStack guardian;
	private ItemStack lightning;
	private ItemStack shield;
	private SpawnItems spawnItems;
	private static final MissileWars plugin = MissileWars.getPlugin();

	public EventListener(Game game) {
		this.game = game;
		spawnItems = new SpawnItems(game, plugin);
		fireball = game.fireball;
		tomahawk = game.tomahawk;
		juggernaut = game.juggernaut;
		shieldBuster = game.shieldBuster;
		guardian = game.guardian;
		lightning = game.lightning;
		shield = game.shield;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if(event.getBlock().getWorld()!=game.getWorld()) return;
		String[] lines = event.getLines();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = ChatColor.translateAlternateColorCodes('&', line);
			event.setLine(i, line);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getPlayer().getWorld()!=game.getWorld()) return;
		final Player p = event.getPlayer();
		final Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
			final ItemStack item = p.getInventory().getItemInMainHand();
			String name = item != null && item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : "";
			if (fireball.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
				spawnItems.spawnFireball(p);
				event.setCancelled(true);
				return;
			} else if (shield.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
				spawnItems.spawnShield(p);
				event.setCancelled(true);
				return;
			}
			if (action != Action.RIGHT_CLICK_AIR) {
				final Block clicked = event.getClickedBlock();
				final Location l = clicked.getLocation();
				if (clicked.getState() != null && clicked.getState() instanceof Sign) {
					final Sign sign = (Sign) clicked.getState();
					final String[] lines = sign.getLines();
					for (String line : lines) {
						if (line.equalsIgnoreCase("§8[§cMW§8]")) {
							for (String line2 : lines) {
								if (line2.equalsIgnoreCase("§aLobby")) {
									game.returnToLobby(p);
									break;
								} else if (line2.equalsIgnoreCase("§aSpectate")) {
									game.spectate(p, true);
								}
							}
							break;
						}
					}
				}
				if(name.length()>2) {
					String strippedName = name.toLowerCase().substring(2);
					if (MissileCommands.positions.containsKey(strippedName)) {
						event.setCancelled(true);
						if (MissileCommands.spawnObject(game.getPlayerTeam(p), strippedName, l, 65,false))
							SpawnItems.removeFromInv(p);
						else p.sendMessage("invalid location!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(
				prefix + game.getPlayerPrefix(event.getPlayer()) + event.getPlayer().getName() + "§c left the game");
		game.removeAllTeams(event.getPlayer());
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.getPlayer().getWorld()!=game.getWorld()) return;
		Player player = event.getPlayer();
		String message = event.getMessage();
		message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replaceAll("%", "%%");
		event.setFormat(game.getPlayerPrefix(player) + player.getName() + " §6» §7" + message);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(event.getEntity().getWorld()!=game.getWorld()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if(event.getPlayer().getWorld()!=game.getWorld()) return;
		Player player = event.getPlayer();
		if (game.getPlayerTeam(player) == GREEN) {
			event.setRespawnLocation(game.getGreenSpawn());
		} else if (game.getPlayerTeam(player) == RED) {
			event.setRespawnLocation(game.getRedSpawn());
		} else if (game.getPlayerTeam(player) == PlayerTeam.SPEC) {
			event.setRespawnLocation(game.getSpecSpawn());
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					game.spectate(event.getPlayer(), true);
				}
			}, 2L);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getEntity().getWorld()!=game.getWorld()) return;
		if (MissileWars.getMWConfig().isKeepInventory()) {
			event.setKeepInventory(true);
		}
		event.getDrops().clear();
		String deathmsg = event.getDeathMessage().replaceFirst(event.getEntity().getName(),
				event.getEntity().getDisplayName() + "§r");
		if (event.getEntity().getKiller() != null) {
			deathmsg = deathmsg.replaceFirst(event.getEntity().getKiller().getName(),
					event.getEntity().getKiller().getDisplayName() + "§r");
		}
		event.setDeathMessage(deathmsg);
		// Autorespawn
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				event.getEntity().spigot().respawn();
			}
		}, 0L);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity().getWorld()!=game.getWorld()) return;
		if (!game.gameStarted && event.getCause() != DamageCause.VOID) {
			event.setCancelled(true);
		} else if (event.getCause() == DamageCause.VOID) {
			if (event.getEntity() instanceof Player) {
				event.setDamage(((Player) event.getEntity()).getHealth());
			}
		} else if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (game.getPlayerTeam(p) == PlayerTeam.SPEC || game.getPlayerTeam(p) == PlayerTeam.NONE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity().getWorld()!=game.getWorld()) return;
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player p1 = (Player) event.getEntity();
			Player p2 = (Player) event.getDamager();
			if (game.getPlayerTeam(p1) == PlayerTeam.SPEC || game.getPlayerTeam(p2) == PlayerTeam.SPEC) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked().getWorld()!=game.getWorld()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if(event.getPlayer().getWorld()!=game.getWorld()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getPlayer().getWorld()!=game.getWorld()) return;
		if (event.getBlock().getType() == Material.NETHER_PORTAL || event.getBlock().getType() == Material.OBSIDIAN) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if(event.getPlayer().getWorld()!=game.getWorld()) return;
		if (event.getReason().equalsIgnoreCase("Flying is not enabled on this server")) { // stop kicking falling
																							// players
			final Player p = event.getPlayer();
			if (p.getVelocity().getY() < 0) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if(event.getEntity().getWorld()!=game.getWorld()) return;
		if (event.getEntity() instanceof Player) {
			if(game.getPlayerTeam((Player) event.getEntity())==PlayerTeam.NONE) return;
			final Player p = (Player) event.getEntity();
			if (game.getPlayerTeam(p) == PlayerTeam.SPEC && p.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if(event.blockList().get(0).getWorld()!=game.getWorld()) return;
		AnimatedExplosion.createExplosion(event.blockList());
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.getLocation().getWorld()!=game.getWorld()) return;
		if(event.getEntity().getType()== EntityType.FIREBALL) {
			for (Block block : event.blockList()) {
				if (block.getType() == Material.NETHER_PORTAL) {
					event.setCancelled(true);
					return;
				}
			}
		}
		for(Block block:event.blockList()) {
			if(block.getType()==Material.NETHER_PORTAL) {
				new WinChecker(game,block.getZ()>0?RED:GREEN);
				return;
			}
		}
		AnimatedExplosion.createExplosion(event.blockList());
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if(event.getBlock().getWorld()!=game.getWorld()) return;
		if (!game.gameStarted) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onChangedWorld(PlayerChangedWorldEvent event) {
		Player player=event.getPlayer();
		if(event.getFrom()==game.getWorld()) game.removeAllTeams(player);
		if(event.getPlayer().getWorld()==game.getWorld()) {
			player.setExp(0);
			player.setLevel(0);
			player.setScoreboard(game.getScoreboard());
			game.returnToLobby(player);
		}
	}
}
