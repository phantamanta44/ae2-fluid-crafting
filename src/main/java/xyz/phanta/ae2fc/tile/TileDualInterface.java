package xyz.phanta.ae2fc.tile;

import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.core.sync.GuiBridge;
import appeng.fluids.helper.DualityFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.IPriorityHost;
import appeng.tile.grid.AENetworkInvTile;
import appeng.util.Platform;
import appeng.util.inv.IInventoryDestination;
import appeng.util.inv.InvOperation;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import xyz.phanta.ae2fc.init.FcBlocks;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class TileDualInterface extends AENetworkInvTile
        implements IGridTickable, IInventoryDestination, IInterfaceHost, IPriorityHost, IFluidInterfaceHost {
    public TileDualInterface() {
        super();
    }

    private final DualityFluidInterface fluidDuality = new DualityFluidInterface(this.getProxy(), this);
    private final DualityInterface itemDuality = new DualityInterface(this.getProxy(), this);

    // Indicates that this interface has no specific direction set
    private boolean omniDirectional = true;

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        this.itemDuality.notifyNeighbors();
        this.fluidDuality.notifyNeighbors();
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        this.itemDuality.notifyNeighbors();
        this.fluidDuality.notifyNeighbors();
    }

    public void setSide(final EnumFacing facing) {
        if (Platform.isClient()) {
            return;
        }

        EnumFacing newForward = facing;

        if (!this.omniDirectional && this.getForward() == facing.getOpposite()) {
            newForward = facing;
        } else if (!this.omniDirectional
                && (this.getForward() == facing || this.getForward() == facing.getOpposite())) {
            this.omniDirectional = true;
        } else if (this.omniDirectional) {
            newForward = facing.getOpposite();
            this.omniDirectional = false;
        } else {
            newForward = Platform.rotateAround(this.getForward(), facing);
        }

        if (this.omniDirectional) {
            this.setOrientation(EnumFacing.NORTH, EnumFacing.UP);
        } else {
            EnumFacing newUp = EnumFacing.UP;
            if (newForward == EnumFacing.UP || newForward == EnumFacing.DOWN) {
                newUp = EnumFacing.NORTH;
            }
            this.setOrientation(newForward, newUp);
        }

        this.configureNodeSides();
        this.markForUpdate();
        this.saveChanges();
    }

    private void configureNodeSides() {
        if (this.omniDirectional) {
            this.getProxy().setValidSides(EnumSet.allOf(EnumFacing.class));
        } else {
            this.getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(this.getForward())));
        }
    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {
        this.itemDuality.addDrops(drops);
    }

    @Override
    public void gridChanged() {
        this.itemDuality.gridChanged();
        this.fluidDuality.gridChanged();
    }

    @Override
    public void onReady() {
        this.configureNodeSides();

        super.onReady();
        this.itemDuality.initialize();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("omniDirectional", this.omniDirectional);
        NBTTagCompound itemDuality = new NBTTagCompound();
        NBTTagCompound fluidDuality = new NBTTagCompound();
        this.itemDuality.writeToNBT(itemDuality);
        this.fluidDuality.writeToNBT(fluidDuality);
        data.setTag("itemDuality", itemDuality);
        data.setTag("fluidDuality", fluidDuality);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.omniDirectional = data.getBoolean("omniDirectional");
        this.itemDuality.readFromNBT(data.getCompoundTag("itemDuality"));
        this.fluidDuality.readFromNBT(data.getCompoundTag("fluidDuality"));
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean c = super.readFromStream(data);
        boolean oldOmniDirectional = this.omniDirectional;
        this.omniDirectional = data.readBoolean();
        return oldOmniDirectional != this.omniDirectional || c;
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeBoolean(this.omniDirectional);
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.SMART;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this.getTileEntity());
    }

    @Override
    public boolean canInsert(final ItemStack stack) {
        return this.itemDuality.canInsert(stack);
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return this.itemDuality.getInventoryByName(name);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        TickingRequest item = this.itemDuality.getTickingRequest(node);
        TickingRequest fluid = this.fluidDuality.getTickingRequest(node);
        return new TickingRequest(Math.min(item.minTickRate, fluid.minTickRate),
                Math.max(item.maxTickRate, fluid.maxTickRate), item.isSleeping == fluid.isSleeping && item.isSleeping,
                true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        TickRateModulation item = this.itemDuality.tickingRequest(node, ticksSinceLastCall);
        TickRateModulation fluid = this.fluidDuality.tickingRequest(node, ticksSinceLastCall);
        if (item == fluid) {
            return item;
        }
        if (item == TickRateModulation.SLOWER || fluid == TickRateModulation.SLOWER) {
            return TickRateModulation.SLOWER;
        }
        return TickRateModulation.URGENT;
    }

    @Override
    public IItemHandler getInternalInventory() {
        return this.itemDuality.getInternalInventory();
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc,
                                  final ItemStack removed, final ItemStack added) {
        this.itemDuality.onChangeInventory(inv, slot, mc, removed, added);
    }

    @Override
    public DualityInterface getInterfaceDuality() {
        return this.itemDuality;
    }

    @Override
    public DualityFluidInterface getDualityFluidInterface() {
        return this.fluidDuality;
    }

    @Override
    public EnumSet<EnumFacing> getTargets() {
        if (this.omniDirectional) {
            return EnumSet.allOf(EnumFacing.class);
        }
        return EnumSet.of(this.getForward());
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.itemDuality.getConfigManager();
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        return this.itemDuality.pushPattern(patternDetails, table);
    }

    @Override
    public boolean isBusy() {
        return this.itemDuality.isBusy();
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        this.itemDuality.provideCrafting(craftingTracker);
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.itemDuality.getInstalledUpgrades(u);
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.itemDuality.getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(final ICraftingLink link, final IAEItemStack items, final Actionable mode) {
        return this.itemDuality.injectCraftedItems(link, items, mode);
    }

    @Override
    public void jobStateChange(final ICraftingLink link) {
        this.itemDuality.jobStateChange(link);
    }

    @Override
    public int getPriority() {
        return this.itemDuality.getPriority();
    }

    @Override
    public void setPriority(final int newValue) {
        this.itemDuality.setPriority(newValue);
    }

    /**
     * @return True if this interface is omni-directional.
     */
    public boolean isOmniDirectional() {
        return this.omniDirectional;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return this.itemDuality.hasCapability(capability, facing)
                || this.fluidDuality.hasCapability(capability, facing) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        T result = this.itemDuality.getCapability(capability, facing);
        if (result != null) {
            return result;
        }
        result = this.fluidDuality.getCapability(capability, facing);
        if (result != null) {
            return result;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return new ItemStack(FcBlocks.DUAL_INTERFACE);
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.values()[Ae2GuiUtils.DUAL_ITEM_INTERFACE.ordinal()];
    }
}
