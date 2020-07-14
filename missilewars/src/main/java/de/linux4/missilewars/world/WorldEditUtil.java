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
import java.io.FileInputStream;

import org.bukkit.Location;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WorldEditUtil {

	private File schematics;

	public WorldEditUtil(File schematicFolder) {
		this.schematics = schematicFolder;
	}

	public boolean pasteSchematic(String name, Location loc, boolean ignoreAir) {
		try {
			File schematic = new File(schematics, name + ".schem");
			ClipboardFormat format = ClipboardFormats.findByFile(schematic);
			ClipboardReader reader = format.getReader(new FileInputStream(schematic));
			Clipboard clipboard = reader.read();
			if(Math.abs(loc.getZ())<73 && Math.abs(loc.getZ())+clipboard.getDimensions().getZ()>72 && Math.abs(-27-loc.getX())<23) return false;
			com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);
			Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
					.to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).ignoreAirBlocks(ignoreAir).build();
			Operations.complete(operation);
			editSession.flushSession();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
