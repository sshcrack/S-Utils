package me.sshcrack.sutils.interactable.challenges.base.randomRecipes;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class RecipeInfo {
    private final String[] shape;
    private final Map<Character, ItemStack> ingredients;

    public RecipeInfo(String[] shape, Map<Character, ItemStack> ingredients) {
        this.shape = shape;
        this.ingredients = ingredients;
    }

    public Map<Character, ItemStack> getIngredients() {
        return ingredients;
    }

    public String[] getShape() {
        return shape;
    }
}
