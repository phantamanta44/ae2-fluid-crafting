package xyz.phanta.ae2fc.tile;

import appeng.api.AEApi;
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
import xyz.phanta.ae2fc.block.BlockDualInterface;
import xyz.phanta.ae2fc.init.FcBlocks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class TileDualInterface extends AENetworkInvTile implements IGridTickable, IInventoryDestination, IInterfaceHost, IPriorityHost, IFluidInterfaceHost {
    public TileDualInterface() {
        super();
//        //modify "private final DualityInterface duality" to "DualityDInterface"
//        try {
//            Field field = ReflectionHelper.findField(TileInterface.class, "duality");
//            field.setAccessible(true);
//            Field modifiersField = ReflectionHelper.findField(Field.class, "modifiers");
//            modifiersField.setAccessible(true);
//            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//            field.set(this, new DualityDInterface(this.getProxy(), this));
//        } catch (IllegalAccessException e){
//            e.printStackTrace();
//        }
    }
    private final DualityFluidInterface fluid_duality = new DualityFluidInterface(this.getProxy(), this);
    private final DualityInterface item_duality = new DualityInterface(this.getProxy(), this);

    // Indicates that this interface has no specific direction set
    private boolean omniDirectional = true;

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        this.item_duality.notifyNeighbors();
        this.fluid_duality.gridChanged();
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        this.item_duality.notifyNeighbors();
        this.fluid_duality.notifyNeighbors();
    }

    public void setSide(final EnumFacing facing) {
        if (Platform.isClient()) {
            return;
        }

        EnumFacing newForward = facing;

        if (!this.omniDirectional && this.getForward() == facing.getOpposite()) {
            newForward = facing;
        } else if (!this.omniDirectional && (this.getForward() == facing || this.getForward() == facing.getOpposite())) {
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
        this.item_duality.addDrops(drops);
    }

    @Override
    public void gridChanged() {
        this.item_duality.gridChanged();
        this.fluid_duality.gridChanged();
    }

    @Override
    public void onReady() {
        this.configureNodeSides();

        super.onReady();
        this.item_duality.initialize();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean( "omniDirectional", this.omniDirectional );
        NBTTagCompound itemDuality = new NBTTagCompound();
        NBTTagCompound fluidDuality = new NBTTagCompound();
        this.item_duality.writeToNBT(itemDuality);
        this.fluid_duality.writeToNBT(fluidDuality);
        data.setTag("itemDuality", itemDuality);
        data.setTag("fluidDuality", fluidDuality);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.omniDirectional = data.getBoolean( "omniDirectional" );
        this.item_duality.readFromNBT(data.getCompoundTag("itemDuality"));
        this.fluid_duality.readFromNBT(data.getCompoundTag("fluidDuality"));
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
        return this.item_duality.canInsert(stack);
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return this.item_duality.getInventoryByName(name);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        TickingRequest item = this.item_duality.getTickingRequest(node);
        TickingRequest fluid = this.fluid_duality.getTickingRequest(node);
        return new TickingRequest(Math.min(item.minTickRate, fluid.minTickRate), Math.max(item.maxTickRate, fluid.maxTickRate), item.isSleeping == fluid.isSleeping && item.isSleeping, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        TickRateModulation item = this.item_duality.tickingRequest(node, ticksSinceLastCall);
        TickRateModulation fluid = this.fluid_duality.tickingRequest(node, ticksSinceLastCall);
        if (item == fluid)
            return item;
        if (item == TickRateModulation.SLOWER || fluid == TickRateModulation.SLOWER)
            return TickRateModulation.SLOWER;
        return TickRateModulation.URGENT;
    }

    @Override
    public IItemHandler getInternalInventory() {
        return this.item_duality.getInternalInventory();
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        this.item_duality.onChangeInventory(inv, slot, mc, removed, added);
    }

    @Override
    public DualityInterface getInterfaceDuality() {
        return this.item_duality;
    }

    @Override
    public DualityFluidInterface getDualityFluidInterface() {
        return this.fluid_duality;
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
        return this.item_duality.getConfigManager();
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        return this.item_duality.pushPattern(patternDetails, table);
    }

    @Override
    public boolean isBusy() {
        return this.item_duality.isBusy();
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        this.item_duality.provideCrafting(craftingTracker);
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.item_duality.getInstalledUpgrades(u);
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.item_duality.getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(final ICraftingLink link, final IAEItemStack items, final Actionable mode) {
        return this.item_duality.injectCraftedItems(link, items, mode);
    }

    @Override
    public void jobStateChange(final ICraftingLink link) {
        this.item_duality.jobStateChange(link);
    }

    @Override
    public int getPriority() {
        return this.item_duality.getPriority();
    }

    @Override
    public void setPriority(final int newValue) {
        this.item_duality.setPriority(newValue);
    }

    /**
     * @return True if this interface is omni-directional.
     */
    public boolean isOmniDirectional() {
        return this.omniDirectional;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return this.item_duality.hasCapability(capability, facing) || this.fluid_duality.hasCapability(capability, facing) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        T result = this.item_duality.getCapability(capability, facing);
        if (result != null) {
            return result;
        }
        result = this.fluid_duality.getCapability(capability, facing);
        if (result != null) {
            return result;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public ItemStack getItemStackRepresentation() {
//        return AEApi.instance().definitions().blocks().iface().maybeStack(1).orElse(ItemStack.EMPTY);
        return new ItemStack(FcBlocks.DUAL_INTERFACE);
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.values()[4];
    }
}
