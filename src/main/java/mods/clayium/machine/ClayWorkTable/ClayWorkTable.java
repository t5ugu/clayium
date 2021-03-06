
package mods.clayium.machine.ClayWorkTable;

import mods.clayium.gui.GuiHandler;
import mods.clayium.machine.common.ClayMachineTempTiered;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ClayWorkTable extends ClayMachineTempTiered {
	public ClayWorkTable() {
		super(Material.ROCK, TileEntityClayWorkTable.class, "clay_work_table", GuiHandler.clayWorkTableGuiID, 0);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setHardness(1F);
		setResistance(4F);
	}
}
