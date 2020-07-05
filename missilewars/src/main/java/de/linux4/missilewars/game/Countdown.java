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
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import de.linux4.missilewars.MissileWars;

public class Countdown implements Runnable {

	private GameManager manager;
	public double time = MissileWars.getMWConfig().getBeginCountdown();
	public double origTime = MissileWars.getMWConfig().getBeginCountdown();
	private static final String prefix = MissileWars.PREFIX;

	public Countdown(GameManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		if (!manager.countdownFinished) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setExp((float) (time / origTime));
				player.setLevel((int) time);
				if (time == 60 || time == 45 || time == 30 || time == 15 || time == 10 || time == 5 || time == 4
						|| time == 3 || time == 2) {
					player.sendMessage(prefix + "§aStarting in §6" + (int) time + "§a seconds!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				} else if (time == 1) {
					player.sendMessage(prefix + "§aStarting in §6" + (int) time + "§a second!");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				} else if (time == 0) {
					if (!manager.countdownFinished) {
						player.setExp(0);
						player.sendMessage(prefix + "§aStarting §6now!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						manager.setCountdownFinished(true);
					}
				}
			}
			time--;
		}
	}

}
