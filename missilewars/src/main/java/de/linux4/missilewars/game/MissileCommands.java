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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import de.linux4.missilewars.MissileWars;

import java.util.*;

public class MissileCommands {

	private CommandSender console = Bukkit.getConsoleSender();

	public static void spawnMissile(Game.PlayerTeam team,String missileName, Location location) {
		List position=positions.get(missileName);
		String teamName;
		switch (team) {
			case GREEN:
				position.set(2,-(Integer) position.get(2));
				teamName="green";
				break;
			case RED:
				teamName="red";
				break;
			default:
				return;
		}
		Location rel = new Location(
				location.getWorld(),
				location.getX() + (Integer) position.get(0),
				location.getY() + (Integer) position.get(1),
				location.getZ() + (Integer) position.get(2)
		);
		MissileWars.getWorldEditUtil().pasteSchematic(teamName+"_"+missileName, rel, true);
	}
	public static Map<String, List<Integer>> positions=new HashMap<>();
	static {
		positions.put("tomahawk", Arrays.asList(0,-3,4));
		positions.put("shieldbuster", Arrays.asList(0,-3,4));
		positions.put("juggernaut", Arrays.asList(0,-3,4));
		positions.put("lightning", Arrays.asList(0,-3,5));
		positions.put("guardian", Arrays.asList(0,-3,4));
	}

	public void redShield(Snowball snowball) {
		MissileWars.getWorldEditUtil().pasteSchematic("red_shield", snowball.getLocation(), true);
	}

	public void greenShield(Snowball snowball) {
		MissileWars.getWorldEditUtil().pasteSchematic("green_shield", snowball.getLocation(), true);
	}

	public void redWin() {
		MissileWars.getWorldEditUtil().pasteSchematic("red_win",
				new Location(MissileWars.getWorldManager().getActiveWorld(), -27, 88, -51), true);
	}

	public void greenWin() {
		MissileWars.getWorldEditUtil().pasteSchematic("green_win",
				new Location(MissileWars.getWorldManager().getActiveWorld(), -27, 88, 51), true);
	}

	protected void command(String cmd) {
		Bukkit.dispatchCommand(console, cmd);
	}

	protected void relativePaste(String name, Location l, double relX, double relY, double relZ) {

	}

}
