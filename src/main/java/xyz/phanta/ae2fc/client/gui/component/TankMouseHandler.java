package xyz.phanta.ae2fc.client.gui.component;

import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import xyz.phanta.ae2fc.constant.NameConst;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TankMouseHandler implements MouseRegionManager.Handler {

    private final IAEFluidTank tank;
    private final int index;

    public TankMouseHandler(IAEFluidTank tank, int index) {
        this.tank = tank;
        this.index = index;
    }

    @Nullable
    @Override
    public List<String> getTooltip() {
        IAEFluidStack fluid = tank.getFluidInSlot(index);
        return Arrays.asList(
                fluid != null ? fluid.getFluidStack().getLocalizedName() : I18n.format(NameConst.TT_EMPTY),
                TextFormatting.GRAY + String.format("%,d / %,d mB",
                        fluid != null ? fluid.getStackSize() : 0L, tank.getTankProperties()[index].getCapacity()));
    }

}
