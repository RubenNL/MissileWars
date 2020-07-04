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

public class MissileCommands {

	private CommandSender console = Bukkit.getConsoleSender();

	public void greenTomahawk(Location l) {
		relativePaste("green_tomahawk", l, 0, -3, -4);
	}

	public void redTomahawk(Location l) {
		relativePaste("red_tomahawk", l, 0, -3, 4);
	}

	public void greenShieldBuster(Location l) {
		relativePaste("green_shieldbuster", l, 0, -3, -4);
	}

	public void redShieldBuster(Location l) {
		relativePaste("red_shieldbuster", l, 0, -3, 4);
	}

	public void greenJuggernaut(Location l) {
		relativePaste("green_juggernaut", l, 0, -3, -4);
	}

	public void redJuggernaut(Location l) {
		relativePaste("red_juggernaut", l, 0, -3, 4);
	}

	public void greenLightning(Location l) {
		relativePaste("green_lightning", l, 0, -3, -5);
	}

	public void redLightning(Location l) {
		relativePaste("red_lightning", l, 0, -3, 5);
	}

	public void greenGuardian(Location l) {
		relativePaste("green_guardian", l, 0, -3, -4);
	}

	public void redGuardian(Location l) {
		relativePaste("red_guardian", l, 0, -3, 4);
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
		Location rel = new Location(l.getWorld(), l.getX() + relX, l.getY() + relY,
				l.getZ() + relZ);
		MissileWars.getWorldEditUtil().pasteSchematic(name, rel, true);
	}

}
