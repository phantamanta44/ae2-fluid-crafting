package xyz.phanta.ae2fc.tile;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.*;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.sync.GuiBridge;
import appeng.fluids.util.AEFluidStack;
import appeng.helpers.IPriorityHost;
import appeng.helpers.Reflected;
import appeng.me.GridAccessException;
import appeng.me.cache.CraftingGridCache;
import appeng.me.helpers.MachineSource;
import appeng.me.storage.MEInventoryHandler;
import appeng.tile.grid.AENetworkTile;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import xyz.phanta.ae2fc.init.FcBlocks;
import xyz.phanta.ae2fc.item.ItemFluidDrop;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileFluidDiscretizer extends AENetworkTile implements IGridTickable, ICellContainer, IPriorityHost {

    private final FluidDiscretizingInventory inv = new FluidDiscretizingInventory();
    private final IActionSource ownActionSource = new MachineSource(this);
    private int priority = 0;
    private boolean prevActiveState = false;

    @Reflected
    public TileFluidDiscretizer() {
        getProxy().setIdlePowerUsage(3D);
        getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return new ItemStack(FcBlocks.FLUID_DISCRETIZER);
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int newValue) {
        this.priority = newValue;
        inv.invHandler.setPriority(newValue);
        saveChanges();
        try {
            getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate()); // because of prio update
        } catch (final GridAccessException e) {
            // NO-OP
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
        if (getProxy().isActive() && channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
            return Collections.singletonList(inv.invHandler);
        }
        return Collections.emptyList();
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        // we sleep if no useful ticking occurs, so this should be fine
        return new TickingRequest(1, 1, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        return inv.tick() ? TickRateModulation.URGENT : TickRateModulation.SLEEP;
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> cellInventory) {
        world.markChunkDirty(pos, this); // optimization, i guess?
    }

    @Override
    public void gridChanged() {
        IMEMonitor<IAEFluidStack> fluidGrid = getFluidGrid();
        if (fluidGrid != null) {
            fluidGrid.addListener(inv, fluidGrid);
        }
    }

    @MENetworkEventSubscribe
    public void onPowerUpdate(MENetworkPowerStatusChange event) {
        updateState();
    }

    @MENetworkEventSubscribe
    public void onChannelUpdate(MENetworkChannelsChanged event) {
        updateState();
    }

    private void updateState() {
        boolean isActive = getProxy().isActive();
        if (isActive != prevActiveState) {
            prevActiveState = isActive;
            try {
                getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
            } catch (GridAccessException e) {
                // NO-OP
            }
        }
    }

    @Nullable
    @Override
    public GuiBridge getGuiBridge() {
        return null;
    }

    @Override
    public void blinkCell(int slot) {
        // NO-OP
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("Priority", priority);
        NBTTagCompound invTag = new NBTTagCompound();
        inv.writeToNbt(invTag);
        data.setTag("InvHandler", invTag);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        priority = data.getInteger("Priority");
        inv.invHandler.setPriority(priority);
        inv.readFromNbt(data.getCompoundTag("InvHandler"));
    }

    @Nullable
    private IEnergyGrid getEnergyGrid() {
        try {
            return getProxy().getGrid().getCache(IEnergyGrid.class);
        } catch (GridAccessException e) {
            return null;
        }
    }

    @Nullable
    private IMEMonitor<IAEFluidStack> getFluidGrid() {
        try {
            return getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class)
                    .getInventory(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
        } catch (GridAccessException e) {
            return null;
        }
    }

    private class FluidDiscretizingInventory implements IMEInventory<IAEItemStack>, IMEMonitorHandlerReceiver<IAEFluidStack> {

        private final MEInventoryHandler<IAEItemStack> invHandler = new MEInventoryHandler<>(this, getChannel());
        private final List<IAEFluidStack> checkCrafting = new ArrayList<>();
        private boolean inCraftingCheck = false;
        @Nullable
        private List<IAEItemStack> itemCache = null;

        // if fluid is injected into the network, it gets converted to fluid drops. however, since this is treated as
        // external insertion into an external inv rather than direct insertion into the network, the autocrafting
        // to recognize it as satisfication of an autocrafting intermediate/result. the way autocrafting works is that
        // it exposes the crafting grid as a storage provider with Integer.MAX_VALUE priority. therefore, we manually
        // satiate the autocrafting system by getting the crafting inventory and attempting to inject the inserted fluid
        // into it as drops, returning the fluid to the fluid grid if it fails
        boolean tick() {
            if (checkCrafting.isEmpty()) {
                return false;
            }
            CraftingGridCache craftingGrid;
            try {
                craftingGrid = getProxy().getGrid().getCache(ICraftingGrid.class);
            } catch (GridAccessException e) {
                return false;
            }
            IMEMonitor<IAEFluidStack> fluidGrid = getFluidGrid();
            if (fluidGrid == null) {
                return false;
            }
            inCraftingCheck = true;
            try {
                for (IAEFluidStack stack : checkCrafting) {
                    IAEFluidStack extracted = fluidGrid.extractItems(stack, Actionable.MODULATE, ownActionSource);
                    if (extracted != null) {
                        IAEItemStack remaining = craftingGrid.injectItems(
                                ItemFluidDrop.newAeStack(extracted), Actionable.MODULATE, ownActionSource);
                        if (remaining != null) {
                            fluidGrid.injectItems(
                                    ItemFluidDrop.getAeFluidStack(remaining), Actionable.MODULATE, ownActionSource);
                        }
                    }
                }
                checkCrafting.clear();
            } finally {
                inCraftingCheck = false;
            }
            saveChanges();
            return true;
        }

        @SuppressWarnings("DuplicatedCode")
        @Nullable
        @Override
        public IAEItemStack extractItems(IAEItemStack request, Actionable mode, IActionSource src) {
            IAEFluidStack fluidStack = ItemFluidDrop.getAeFluidStack(request);
            if (fluidStack == null) {
                return null;
            }
            IMEMonitor<IAEFluidStack> fluidGrid = getFluidGrid();
            if (fluidGrid == null) {
                return null;
            }
            IEnergyGrid energyGrid = getEnergyGrid();
            if (energyGrid == null) {
                return null;
            }
            return ItemFluidDrop.newAeStack(Platform.poweredExtraction(energyGrid, fluidGrid, fluidStack, ownActionSource, mode));
        }

        @SuppressWarnings("DuplicatedCode")
        @Nullable
        @Override
        public IAEItemStack injectItems(IAEItemStack input, Actionable type, IActionSource src) {
            IAEFluidStack fluidStack = ItemFluidDrop.getAeFluidStack(input);
            if (fluidStack == null) {
                return input;
            }
            IMEMonitor<IAEFluidStack> fluidGrid = getFluidGrid();
            if (fluidGrid == null) {
                return input;
            }
            IEnergyGrid energyGrid = getEnergyGrid();
            if (energyGrid == null) {
                return input;
            }
            return ItemFluidDrop.newAeStack(Platform.poweredInsert(energyGrid, fluidGrid, fluidStack, ownActionSource, type));
        }

        @Override
        public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
            if (itemCache == null) {
                itemCache = new ArrayList<>();
                IMEMonitor<IAEFluidStack> fluidGrid = getFluidGrid();
                if (fluidGrid != null) {
                    for (IAEFluidStack fluid : fluidGrid.getStorageList()) {
                        IAEItemStack stack = ItemFluidDrop.newAeStack(fluid);
                        if (stack != null) {
                            itemCache.add(stack);
                        }
                    }
                }
            }
            for (IAEItemStack stack : itemCache) {
                out.addStorage(stack);
            }
            return out;
        }

        @Override
        public boolean isValid(Object verificationToken) {
            IMEMonitor<IAEFluidStack> fluidGrid = getFluidGrid();
            return fluidGrid != null && fluidGrid == verificationToken;
        }

        @Override
        public void postChange(IBaseMonitor<IAEFluidStack> monitor, Iterable<IAEFluidStack> change, IActionSource actionSource) {
            itemCache = null;
            try {
                List<IAEItemStack> mappedChanges = new ArrayList<>();
                for (IAEFluidStack fluidStack : change) {
                    IAEItemStack itemStack = ItemFluidDrop.newAeStack(fluidStack);
                    if (itemStack != null) {
                        mappedChanges.add(itemStack);
                    }
                    if (!inCraftingCheck) {
                        checkCrafting.add(fluidStack);
                    }
                }
                getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class)
                        .postAlterationOfStoredItems(getChannel(), mappedChanges, ownActionSource);
                if (!inCraftingCheck) {
                    saveChanges(); // save, since we updated checkCrafting
                }
                getProxy().getTick().alertDevice(getProxy().getNode()); // immediately tick to flush the crafting check cache
            } catch (GridAccessException e) {
                // NO-OP
            }
        }

        @Override
        public void onListUpdate() {
            // NO-OP
        }

        @Override
        public IStorageChannel<IAEItemStack> getChannel() {
            return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
        }

        void writeToNbt(NBTTagCompound tag) {
            NBTTagList toCheck = tag.getTagList("CheckCrafting", Constants.NBT.TAG_COMPOUND);
            for (NBTBase stackTag : toCheck) {
                checkCrafting.add(AEFluidStack.fromNBT((NBTTagCompound)stackTag));
            }
        }

        void readFromNbt(NBTTagCompound tag) {
            NBTTagList toCheck = new NBTTagList();
            for (IAEFluidStack stack : checkCrafting) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stack.writeToNBT(stackTag);
                toCheck.appendTag(stackTag);
            }
            tag.setTag("CheckCrafting", toCheck);
        }

    }

}
