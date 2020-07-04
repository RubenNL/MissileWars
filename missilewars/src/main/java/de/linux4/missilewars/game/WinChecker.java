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

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import de.linux4.missilewars.MissileWars;
import de.linux4.missilewars.game.Game.PlayerTeam;

public class WinChecker implements Runnable {

	private Location red0;
	private Location red1;
	private Location green0;
	private Location green1;
	private Game game;
	private boolean redWin = false;
	private boolean greenWin = false;
	private static final ThreadLocalRandom random = ThreadLocalRandom.current();
	private double countdown = 30;
	private boolean taskScheduled = false;
	private static final String prefix = MissileWars.PREFIX;
	private int taskid = 0;
	private Location fireworkLoc;
	private static final FireworkEffect redFirework = FireworkEffect.builder().flicker(true).trail(true)
			.withColor(Color.FUCHSIA).withColor(Color.RED).withTrail().with(Type.BALL_LARGE).build();
	private static final FireworkEffect greenFirework = FireworkEffect.builder().flicker(true).trail(true)
			.withColor(Color.LIME).withColor(Color.GREEN).withTrail().with(Type.BALL_LARGE).build();

	public WinChecker(Game game) {
		this.game = game;
		fireworkLoc = game.getSpecSpawn();
		World world = game.getWorld();
		red0 = new Location(world, -28, 52, -72);
		red1 = new Location(world, -26, 52, -72);
		green0 = new Location(world, -26, 52, 72);
		green1 = new Location(world, -28, 52, 72);

	}

	@Override
	public void run() {
		if (game == null) {
			return;
		}
		if (!taskScheduled && (redWin || greenWin)) {
			taskScheduled = true;
			taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(MissileWars.getPlugin(MissileWars.class),
					new Runnable() {
						@Override
						public void run() {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (countdown > 1) {
									if (countdown == 30 || countdown == 15 || countdown == 10 || countdown == 5
											|| countdown == 4 || countdown == 3 || countdown == 2) {
										p.sendMessage(prefix + "§cRestarting in §6" + (int) countdown + " §cseconds!");
									}
								} else if (countdown == 1) {
									p.sendMessage(prefix + "§cRestarting in §6" + (int) countdown + " §csecond!");
								}
							}
							if (countdown == 0) {
								Bukkit.broadcastMessage(prefix + "§cRestarting!");
								game = null;
								red0 = null;
								red1 = null;
								green0 = null;
								green1 = null;
								fireworkLoc = null;
								MissileWars.getPlugin(MissileWars.class).onDisable();
								MissileWars.getPlugin(MissileWars.class).onEnable();
								Bukkit.getScheduler().cancelTask(taskid);
								// System.exit(0);
								return;
							}
							countdown--;
						}

					}, 20L, 20L);
		}
		if (redWin) {
			firework(redFirework);
			return;
		} else if (greenWin) {
			firework(greenFirework);
			return;
		}
		if (red0.getBlock().getType() != Material.NETHER_PORTAL
				|| red1.getBlock().getType() != Material.NETHER_PORTAL) {
			game.gameStopped = true;
			greenWin = true;
			MissileWars.getMissileCommands().spawnObject(PlayerTeam.GREEN,"win",game.getWorld());
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(MissileWars.PREFIX + "§aTeam green has won the game!");
				if (game.getPlayerTeam(p) == PlayerTeam.GREEN || game.getPlayerTeam(p) == PlayerTeam.RED) {
					game.spectate(p, false);
				}
			}
			firework(greenFirework);
			return;
		}
		if (green0.getBlock().getType() != Material.NETHER_PORTAL
				|| green1.getBlock().getType() != Material.NETHER_PORTAL) {
			game.gameStopped = true;
			redWin = true;
			MissileWars.getMissileCommands().spawnObject(PlayerTeam.RED,"win",game.getWorld());
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(MissileWars.PREFIX + "§cTeam red has won the game!");
				if (game.getPlayerTeam(p) == PlayerTeam.GREEN || game.getPlayerTeam(p) == PlayerTeam.RED) {
					game.spectate(p, false);
				}
			}
			firework(redFirework);
			return;
		}
	}

	public void firework(FireworkEffect effect) {
		int rand = random.nextInt(-72, 20 + 1);
		int rand1 = random.nextInt(-66, 66 + 1);
		Location loc = fireworkLoc.clone();
		loc.setX(rand);
		loc.setZ(rand1);
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.addEffect(effect);
		fwm.setPower(2);
		fw.setFireworkMeta(fwm);
	}
}
