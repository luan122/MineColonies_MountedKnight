package com.mountedknight.blocks;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.mountedknight.registry.ModBuildings;
import org.jetbrains.annotations.NotNull;

/**
 * The Stables hut block.
 *
 * Extends MineColonies' {@link AbstractBlockHut} so that:
 * - It can only be placed via the Structurize build tool
 * - Right-clicking opens the MineColonies building GUI
 * - It appears in the build tool's building browser
 * - It creates a TileEntityColonyBuilding for colony integration
 */
public class BlockHutStables extends AbstractBlockHut<BlockHutStables> {

    public BlockHutStables() {
        super();
    }

    @NotNull
    @Override
    public String getHutName() {
        return "blockhut" + ModBuildings.STABLES_ID;
    }

    @Override
    public BuildingEntry getBuildingEntry() {
        return ModBuildings.STABLES.get();
    }
}
