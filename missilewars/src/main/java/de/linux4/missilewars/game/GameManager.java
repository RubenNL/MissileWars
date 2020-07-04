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
import org.bukkit.entity.Player;

import de.linux4.missilewars.MissileWars;
import de.linux4.missilewars.game.Game.PlayerTeam;

public class GameManager implements Runnable {

	private Game game;
	public boolean countdownFinished = false;
	private MissileWars plugin;
	public Countdown countdown;
	private int countdownTaskId = 0;
	private int itemManagerTaskId = 0;
	private int winCheckerTaskId = 0;

	public GameManager(Game game, MissileWars plugin) {
		this.plugin = plugin;
		this.game = game;
	}

	public void stop() {
		game = null;
		plugin = null;
		countdown = null;
		Bukkit.getScheduler().cancelTask(countdownTaskId);
		Bukkit.getScheduler().cancelTask(itemManagerTaskId);
		Bukkit.getScheduler().cancelTask(winCheckerTaskId);
	}

	@Override
	public void run() {
		if ((game.greenTeam.size() >= 1 || game.redTeam.size() >= 1) && !game.gameStarting) {
			game.setGameStarting(true);
			countdown = new Countdown(this);
			countdownTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, countdown, 0L, 20L);
		} else if (game.gameStarting && countdownFinished && !game.gameStarted) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (game.getPlayerTeam(player) == PlayerTeam.GREEN) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							game.greenTeleport(player);
						}
					}, 0L);
				} else if (game.getPlayerTeam(player) == PlayerTeam.RED) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							game.redTeleport(player);
						}
					}, 0L);
				}
			}
			itemManagerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new ItemManager(game), 0L,
					MissileWars.getMWConfig().getResupplyTimer() * 20L);
			winCheckerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new WinChecker(game), 0L, 5L);
			game.setGameStarted(true);
		}
	}

	public void setCountdownFinished(boolean countdownFinished) {
		this.countdownFinished = countdownFinished;
	}

	public void startGame() {
		countdown.time = 1;
	}

}
