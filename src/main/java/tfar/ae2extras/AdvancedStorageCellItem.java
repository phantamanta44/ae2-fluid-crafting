package tfar.ae2extras;

import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.Api;
import appeng.items.storage.AbstractStorageCell;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AdvancedStorageCellItem extends AbstractStorageCell<IAEItemStack> {

    protected final int perType;
    protected final double idleDrain;
    private final Supplier<Item> itemSupplier;

    public AdvancedStorageCellItem(Properties properties, int kilobytes, double idleDrain, Supplier<Item> itemSupplier) {
        super(properties, null, kilobytes);
        perType = kilobytes * 8;
        this.idleDrain = idleDrain;
        this.itemSupplier = itemSupplier;
    }

    @Override
    public double getIdleDrain() {
        return idleDrain;
    }

    @Nonnull
    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return Api.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    @Override
    public int getBytesPerType(@Nonnull ItemStack itemStack) {
        return perType;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        this.disassembleDrive(player.getHeldItem(hand), world, player);
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    private boolean disassembleDrive(ItemStack stack, World world, PlayerEntity player) {
        if (player.isCrouching()) {
            if (Platform.isClient()) {
                return false;
            }

            PlayerInventory playerInventory = player.inventory;
            IMEInventoryHandler inv = Api.instance().registries().cell().getCellInventory(stack, null, this.getChannel());
            if (inv != null && playerInventory.getCurrentItem() == stack) {
                InventoryAdaptor ia = InventoryAdaptor.getAdaptor(player);
                IItemList<IAEItemStack> list = inv.getAvailableItems(this.getChannel().createList());
                if (list.isEmpty() && ia != null) {
                    playerInventory.setInventorySlotContents(playerInventory.currentItem, ItemStack.EMPTY);
                    ItemStack extraB = ia.addItems(getCellComponent());
                    if (!extraB.isEmpty()) {
                        player.dropItem(extraB, false);
                    }

                    IItemHandler upgradesInventory = this.getUpgradesInventory(stack);

                    for(int upgradeIndex = 0; upgradeIndex < upgradesInventory.getSlots(); ++upgradeIndex) {
                        ItemStack upgradeStack = upgradesInventory.getStackInSlot(upgradeIndex);
                        ItemStack leftStack = ia.addItems(upgradeStack);
                        if (!leftStack.isEmpty() && upgradeStack.getItem() instanceof IUpgradeModule) {
                            player.dropItem(upgradeStack, false);
                        }
                    }

                    this.dropEmptyStorageCellCase(ia, player);
                    if (player.container != null) {
                        player.container.detectAndSendChanges();
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return this.disassembleDrive(stack, context.getWorld(), context.getPlayer()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    public ItemStack getCellComponent() {
        return new ItemStack(itemSupplier.get());
    }

    @Override
    protected void dropEmptyStorageCellCase(final InventoryAdaptor ia, final PlayerEntity player) {
        Api.instance().definitions().materials().emptyStorageCell().maybeStack(1).ifPresent(is -> {
            final ItemStack extraA = ia.addItems(is);
            if (!extraA.isEmpty()) {
                player.dropItem(extraA, false);
            }
        });
    }

}
