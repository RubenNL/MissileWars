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

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class AnimatedExplosion {
	
	public static void createExplosion(List<Block> blockList) {
		for (final Block b : blockList) {
			final Material material = b.getType();
			if (material != Material.AIR && material != Material.TNT) {
				final FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
				b.setType(Material.AIR);
				fb.setDropItem(false);
				fb.setCustomName("MW-AE");
				fb.setCustomNameVisible(false);
				final float x = -0.25F + (float) (Math.random() * ((0.25 - -0.25) + 0.25));
				final float y = -0.25F + (float) (Math.random() * ((0.25 - -0.25) + 0.25));
				final float z = -0.25F + (float) (Math.random() * ((0.25 - -0.25) + 0.25));
				fb.setVelocity(new Vector(x, y, z));
			}
		}

	}
}
