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

import de.linux4.missilewars.MissileWars;
import de.linux4.missilewars.game.Game.PlayerTeam;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.concurrent.ThreadLocalRandom;

import static de.linux4.missilewars.game.Game.PlayerTeam.RED;

public class WinActions {
	private static final ThreadLocalRandom random = ThreadLocalRandom.current();
	private double countdown = MissileWars.getMWConfig().getEndCountdown();
	private boolean taskScheduled = false;
	private static final String prefix = MissileWars.PREFIX;
	private int taskid = 0;
	private Location fireworkLoc;

	public WinActions(Game game, Game.PlayerTeam team) {
		fireworkLoc = game.getSpecSpawn();
		World world = game.getWorld();
		if (game.gameStopped) return;
		game.gameStopped = true;
		MissileCommands.spawnObject(team, "win", game.getWorld());
		for (Player p : world.getPlayers()) {
			p.sendMessage(MissileWars.PREFIX + "§aTeam " + team + " has won the game!");
			game.spectate(p, false);
		}
		firework(team);
		taskScheduled = true;
		taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(MissileWars.getPlugin(MissileWars.class),
				() -> {
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
						MissileWars.getPlugin().reset();
						return;
					}
					countdown--;
					firework(team);
				}, 20L, 20L);
	}

	public void firework(PlayerTeam team) {
		if (!MissileWars.getMWConfig().fireworks()) return;
		int rand = random.nextInt(-72, 20 + 1);
		int rand1 = random.nextInt(-66, 66 + 1);
		Location loc = fireworkLoc.clone();
		loc.setX(rand);
		loc.setZ(rand1);
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.addEffect(FireworkEffect.builder().flicker(true).trail(true)
				.withColor(Color.FUCHSIA).withColor(team == RED ? Color.RED : Color.GREEN).withTrail().with(Type.BALL_LARGE).build());
		fwm.setPower(2);
		fw.setFireworkMeta(fwm);
	}
}
