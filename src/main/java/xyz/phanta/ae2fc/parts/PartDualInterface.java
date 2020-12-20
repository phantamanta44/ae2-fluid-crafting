package xyz.phanta.ae2fc.parts;

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
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.fluids.helper.DualityFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.Reflected;
import appeng.items.parts.PartModels;
import appeng.parts.PartBasicState;
import appeng.parts.PartModel;
import appeng.util.Platform;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.IInventoryDestination;
import appeng.util.inv.InvOperation;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.inventory.GuiType;
import xyz.phanta.ae2fc.inventory.InventoryHandler;
import xyz.phanta.ae2fc.tile.base.FcPriorityHost;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;

public class PartDualInterface extends PartBasicState
        implements IGridTickable, IInventoryDestination, IInterfaceHost, IAEAppEngInventory, FcPriorityHost, IFluidInterfaceHost {

    @PartModels
    public static ResourceLocation[] MODELS = new ResourceLocation[] {
            new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_base"),
            new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_on"),
            new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_off"),
            new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_has_channel")
    };

    public static final PartModel MODELS_OFF = new PartModel(MODELS[0], MODELS[2]);
    public static final PartModel MODELS_ON = new PartModel(MODELS[0], MODELS[1]);
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS[0], MODELS[3]);

    private final DualityFluidInterface fluidDuality = new DualityFluidInterface(this.getProxy(), this);
    private final DualityInterface itemDuality = new DualityInterface(this.getProxy(), this);

    @Reflected
    public PartDualInterface(final ItemStack is) {
        super(is);
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        this.itemDuality.gridChanged();
        this.fluidDuality.gridChanged();
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        this.itemDuality.gridChanged();
        this.fluidDuality.gridChanged();
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.itemDuality.getInstalledUpgrades(u);
    }

    @Override
    public void gridChanged() {
        this.itemDuality.gridChanged();
        this.fluidDuality.gridChanged();
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.itemDuality.readFromNBT(data.getCompoundTag("itemDuality"));
        this.fluidDuality.readFromNBT(data.getCompoundTag("fluidDuality"));
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagCompound itemDuality = new NBTTagCompound();
        NBTTagCompound fluidDuality = new NBTTagCompound();
        this.itemDuality.writeToNBT(itemDuality);
        this.fluidDuality.writeToNBT(fluidDuality);
        data.setTag("itemDuality", itemDuality);
        data.setTag("fluidDuality", fluidDuality);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.itemDuality.initialize();
    }

    @Override
    public void getDrops(final List<ItemStack> drops, final boolean wrenched) {
        this.itemDuality.addDrops(drops);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.itemDuality.getConfigManager();
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return this.itemDuality.getInventoryByName(name);
    }

    @Override
    public boolean onPartActivate(final EntityPlayer p, final EnumHand hand, final Vec3d pos) {
        if (Platform.isServer()) {
            TileEntity tile = getTileEntity();
            InventoryHandler.openGui(p, tile.getWorld(), tile.getPos(), getSide().getFacing(), GuiType.DUAL_ITEM_INTERFACE);
        }
        return true;
    }

    @Override
    public boolean canInsert(final ItemStack stack) {
        return this.itemDuality.canInsert(stack);
    }

//    @Override
//    public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel )
//    {
//        return this.item_duality.getInventory( channel );
//    }

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
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc,
                                  final ItemStack removedStack, final ItemStack newStack) {
        this.itemDuality.onChangeInventory(inv, slot, mc, removedStack, newStack);
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
        return EnumSet.of(this.getSide().getFacing());
    }

    @Override
    public TileEntity getTileEntity() {
        return super.getHost().getTile();
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

    @Override
    public boolean hasCapability(Capability<?> capabilityClass) {
        EnumFacing facing = this.getSide().getFacing();
        return this.itemDuality.hasCapability(capabilityClass, facing)
                || this.fluidDuality.hasCapability(capabilityClass, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capabilityClass) {
        EnumFacing facing = this.getSide().getFacing();
        T result = this.itemDuality.getCapability(capabilityClass, facing);
        if (result != null) {
            return result;
        }
        result = this.fluidDuality.getCapability(capabilityClass, facing);
        return result;
    }

    @Override
    public GuiType getGuiType() {
        return GuiType.DUAL_ITEM_INTERFACE;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return new ItemStack(FcItems.PART_DUAL_INTERFACE, 1);
    }

    @Nonnull
    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
}
