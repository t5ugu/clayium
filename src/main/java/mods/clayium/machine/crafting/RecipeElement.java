package mods.clayium.machine.crafting;

import mods.clayium.util.UtilItemStack;
import mods.clayium.util.crafting.IItemPattern;
import mods.clayium.util.crafting.OreDictionaryStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;

public class RecipeElement {
    public static final RecipeElement FLAT = new RecipeElement(ItemStack.EMPTY, 0, 0, ItemStack.EMPTY, 0, 0);

    public RecipeElement(ItemStack materialIn, int method, int tier, ItemStack resultIn, long energy, long time) {
        this(Arrays.asList(materialIn), method, tier, Arrays.asList(resultIn), energy, time);
    }

    public RecipeElement(List<ItemStack> materialIn, int method, int tier, List<ItemStack> resultIn, long energy, long time) {
        condition = new RecipeCondition(materialIn, method, tier);
        result = new RecipeResult(resultIn, energy, time);
    }

    public RecipeCondition getCondition() {
        return condition;
    }

    public RecipeResult getResult() {
        return result;
    }

    private final RecipeCondition condition;
    private final RecipeResult result;

    public static class RecipeCondition {
        private final List<ItemStack> materials;
        private final int method;
        private final int tier;

        public List<ItemStack> getMaterials() {
            return materials;
        }

        public int getMethod() {
            return method;
        }

        public int getTier() {
            return tier;
        }

        public RecipeCondition(List<ItemStack> materials, int method, int tier) {
            this.materials = materials;
            this.method = method;
            this.tier = tier;
        }

        public boolean match(List<ItemStack> itemStacksIn, int methodIn, int tierIn) {
            if (method != methodIn || tier > tierIn || materials.size() > itemStacksIn.size()) return false;
            return match(itemStacksIn);
        }

        public boolean match(List<ItemStack> itemStacksIn) {
            for (int i = 0; i < materials.size(); i++)
                if (!inclusion(materials.get(i), itemStacksIn.get(i)))
                    return false;

            return true;
        }

        public static boolean inclusion(ItemStack from, ItemStack comes) {
            if (from.isEmpty()) return true;
            if (from.getCount() > comes.getCount()) return false;
            if (from.getHasSubtypes() && comes.getHasSubtypes()) return from.isItemEqual(comes);
            return from.getItem() == comes.getItem();
        }

        public int[] getStackSizes(ItemStack... items) {
            int[] sizes = new int[items.length];
            for (int i = 0; i < items.length && i < this.materials.size(); i++) {
                sizes[i] = getStackSize(this.materials.get(i), items[i]);
            }
            return sizes;
        }

        public boolean isCraftable(ItemStack itemstack, int tier) {
            if (this.tier > tier) {
                return false;
            }

            for (ItemStack stack : this.materials) {
                if (canBeCraftedODs(itemstack, stack, false)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class RecipeResult {
        private final List<ItemStack> results;
        private final long energy;
        private final long time;

        public List<ItemStack> getResults() {
            return results;
        }

        public long getEnergy() {
            return energy;
        }

        public long getTime() {
            return time;
        }

        public RecipeResult(List<ItemStack> results, long energy, long time) {
            this.results = results;
            this.energy = energy;
            this.time = time;
        }
    }

    public static int getStackSize(Object recipe, ItemStack item) {
        if (recipe instanceof IItemPattern) {
            if (item == null) {
                ItemStack[] items = ((IItemPattern) recipe).toItemStacks();
                if (items != null && items.length >= 1)
                    item = items[0];
            }
            return ((IItemPattern) recipe).getStackSize(item);
        }
        return getStackSize(recipe);
    }

    public static int getStackSize(Object item) {
        if (item instanceof ItemStack) return ((ItemStack) item).getCount();
//        if (item instanceof OreDictionaryStack) return ((OreDictionaryStack) item).stackSize;
        if (item instanceof String) return 1;

        return 0;
    }

    public static boolean canBeCrafted(ItemStack itemstack, ItemStack itemstack2, boolean sizeCheck) {
        if (itemstack2 == null) return true;
        if (itemstack == null) return false;
        return UtilItemStack.areItemEqual(itemstack2, itemstack)
                && (itemstack2.getItemDamage() == 32767 || itemstack.getItemDamage() == 32767
                || UtilItemStack.areDamageEqual(itemstack2, itemstack))
                && (!sizeCheck || itemstack2.getCount() <= itemstack.getCount());
    }

    public static boolean canBeCrafted(ItemStack itemstack, ItemStack itemstack2) {
        return canBeCrafted(itemstack, itemstack2, true);
    }

    public static boolean canBeCraftedOD(ItemStack itemstack, Object object, boolean sizeCheck) {
        if (object == null) return true;
        if (itemstack == null) return false;
        if (object instanceof String) {
            return UtilItemStack.hasOreName(itemstack, (String) object);
        }
        if (object instanceof OreDictionaryStack) {
            if (sizeCheck && ((OreDictionaryStack) object).stackSize > itemstack.getCount())
                return false;
            return UtilItemStack.hasOreName(itemstack, ((OreDictionaryStack) object).id);
        }
        if (object instanceof ItemStack)
            return canBeCrafted(itemstack, (ItemStack) object, sizeCheck);
        if (object instanceof IItemPattern)
            return ((IItemPattern) object).match(itemstack, sizeCheck);

        return false;
    }

    public static boolean canBeCraftedOD(ItemStack itemstack, Object object) {
        return canBeCraftedOD(itemstack, object, true);
    }

    public static boolean canBeCraftedODs(Object stackingred, Object recipeingred, boolean sizeCheck) {
        if (recipeingred == null) return true;
        if (stackingred == null) return false;
        if (stackingred instanceof ItemStack) {
            return canBeCraftedOD((ItemStack) stackingred, recipeingred, sizeCheck);
        }
        if (stackingred instanceof String || stackingred instanceof OreDictionaryStack) {
            int oreid = (stackingred instanceof OreDictionaryStack) ? ((OreDictionaryStack) stackingred).id : OreDictionary.getOreID((String) stackingred);
            int stackSize = (stackingred instanceof OreDictionaryStack) ? ((OreDictionaryStack) stackingred).stackSize : 1;
            for (ItemStack item : OreDictionary.getOres(String.valueOf(oreid))) {
                ItemStack item0 = item.copy();
                item0.setCount(stackSize);
                if (canBeCraftedOD(item0, recipeingred, sizeCheck)) {
                    return true;
                }
            }
        }
        if (stackingred instanceof IItemPattern) {
            return ((IItemPattern) stackingred).hasIntersection(convert(recipeingred), sizeCheck);
        }
        return false;
    }

    public static IItemPattern convert(Object ingred) {
    /* TODO still added ItemPattern
        if (ingred instanceof ItemStack) {
            return new ItemPatternItemStack((ItemStack) ingred);
        }
        if (ingred instanceof OreDictionaryStack) {
            return new ItemPatternOreDictionary(((OreDictionaryStack) ingred).id, ((OreDictionaryStack) ingred).stackSize);
        }
        if (ingred instanceof String) {
            return new ItemPatternOreDictionary((String) ingred, 1);
        }
    */
        if (ingred instanceof IItemPattern) {
            return (IItemPattern) ingred;
        }
        return null;
    }
}