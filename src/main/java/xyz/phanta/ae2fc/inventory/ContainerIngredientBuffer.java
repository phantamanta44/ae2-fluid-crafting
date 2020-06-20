package xyz.phanta.ae2fc.inventory;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotNormal;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;
import xyz.phanta.ae2fc.tile.TileIngredientBuffer;

public class ContainerIngredientBuffer extends AEBaseContainer implements TankDumpable {

    private final TileIngredientBuffer tile;

    public ContainerIngredientBuffer(InventoryPlayer ipl, TileIngredientBuffer tile) {
        super(ipl, tile);
        this.tile = tile;
        IItemHandler inv = tile.getInternalInventory();
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotNormal(inv, i, 8 + 18 * i, 108));
        }
        bindPlayerInventory(ipl, 0, 140);
    }

    public TileIngredientBuffer getTile() {
        return tile;
    }

    @Override
    public boolean canDumpTank(int index) {
        return tile.getFluidInventory().getFluidInSlot(index) != null;
    }

    @Override
    public void dumpTank(int index) {
        if (index >= 0 && index < tile.getFluidInventory().getSlots()) {
            tile.getFluidInventory().setFluidInSlot(index, null);
        }
    }

}
