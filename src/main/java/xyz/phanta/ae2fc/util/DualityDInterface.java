package xyz.phanta.ae2fc.util;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.settings.TickRates;
import appeng.fluids.util.AEFluidInventory;
import appeng.fluids.util.IAEFluidInventory;
import appeng.fluids.util.IAEFluidTank;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.util.InventoryAdaptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xyz.phanta.ae2fc.handler.CoreModHooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DualityDInterface extends DualityInterface implements IAEFluidInventory {
    private final AEFluidInventory tanks = new AEFluidInventory(this, 1, Fluid.BUCKET_VOLUME * 64);
    private static final String FLUID_NBT_KEY = "storage_fluid";

    public DualityDInterface(AENetworkProxy networkProxy, IInterfaceHost ih) {
        super(networkProxy, ih);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPrivateValue(String fieldName) {
        return (T) getPrivateValue(this, fieldName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPrivateValue(DualityInterface instance, String fieldName) {
        return (T) ReflectionHelper.getPrivateValue(DualityInterface.class, instance, fieldName);
    }

    public <E> void setPrivateValue(E value, String fieldName) {
        setPrivateValue(this, value, fieldName);
    }

    public <E> void setPrivateValue(DualityInterface instance, E value, String fieldName) {
        ReflectionHelper.setPrivateValue(DualityInterface.class, instance, value, fieldName);
    }

    private boolean hasItemsToSend() {
        List<ItemStack> waitingToSend = getPrivateValue("waitingToSend");
        return waitingToSend != null && !waitingToSend.isEmpty();
    }

    private boolean hasFluid() {
        IAEFluidStack aeFluidStack = this.tanks.getFluidInSlot(0);
        return aeFluidStack != null && aeFluidStack.getStackSize() != 0;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(TickRates.Interface.getMin(), TickRates.Interface.getMax(), !this.hasWorkToDo(), true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        AENetworkProxy gridProxy = getPrivateValue("gridProxy");
        IInterfaceHost iHost = getPrivateValue("iHost");
        if (!gridProxy.isActive()) {
            return TickRateModulation.SLEEP;
        }
        if (this.hasItemsToSend()) {
            this.pushItemsOut(iHost.getTargets());
//            try {
//                ReflectionHelper.findMethod(DualityInterface.class, "pushItemsOut", null).invoke(this, iHost.getTargets());
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
        }
        boolean pushedFluid = false;
        if (this.hasFluid()) {
            pushedFluid = this.pushFluidOut();
        }
        try {
            final boolean couldDoWork = (boolean) ReflectionHelper.findMethod(DualityInterface.class, "updateStorage", null).invoke(this);
            return this.hasWorkToDo() ? ( (couldDoWork || pushedFluid) ? TickRateModulation.URGENT : TickRateModulation.SLOWER ) : TickRateModulation.SLEEP;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return TickRateModulation.SLEEP;
    }

    private void pushItemsOut(final EnumSet<EnumFacing> possibleDirections) {
        if (!this.hasItemsToSend()) {
            return;
        }
        IInterfaceHost iHost = getPrivateValue("iHost");
        List<ItemStack> waitingToSend = getPrivateValue("waitingToSend");
        final TileEntity tile = iHost.getTileEntity();
        final World w = tile.getWorld();
        final Iterator<ItemStack> i = waitingToSend.iterator();
        while (i.hasNext()) {
            ItemStack whatToSend = i.next();

            for (final EnumFacing s : possibleDirections) {
                final TileEntity te = w.getTileEntity(tile.getPos().offset(s));
                if (te == null) {
                    continue;
                }
                final InventoryAdaptor ad = CoreModHooks.wrapInventory(te, s.getOpposite());
                if (ad != null) {
                    final ItemStack result = ad.addItems(whatToSend);
                    if (result.isEmpty()) {
                        whatToSend = ItemStack.EMPTY;
                    } else {
                        whatToSend.setCount(whatToSend.getCount() - (whatToSend.getCount() - result.getCount()));
                    }
                    if (whatToSend.isEmpty()) {
                        break;
                    }
                }
            }
            if (whatToSend.isEmpty()) {
                i.remove();
            }
        }
        if (waitingToSend.isEmpty()) {
            setPrivateValue(null, "waitingToSend");
        }
    }

    public boolean pushFluidOut() {
        boolean pushed = false;
        if (this.hasFluid()) {
            try {
                IAEFluidStack aeFluidStack = this.tanks.getFluidInSlot(0);
                Fluid aeFluidStackFluid = aeFluidStack.getFluid();
                AENetworkProxy gridProxy = getPrivateValue("gridProxy");
                IMEInventory<IAEFluidStack> dest = gridProxy.getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
                IActionSource src = getPrivateValue("interfaceRequestSource");
                IAEFluidStack left = dest.injectItems(aeFluidStack.copy(), Actionable.MODULATE, src);
                long leftSize = 0;
                if (left != null) {
                    leftSize = left.getStackSize();
                }
                if (aeFluidStack.getStackSize() != leftSize) {
                    pushed = true;
                }
                this.tanks.setFluidInSlot(0, left);
            } catch (GridAccessException e) {
                e.printStackTrace();
            }
        }
        return pushed;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        this.tanks.writeToNBT(data, FLUID_NBT_KEY);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.tanks.readFromNBT(data, FLUID_NBT_KEY);
    }

    private boolean hasWorkToDo() {
        if (this.hasItemsToSend() || this.hasFluid()) {
            return true;
        } else {
            IAEItemStack[] requireWork = getPrivateValue("requireWork");
            for (final IAEItemStack requiredWork : requireWork) {
                if (requiredWork != null) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public <T> T getCapability(Capability<T> capabilityClass, EnumFacing facing) {
        if (capabilityClass == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) this.tanks;
        } else {
            return super.getCapability(capabilityClass, facing);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capabilityClass, EnumFacing facing) {
        return super.hasCapability(capabilityClass, facing) || capabilityClass == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public void onFluidInventoryChanged(IAEFluidTank inv, int slot) {
        AENetworkProxy gridProxy = getPrivateValue("gridProxy");
        try {
            if (this.hasFluid()) {
                gridProxy.getTick().alertDevice(gridProxy.getNode());
            } else {
                gridProxy.getTick().sleepDevice(gridProxy.getNode());

            }
        } catch (GridAccessException e) {
            e.printStackTrace();
        }
    }

    public void setPlacer(EntityPlayer player) {
        AENetworkProxy gridProxy = getPrivateValue("gridProxy");
        if (gridProxy != null) {
            gridProxy.setOwner(player);
        }
    }
}
