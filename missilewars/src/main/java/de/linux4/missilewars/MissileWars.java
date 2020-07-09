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
package de.linux4.missilewars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import de.linux4.missilewars.game.Game;
import de.linux4.missilewars.game.GameManager;
import de.linux4.missilewars.game.ItemManager;
import de.linux4.missilewars.game.JoinChecker;
import de.linux4.missilewars.game.MissileCommands;
import de.linux4.missilewars.listener.EventListener;
import de.linux4.missilewars.world.WorldEditUtil;
import de.linux4.missilewars.world.WorldManager;

public class MissileWars extends JavaPlugin {

	public static final String PREFIX = "§8» §cMissileWars §8┃ ";
	private Game game;
	public static final String NO_PERMISSION = PREFIX + "§cNo Permissions!";
	public static final String NO_CONSOLE_CMD = PREFIX + "§4Only players can execute this command!";
	private GameManager gameManager;
	private int joinTaskId = 0;
	private int gameManagerTaskId = 0;
	private static MissileWars plugin;
	private static MissileCommands commands;
	private static WorldEditUtil worldedit;
	private static WorldManager worldManager;
	private static Config config;

	public void reset() {
		List<Player> players=new ArrayList<>();
		if(game!=null) {
			players=game.getWorld().getPlayers();
			for(Player player:players) {
				player.getInventory().clear();
				player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
			}
		}
		worldManager.reset();
		onDisable();
		game = new Game(worldManager.getWorld());
		gameManager = new GameManager(game, this);
		joinTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new JoinChecker(game), 0L, 5L);
		gameManagerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, gameManager, 0L, 5L);
		Bukkit.getPluginManager().registerEvents(new EventListener(game), this);
		for(Player player:players) game.returnToLobby(player);
	}
	@Override
	public void onEnable() {
		plugin = this;

		this.saveResource("config.yml", false);
		try {
			config = new Config(new File(this.getDataFolder(), "config.yml"));
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		File schematics = new File(this.getDataFolder(), "schematics/");
		schematics.mkdirs();
		try {
			ZipFile zip = new ZipFile(
					new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory() && entry.getName().startsWith("schematics/")) {
					File schematic = new File(this.getDataFolder(), entry.getName());
					InputStream in = zip.getInputStream(entry);
					FileOutputStream out = new FileOutputStream(schematic);
					IOUtils.copy(in, out);
					in.close();
					out.close();
				}
			}
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (worldManager == null) worldManager = new WorldManager();

		worldedit = new WorldEditUtil(schematics);
		commands = new MissileCommands();
		reset();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTask(joinTaskId);
		Bukkit.getScheduler().cancelTask(gameManagerTaskId);
		Bukkit.getScheduler().cancelTasks(this);
		game = null;
		if (gameManager != null) {
			gameManager.stop();
			gameManager = null;
		}
		worldManager.unloadWorld();
	}

	public static MissileWars getPlugin() {
		return plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (sender.hasPermission("missilewars.start")) {
				if (game.gameStarting) {
					if (!game.gameStarted && gameManager.countdown.time > 1) {
						gameManager.startGame();
					}
				} else {
					sender.sendMessage(PREFIX + "§cThe game is not starting!");
				}
			} else {
				sender.sendMessage(NO_PERMISSION);
			}
		} else if (cmd.getName().equalsIgnoreCase("dequip")) {
			if (sender.hasPermission("missilewars.debug")) {
				if (sender instanceof Player) {
					int a = 1;
					if (args.length == 1) {
						try {
							a = Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							// ignored
						}
					}
					new ItemManager(game).dEquip((Player) sender, a);
					sender.sendMessage(PREFIX + "§aYou have been equipped!");
				} else {
					sender.sendMessage(NO_CONSOLE_CMD);
				}
			} else {
				sender.sendMessage(NO_PERMISSION);
			}
		} else if (cmd.getName().equalsIgnoreCase("leave")) {
			if (sender instanceof Player) {
				final Player p = (Player) sender;
				if (game.greenTeam.contains(p) || game.redTeam.contains(p)
						|| p.getScoreboard().getTeam("spec").hasEntry(p.getName())) {
					game.returnToLobby(p);
				} else {
					p.sendMessage(PREFIX + "§cYou are not ingame!");
				}
			} else {
				sender.sendMessage(NO_CONSOLE_CMD);
			}
		} else if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec")) {
			if (sender instanceof Player) {
				final Player p = (Player) sender;
				if (p.getScoreboard().getTeam("spec").hasEntry(p.getName())) {
					p.sendMessage(PREFIX + "§cYou are already spectating!");
				} else {
					game.spectate(p, true);
				}
			} else {
				sender.sendMessage(NO_CONSOLE_CMD);
			}
		} else if (cmd.getName().equalsIgnoreCase("reset")) {
			if (sender.hasPermission("missilewars.reset")) {
				reset();
			} else {
				sender.sendMessage(NO_PERMISSION);
			}
		} else if (cmd.getName().equalsIgnoreCase("mw")) {
			Player senderPlayer=(Player) sender;
			senderPlayer.setExp(0);
			senderPlayer.setLevel(0);
			senderPlayer.setScoreboard(game.getScoreboard());
			game.returnToLobby(senderPlayer);
		}
		return true;
	}

	public static WorldEditUtil getWorldEditUtil() {
		return worldedit;
	}

	public static Config getMWConfig() {
		return config;
	}
}
