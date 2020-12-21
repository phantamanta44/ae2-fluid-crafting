package xyz.phanta.ae2fc.tile.base;

import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import xyz.phanta.ae2fc.inventory.GuiType;

public interface FcPriorityHost extends IPriorityHost {

    GuiType getGuiType();

    @Override
    default GuiBridge getGuiBridge() {
        return GuiBridge.GUI_Handler;
    }

}
