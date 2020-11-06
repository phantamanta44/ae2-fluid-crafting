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
import appeng.core.sync.GuiBridge;
import appeng.fluids.helper.DualityFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.IPriorityHost;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.init.FcItems;

import java.util.EnumSet;
import java.util.List;

public class PartDualInterface extends PartBasicState implements IGridTickable, IInventoryDestination, IInterfaceHost, IAEAppEngInventory, IPriorityHost, IFluidInterfaceHost {
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_base");
    public static final ResourceLocation MODEL_ON = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_on");
    public static final ResourceLocation MODEL_OFF = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_off");
    public static final ResourceLocation MODEL_HAS_CHANNEL = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_has_channel");
    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF);

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON);

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_HAS_CHANNEL);

    private final DualityFluidInterface fluid_duality = new DualityFluidInterface(this.getProxy(), this);
    private final DualityInterface item_duality = new DualityInterface(this.getProxy(), this);

    @Reflected
    public PartDualInterface(final ItemStack is) {
        super(is);
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        this.item_duality.gridChanged();
        this.fluid_duality.gridChanged();
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        this.item_duality.gridChanged();
        this.fluid_duality.gridChanged();
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.item_duality.getInstalledUpgrades(u);
    }

    @Override
    public void gridChanged() {
        this.item_duality.gridChanged();
        this.fluid_duality.gridChanged();
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.item_duality.readFromNBT(data.getCompoundTag("itemDuality"));
        this.fluid_duality.readFromNBT(data.getCompoundTag("fluidDuality"));
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagCompound itemDuality = new NBTTagCompound();
        NBTTagCompound fluidDuality = new NBTTagCompound();
        this.item_duality.writeToNBT(itemDuality);
        this.fluid_duality.writeToNBT(fluidDuality);
        data.setTag("itemDuality", itemDuality);
        data.setTag("fluidDuality", fluidDuality);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.item_duality.initialize();
    }

    @Override
    public void getDrops(final List<ItemStack> drops, final boolean wrenched) {
        this.item_duality.addDrops(drops);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.item_duality.getConfigManager();
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return this.item_duality.getInventoryByName(name);
    }

    @Override
    public boolean onPartActivate(final EntityPlayer p, final EnumHand hand, final Vec3d pos) {
        if (p.isSneaking()) {
            return false;
        }
        TileEntity te = this.getTileEntity();
        if (te == null) return false;
        BlockPos blockPos = te.getPos();
        if (Platform.isServer()) {
            p.openGui(Ae2FluidCrafting.INSTANCE, 4 << 4 | this.getSide().ordinal(), te.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        return true;
    }

    @Override
    public boolean canInsert(final ItemStack stack) {
        return this.item_duality.canInsert(stack);
    }

//    @Override
//    public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel )
//    {
//        return this.item_duality.getInventory( channel );
//    }

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
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removedStack, final ItemStack newStack) {
        this.item_duality.onChangeInventory(inv, slot, mc, removedStack, newStack);
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
        return EnumSet.of(this.getSide().getFacing());
    }

    @Override
    public TileEntity getTileEntity() {
        return super.getHost().getTile();
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

    @Override
    public boolean hasCapability(Capability<?> capabilityClass) {
        EnumFacing facing = this.getSide().getFacing();
        return this.item_duality.hasCapability(capabilityClass, facing) || this.fluid_duality.hasCapability(capabilityClass, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capabilityClass) {
        EnumFacing facing = this.getSide().getFacing();
        T result = this.item_duality.getCapability(capabilityClass, facing);
        if (result != null) {
            return result;
        }
        result = this.fluid_duality.getCapability(capabilityClass, facing);
        return result;
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.values()[4];
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return new ItemStack(FcItems.PART_DUAL_INTERFACE, 1);
    }

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
