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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private int maxPlayers;
	private boolean keepInventory;
	private int itemCap;
	private int resupplyTimer;
	private boolean animatedExplosions;
	private boolean preventMissilesInOwnBase;
	private boolean fireworks;
	private int beginCountdown;
	private int endCountdown;
	private boolean fairGame;
	public Config(File file) {
		FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
		maxPlayers = conf.getInt("max-players", 24);
		keepInventory = conf.getBoolean("keepinventory", true);
		itemCap = conf.getInt("item-cap", 1);
		resupplyTimer = conf.getInt("resupply-timer", 11);
		animatedExplosions = conf.getBoolean("animated-explosions",true);
		preventMissilesInOwnBase = conf.getBoolean("prevent-missiles-in-own-base",false);
		fireworks = conf.getBoolean("fireworks",true);
		beginCountdown = conf.getInt("beginCountdown", 60);
		endCountdown = conf.getInt("endCountdown", 30);
		fairGame = conf.getBoolean("fairGame",true);
		if (maxPlayers % 2 != 0 || maxPlayers <= 0) {
			throw new IllegalArgumentException("max-players should be an even number > 0");
		}
		
		if(resupplyTimer == 0) {
			throw new IllegalArgumentException("resupply-timer should be > 0");
		}
		
		if(itemCap < 0) {
			throw new IllegalArgumentException("item-cap should be >= 0");
		}
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public boolean isKeepInventory() {
		return keepInventory;
	}
	public int getItemCap() {
		return itemCap;
	}
	public int getResupplyTimer() {
		return resupplyTimer;
	}
	public boolean animatedExplosions() {return animatedExplosions;}
	public boolean preventMissilesInOwnBase() {return preventMissilesInOwnBase;}
	public boolean fireworks() {return fireworks;}
	public int getBeginCountdown() {
		return beginCountdown;
	}
	public int getEndCountdown() {
		return endCountdown;
	}
	public boolean fairGame() {return fairGame;}
}
