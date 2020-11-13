package xyz.phanta.ae2fc.client.gui;

import appeng.client.gui.implementations.GuiPriority;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

import java.io.IOException;

public class GuiMyPriority extends GuiPriority {

    public GuiMyPriority(final InventoryPlayer inventoryPlayer, final IPriorityHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == ReflectionHelper.getPrivateValue(GuiPriority.class, this, "originalGuiBtn")) {
            GuiBridge originalGui = ReflectionHelper.getPrivateValue(GuiPriority.class, this, "OriginalGui");
            Ae2GuiUtils.switchGui(Ae2GuiUtils.valueOf(originalGui.ordinal()));
            return;
        }

        super.actionPerformed(btn);
    }

    protected String getBackground() {
        return "guis/priority.png";
    }
}
