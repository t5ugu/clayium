package mods.clayium.gui;

import mods.clayium.machine.ClayCraftingTable.ContainerClayCraftingTable;
import mods.clayium.machine.ClayCraftingTable.GuiClayCraftingTable;
import mods.clayium.machine.ClayCraftingTable.TileEntityClayCraftingTable;
import mods.clayium.machine.ClayWorkTable.ContainerClayWorkTable;
import mods.clayium.machine.ClayWorkTable.GuiClayWorkTable;
import mods.clayium.machine.ClayWorkTable.TileEntityClayWorkTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null) {
            switch (ID) {
                case clayWorkTableGuiID:
                    return new ContainerClayWorkTable(player.inventory, (TileEntityClayWorkTable) tile);
                case clayCraftingTableGuiID:
                    return new ContainerClayCraftingTable(player.inventory, world, pos);
            }
        }

        return null;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Gui getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null) {
            switch (ID) {
                case clayWorkTableGuiID:
                    return new GuiClayWorkTable(player.inventory, (TileEntityClayWorkTable) tile);
                case clayCraftingTableGuiID:
                    return new GuiClayCraftingTable(new ContainerClayCraftingTable(player.inventory, world, pos), (TileEntityClayCraftingTable) tile, state.getBlock());
            }
        }

        return null;
    }

    public static final int clayWorkTableGuiID = 0;
    public static final int clayCraftingTableGuiID = 30;
}
