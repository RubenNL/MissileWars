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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.linux4.missilewars.game.GameManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class WorldManager {

	private static final String NAME_PREFIX = "mw";
	private String mapname;

	public WorldManager() {
		mapname = Bukkit.getBukkitVersion().contains("1.13") ? "map" : "map14";
		reset();
	}

	public void reset() {
		unloadWorld();
		getWorld();
	}

	public void unloadWorld() {
		Bukkit.unloadWorld(NAME_PREFIX, false);
	}
	public World getWorld() {
		World world=Bukkit.getWorld(NAME_PREFIX);
		if(world!=null) return world;
		File mapFolder = new File(Bukkit.getWorldContainer(), NAME_PREFIX);
		if(!mapFolder.exists()) {
			try {
				ZipFile zip = new ZipFile(
						new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().startsWith(mapname + "/")) {
						File file = new File(mapFolder,
								entry.getName().substring((mapname + "/").length(), entry.getName().length()));
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
		WorldCreator wc = new WorldCreator(NAME_PREFIX);
		world=Bukkit.getServer().createWorld(wc);
		world.setAutoSave(false);
		world.setKeepSpawnInMemory(false);
		return world;
	}
}
