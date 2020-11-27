package xyz.phanta.ae2fc.parts;

import appeng.api.parts.IPartModel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.sync.GuiBridge;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.PartPatternTerminal;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.item.ItemDenseEncodedPattern;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

import javax.annotation.Nonnull;

public class PartFluidPatternTerminal extends PartPatternTerminal {

    @PartModels
    public static ResourceLocation[] MODELS = new ResourceLocation[] {
            new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/f_pattern_term_on"), // 0
            new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/f_pattern_term_off"), // 1
    };

    private static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODELS[0], MODEL_STATUS_ON);
    private static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODELS[1], MODEL_STATUS_OFF);
    private static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODELS[0], MODEL_STATUS_HAS_CHANNEL);

    public PartFluidPatternTerminal(ItemStack is) {
        super(is);
    }

    @Nonnull
    @Override
    public IPartModel getStaticModels() {
        return this.selectModel( MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL );
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        TileEntity te = this.getTile();
        if (Platform.isWrench(player, player.inventory.getCurrentItem(), te.getPos())) {
            return super.onPartActivate(player, hand, pos);
        }
        if (Platform.isServer()) {
            if (Ae2GuiUtils.FLUID_PATTERN_TERMINAL.hasPermissions(te, this.getSide(), player)) {
                Ae2GuiUtils.openGui(player, te, Ae2GuiUtils.FLUID_PATTERN_TERMINAL, this.getSide());
            } else {
                Platform.openGUI(player, this.getHost().getTile(), this.getSide(), GuiBridge.GUI_ME);
            }
        }
        return true;
    }

    @Override
    public void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removedStack,
                                  ItemStack newStack) {
        if (slot == 1) {
            final ItemStack is = inv.getStackInSlot(1);
            if (!is.isEmpty() && is.getItem() instanceof ItemDenseEncodedPattern) {
                return;
            }
        }
        super.onChangeInventory(inv, slot, mc, removedStack, newStack);
    }

    public void onChangeCrafting(IAEItemStack[] newCrafting, IAEItemStack[] newOutput) {
        IItemHandler crafting = this.getInventoryByName("crafting");
        IItemHandler output = this.getInventoryByName("output");
        if (crafting instanceof AppEngInternalInventory && output instanceof AppEngInternalInventory) {
            for (int x = 0; x < crafting.getSlots() && x < newCrafting.length; x++) {
                final IAEItemStack item = newCrafting[x];
                ((AppEngInternalInventory) crafting)
                        .setStackInSlot(x, item == null ? ItemStack.EMPTY : item.createItemStack());
            }

            for (int x = 0; x < output.getSlots() && x < newOutput.length; x++) {
                final IAEItemStack item = newOutput[x];
                ((AppEngInternalInventory) output)
                        .setStackInSlot(x, item == null ? ItemStack.EMPTY : item.createItemStack());
            }
        }
    }

}
