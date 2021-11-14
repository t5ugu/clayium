package mods.clayium.block.tile;

import mods.clayium.util.crafting.Recipes;
import net.minecraft.item.ItemStack;

public class TileCAInjector extends TileCAMachines {
    private Recipes recipe;
    protected double caFactorExponent = 1.0D;


    public void refreshRecipe() {
        Recipes recipe = Recipes.GetRecipes(this.recipeId);
        if (recipe != null) this.recipe = recipe;
    }

    public void initParams() {
        this.containerItemStacks = new ItemStack[6];
        this.clayEnergySlot = 5;
        this.listSlotsInsert.add(new int[] {0});
        this.listSlotsInsert.add(new int[] {1});
        this.listSlotsInsert.add(new int[] {0, 1});
        this.listSlotsInsert.add(new int[] {5});
        this.listSlotsExtract.add(new int[] {2});
        this.insertRoutes = new int[] {-1, 2, -1, 3, -1, -1};
        this.maxAutoExtract = new int[] {-1, -1, -1, 1};
        this.extractRoutes = new int[] {0, -1, -1, -1, -1, -1};
        this.maxAutoInsert = new int[] {-1};
        this.slotsDrop = new int[] {0, 1, 2, 5};
        this.autoInsert = true;
        this.autoExtract = true;
    }

    public void initParamsByTier(int tier) {
        super.initParamsByTier(tier);
        switch (tier) {
            case 9:
                this.caFactorExponent = 0.2D;
                break;
            case 10:
                this.caFactorExponent = 0.9D;
                break;
            case 11:
                this.caFactorExponent = 3.0D;
                break;
        }

    }


    protected boolean canCraft(ItemStack[] materials) {
        int method = 0;
        if (materials == null || this.recipe == null) {
            return false;
        }
        ItemStack[] itemstacks = this.recipe.getResult(materials, method, this.baseTier);
        if (itemstacks == null) return false;
        if (this.containerItemStacks[2] == null) return true;
        if (!this.containerItemStacks[2].isItemEqual(itemstacks[0])) return false;
        int result = (this.containerItemStacks[2]).stackSize + (itemstacks[0]).stackSize;
        return (result <= getInventoryStackLimit() && result <= this.containerItemStacks[2].getMaxStackSize());
    }

    protected int[] getCraftPermutation(ItemStack[] materials) {
        if (canCraft(materials)) return new int[] {0, 1};
        if (canCraft(new ItemStack[] {materials[1], materials[0]})) return new int[] {1, 0};
        if (canCraft(new ItemStack[] {materials[0]})) return new int[] {0};
        if (canCraft(new ItemStack[] {materials[1]})) return new int[] {1};
        return null;
    }


    public boolean canProceedCraft() {
        if (this.containerItemStacks[3] != null || this.containerItemStacks[4] != null) {
            ItemStack[] arrayOfItemStack = {this.containerItemStacks[3], this.containerItemStacks[4]};
            if (getCraftPermutation(arrayOfItemStack) != null) {
                return true;
            }
            return false;
        }
        ItemStack[] itemstacks = {this.containerItemStacks[0], this.containerItemStacks[1]};
        if (getCraftPermutation(itemstacks) != null) {
            return true;
        }
        return false;
    }


    public double getCraftTimeMultiplier() {
        return Math.pow(getResonance(), -this.caFactorExponent);
    }

    public void proceedCraft() {
        int method = 0;


        if (this.containerItemStacks[3] == null && this.containerItemStacks[4] == null) {
            ItemStack[] mats = {this.containerItemStacks[0], this.containerItemStacks[1]};
            int[] perm = getCraftPermutation(mats);
            if (perm == null) {
                throw new RuntimeException("Invalid recipe reference : The permutation variable is null!");
            }
            ItemStack[] itemstacks = new ItemStack[perm.length];
            for (int i = 0; i < perm.length; i++) {
                itemstacks[i] = mats[perm[i]];
            }
            this.machineConsumingEnergy = (long) ((float) this.recipe.getEnergy(itemstacks, method, this.baseTier) * this.multConsumingEnergy);
        }
        if (consumeClayEnergy(this.machineConsumingEnergy)) {
            if (this.containerItemStacks[3] == null && this.containerItemStacks[4] == null) {
                ItemStack[] mats = {this.containerItemStacks[0], this.containerItemStacks[1]};
                int[] perm = getCraftPermutation(mats);
                if (perm == null) {
                    throw new RuntimeException("Invalid recipe reference : The permutation variable is null!");
                }
                ItemStack[] itemstacks = new ItemStack[perm.length];
                for (int i = 0; i < perm.length; i++) {
                    itemstacks[i] = mats[perm[i]];
                }
                this.machineTimeToCraft = (long) (this.recipe.getTime(itemstacks, method, this.baseTier) * getCraftTimeMultiplier() * this.multCraftTime);
                int[] consumedStackSize = this.recipe.getConsumedStackSize(itemstacks, method, this.baseTier);
                for (int j = 0; j < perm.length; j++) {

                    this.containerItemStacks[j + 3] = this.containerItemStacks[perm[j]].splitStack(consumedStackSize[j]);
                    if ((this.containerItemStacks[perm[j]]).stackSize <= 0) this.containerItemStacks[perm[j]] = null;
                }
            }
            this.machineCraftTime++;
            this.isDoingWork = true;
            if (this.machineCraftTime >= this.machineTimeToCraft) {
                ItemStack[] mats = {this.containerItemStacks[3], this.containerItemStacks[4]};
                int[] perm = getCraftPermutation(mats);
                if (perm == null) {
                    throw new RuntimeException("Invalid recipe reference : The permutation variable is null!");
                }
                ItemStack[] itemstacks = new ItemStack[perm.length];
                for (int i = 0; i < perm.length; i++) {
                    itemstacks[i] = mats[perm[i]];
                }
                ItemStack result = this.recipe.getResult(itemstacks, method, this.baseTier)[0];
                int[] consumedStackSize = this.recipe.getConsumedStackSize(itemstacks, method, this.baseTier);
                this.machineCraftTime = 0L;
                this.machineConsumingEnergy = 0L;
                if (this.containerItemStacks[2] == null) {
                    this.containerItemStacks[2] = result.copy();
                } else if (this.containerItemStacks[2].getItem() == result.getItem()) {
                    (this.containerItemStacks[2]).stackSize += result.stackSize;
                }
                for (int j = 0; j < perm.length; j++) {
                    if (((this.containerItemStacks[j + 3]).stackSize -= consumedStackSize[j]) <= 0)
                        this.containerItemStacks[j + 3] = null;

                }
                if (this.externalControlState > 0) {
                    this.externalControlState--;
                    if (this.externalControlState == 0) this.externalControlState = -1;
                }
            }
        }
    }
}