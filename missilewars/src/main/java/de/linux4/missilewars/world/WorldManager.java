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
package de.linux4.missilewars.world;

import de.linux4.missilewars.MissileWars;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WorldManager {
	private String mapname;

	public WorldManager() {
		mapname = Bukkit.getBukkitVersion().contains("1.13") ? "map" : "map14";
		if(worldFolderExits()) deleteWorld();
		createWorldFolder();
	}

	public void reset() {
		unloadWorld();
		loadWorld();
	}

	public void unloadWorld() {
		Bukkit.unloadWorld(MissileWars.getMWConfig().getWorldName(), false);
	}
	public void loadWorld() {
		WorldCreator wc = new WorldCreator(MissileWars.getMWConfig().getWorldName());
		World world = Bukkit.getServer().createWorld(wc);
		world.setAutoSave(false);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
		world.setGameRule(GameRule.DO_LIMITED_CRAFTING,false);
		world.setKeepSpawnInMemory(false);
	}
	public World getWorld() {
		return Bukkit.getWorld(MissileWars.getMWConfig().getWorldName());
	}
	public File getWorldFolder() {
		return new File(Bukkit.getWorldContainer(), MissileWars.getMWConfig().getWorldName());
	}
	public boolean worldFolderExits() {
		return getWorldFolder().exists();
	}
	public boolean deleteWorld() {
		try {
			FileUtils.deleteDirectory(getWorldFolder());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public void createWorldFolder() {
		try {
			ZipFile zip = new ZipFile(
					new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().startsWith(mapname + "/")) {
					File file = new File(getWorldFolder(),
							entry.getName().substring((mapname + "/").length()));
					if (entry.isDirectory()) {
						file.mkdirs();
					} else {
						InputStream in = zip.getInputStream(entry);
						FileOutputStream out = new FileOutputStream(file);
						IOUtils.copy(in, out);
						in.close();
						out.close();
					}
				}
			}
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
