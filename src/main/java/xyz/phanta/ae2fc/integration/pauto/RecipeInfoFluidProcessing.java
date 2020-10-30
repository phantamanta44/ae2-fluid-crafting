package xyz.phanta.ae2fc.integration.pauto;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.recipe.RecipeInfoProcessing;
import thelm.packagedauto.util.PatternHelper;
import xyz.phanta.ae2fc.util.DensePatternDetails;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RecipeInfoFluidProcessing extends RecipeInfoProcessing { // TODO remove me if not needed

    @Nullable
    private IAEItemStack[] inputs = null, outputs = null; // for serialization
    private final List<ItemStack> collapsedInputs = new ArrayList<>(), collapsedOutputs = new ArrayList<>(); // for use
    private final List<IPackagePattern> packagePatterns = new ArrayList<>();

    @Override
    public IRecipeType getRecipeType() {
        return RecipeTypeFluidProcessing.INSTANCE;
    }

    @Override
    public boolean isValid() {
        return inputs != null && inputs.length > 0;
    }

    @Override
    public List<ItemStack> getInputs() {
        return collapsedInputs;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return collapsedOutputs;
    }

    @Override
    public List<IPackagePattern> getPatterns() {
        return packagePatterns;
    }

    @Override
    public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, World world) {
        List<IAEItemStack> inputList = new ArrayList<>(), outputList = new ArrayList<>();
        mapRecipeItems(input, inputList, collapsedInputs);
        mapRecipeItems(output, outputList, collapsedOutputs);
        inputs = inputList.toArray(new IAEItemStack[0]);
        outputs = outputList.toArray(new IAEItemStack[0]);
        regeneratePackagePatterns();
    }

    private static void mapRecipeItems(List<ItemStack> src, List<IAEItemStack> dest, List<ItemStack> destCollapsed) {
        dest.clear();
        destCollapsed.clear();
        for (ItemStack stack : src) {
            if (!stack.isEmpty()) {
                IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
                if (aeStack != null) {
                    dest.add(aeStack);
                    destCollapsed.add(aeStack.createItemStack());
                }
            }
        }
    }

    private void regeneratePackagePatterns() {
        packagePatterns.clear();
        if (inputs != null) {
            for (int i = 0; i * 9 < inputs.length; i++) {
                packagePatterns.add(new PatternHelper(this, i));
            }
        }
    }

    @Override
    public Int2ObjectMap<ItemStack> getEncoderStacks() {
        Int2ObjectMap<ItemStack> stacks = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < collapsedInputs.size(); i++) {
            stacks.put(i, collapsedInputs.get(i));
        }
        for (int i = 0; i < collapsedOutputs.size(); i++) {
            stacks.put(81 + i, collapsedOutputs.get(i));
        }
        return stacks;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (inputs != null) {
            tag.setTag("Inputs", DensePatternDetails.writeStackArray(inputs));
        }
        if (outputs != null) {
            tag.setTag("Outputs", DensePatternDetails.writeStackArray(outputs));
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        collapsedInputs.clear();
        if (tag.hasKey("Inputs", Constants.NBT.TAG_LIST)) {
            inputs = DensePatternDetails.readStackArray(tag.getTagList("Inputs", Constants.NBT.TAG_COMPOUND), 81);
            for (IAEItemStack stack : inputs) {
                collapsedInputs.add(stack.createItemStack());
            }
        } else {
            inputs = null;
        }
        collapsedOutputs.clear();
        if (tag.hasKey("Outputs", Constants.NBT.TAG_LIST)) {
            outputs = DensePatternDetails.readStackArray(tag.getTagList("Outputs", Constants.NBT.TAG_COMPOUND), 9);
            for (IAEItemStack stack : outputs) {
                collapsedOutputs.add(stack.createItemStack());
            }
        } else {
            outputs = null;
        }
        regeneratePackagePatterns();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInfoFluidProcessing)) {
            return false;
        }
        IAEItemStack[] oInputs = ((RecipeInfoFluidProcessing)obj).inputs, oOutputs = ((RecipeInfoFluidProcessing)obj).outputs;
        if (oInputs == null || oOutputs == null) {
            return inputs == null || outputs == null;
        }
        if (inputs == null || outputs == null || inputs.length != oInputs.length || outputs.length != oOutputs.length) {
            return false;
        }
        for (int i = 0; i < inputs.length; i++) {
            // ae2's equals() on `IAEStack` is defined to ignore stack size, so we check is explicitly
            if (inputs[i].getStackSize() != oInputs[i].getStackSize() || !inputs[i].equals(oInputs[i])) {
                return false;
            }
        }
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i].getStackSize() != oOutputs[i].getStackSize() || !outputs[i].equals(oOutputs[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (inputs == null || outputs == null) {
            return 0;
        }
        int hash = (inputs.length << 16) + outputs.length;
        for (IAEItemStack stack : inputs) {
            hash = Integer.rotateLeft(hash + stack.hashCode(), 3);
            hash = Integer.rotateLeft(hash + Long.hashCode(stack.getStackSize()), 8);
        }
        for (IAEItemStack stack : outputs) {
            hash = Integer.rotateLeft(hash + stack.hashCode(), 4);
            hash = Integer.rotateLeft(hash + Long.hashCode(stack.getStackSize()), 9);
        }
        return hash;
    }

}
