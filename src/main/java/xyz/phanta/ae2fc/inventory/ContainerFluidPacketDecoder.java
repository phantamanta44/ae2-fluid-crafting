package xyz.phanta.ae2fc.inventory;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotNormal;
import net.minecraft.entity.player.InventoryPlayer;
import xyz.phanta.ae2fc.tile.TileFluidPacketDecoder;

public class ContainerFluidPacketDecoder extends AEBaseContainer {

    public ContainerFluidPacketDecoder(InventoryPlayer ipl, TileFluidPacketDecoder tile) {
        super(ipl, tile);
        addSlotToContainer(new SlotNormal(tile.getInventory(), 0, 80, 35));
        bindPlayerInventory(ipl, 0, 84);
    }

}
