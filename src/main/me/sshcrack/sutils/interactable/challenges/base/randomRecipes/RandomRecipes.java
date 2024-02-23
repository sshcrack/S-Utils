package me.sshcrack.sutils.interactable.challenges.base.randomRecipes;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;
import java.util.stream.Collectors;

public class RandomRecipes extends Challenge {
    private List<Recipe> randomRecipes = new ArrayList<>();
    private List<Recipe> vanillaRecipes = new ArrayList<>();

    public RandomRecipes() {
        super(
                "random_recipes",
                new Properties()
                        .item(new ItemStack(Material.CHORUS_FLOWER))
                        .alwaysEnabled()
        );
    }

    public <T> List<T> getElementsFromList(List<T> list, int skip, int size) {
        List<T> clone = new ArrayList<>(list);
        Iterator<T> iterator = clone.iterator();
        for (int i = 0; i < skip; i++) {
            if (!iterator.hasNext())
                break;

            iterator.next();
        }

        ArrayList<T> chunk = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!iterator.hasNext())
                break;

            chunk.add(iterator.next());
        }

        return chunk;
    }

    public List<Recipe> getRandomRecipes(List<Recipe> vanillaRecipe) {
        List<Recipe> cloneVanilla = new ArrayList<>(vanillaRecipe);
        List<ShapedRecipe> originalShaped = cloneVanilla
                .stream()
                .filter(e -> e instanceof ShapedRecipe)
                .map(e -> (ShapedRecipe) e)
                .collect(Collectors.toList());

        List<ShapelessRecipe> originalShapeless = cloneVanilla
                .stream()
                .filter(e -> e instanceof ShapelessRecipe)
                .map(e -> (ShapelessRecipe) e)
                .collect(Collectors.toList());

        List<List<ItemStack>> ingredientsShapeless = originalShapeless
                .stream()
                .map(ShapelessRecipe::getIngredientList)
                .collect(Collectors.toList());

        List<RecipeInfo> ingredientsShaped = originalShaped
                .stream()
                .map(e -> new RecipeInfo(e.getShape(), e.getIngredientMap()))
                .collect(Collectors.toList());

        List<ItemStack> results = originalShaped
                .stream()
                .map(Recipe::getResult)
                .collect(Collectors.toList());

        results.addAll(originalShapeless
                .stream()
                .map(Recipe::getResult)
                .collect(Collectors.toList()));

        Collections.shuffle(results);


        List<ItemStack> shapelessResults = getElementsFromList(results, 0, ingredientsShapeless.size());
        List<ItemStack> shapedResults = getElementsFromList(results, ingredientsShapeless.size(), ingredientsShaped.size());

        List<ShapelessRecipe> shapelessRandomized = new ArrayList<>();
        for (int i = 0; i < shapelessResults.size(); i++) {
            ItemStack result = shapelessResults.get(i);
            List<ItemStack> ingredients = ingredientsShapeless.get(i);

            ShapelessRecipe original = originalShapeless.get(i);

            NamespacedKey key = original.getKey();
            ShapelessRecipe recipe = new ShapelessRecipe(key, result);
            for (ItemStack ingredient : ingredients) {
                for (int x = 0; x < ingredient.getAmount(); x++) {
                    recipe.addIngredient(new ItemStack(ingredient.getType()));
                }
            }

            shapelessRandomized.add(recipe);
        }

        List<ShapedRecipe> shapedRandomized = new ArrayList<>();
        for (int i = 0; i < shapedResults.size(); i++) {
            ItemStack result = shapedResults.get(i);
            RecipeInfo ingredients = ingredientsShaped.get(i);

            ShapedRecipe original = originalShaped.get(i);

            NamespacedKey key = original.getKey();
            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape(ingredients.getShape());
            ingredients.getIngredients().forEach(recipe::setIngredient);

            shapedRandomized.add(recipe);
        }

        ArrayList<Recipe> result = new ArrayList<>();
        result.addAll(shapedRandomized);
        result.addAll(shapelessRandomized);

        Bukkit.getLogger().info(String.format("Randomized recipes: shaped: %s shapeless: %s total: %s", shapedRandomized.size(), shapelessRandomized.size(), result.size()));
        return result;
    }

    public List<String> recipeToString(List<Recipe> recipes) {
        if(recipes == null)
            return new ArrayList<>();

        List<String> out = new ArrayList<>();

        List<ShapedRecipe> shaped = recipes
                .stream()
                .filter(e -> e instanceof ShapedRecipe)
                .map(e -> (ShapedRecipe) e)
                .collect(Collectors.toList());

        List<ShapelessRecipe> shapeless = recipes
                .stream()
                .filter(e -> e instanceof ShapelessRecipe)
                .map(e -> (ShapelessRecipe) e)
                .collect(Collectors.toList());

        for (ShapedRecipe recipe : shaped) {
            StringBuilder str = new StringBuilder();
            str.append("SHAPED{[]}");

            String[] shape = recipe.getShape();
            String shapeJoined = Joiner.on("@").useForNull("NULL_ITEM").join(shape);

            byte[] resultByte = recipe.getResult().serializeAsBytes();
            str.append(Tools.bytesToHex(resultByte));
            str.append("{[]}");
            str.append(shapeJoined);
            str.append("{[]}");

            Map<Character, String> ingredientsStr = new HashMap<>();
            for(Map.Entry<Character, ItemStack> e : recipe.getIngredientMap().entrySet()) {
                String hex = Tools.bytesToHex(e.getValue().serializeAsBytes());
                ingredientsStr.put(e.getKey(), hex);
            }

            str.append(Joiner.on(",").useForNull("NULL_ITEM").withKeyValueSeparator("=").join(ingredientsStr));

            str.append("{[]}");
            str.append(recipe.getKey().asString());

            out.add(str.toString());
        }

        for (ShapelessRecipe recipe : shapeless) {
            StringBuilder str = new StringBuilder();
            str.append("SHAPELESS{[]}");

            byte[] resultByte = recipe.getResult().serializeAsBytes();
            String resultHex = Tools.bytesToHex(resultByte);
            str.append(resultHex);
            str.append("{[]}");

            List<ItemStack> ingredients = recipe.getIngredientList();
            boolean first = true;
            for (ItemStack ingredient : ingredients) {
                byte[] ingByte = ingredient.serializeAsBytes();
                String ingHex = Tools.bytesToHex(ingByte);

                if (first)
                    str.append("@");

                first = false;
                str.append(ingHex);
            }

            str.append("{[]}");
            str.append(recipe.getKey().asString());

            out.add(str.toString());
        }

        return out;
    }

    public List<Recipe> stringToRecipe(List<String> rawString) {
        List<Recipe> out = new ArrayList<>();

        List<String> shapeless = rawString
                .stream()
                .filter(e -> e.contains("SHAPELESS"))
                .collect(Collectors.toList());

        List<String> shaped = rawString
                .stream()
                .filter(e -> e.contains("SHAPED"))
                .collect(Collectors.toList());

        for (String s : shapeless) {
            String[] parts = s.split("\\{\\[]}");
            String resultStr = parts[1];
            String ingredientsStr = parts[2];
            String keyStr = parts[3];

            ItemStack result = ItemStack.deserializeBytes(Tools.hexToBytes(resultStr));
            List<ItemStack> ingredients = Arrays.stream(ingredientsStr
                            .split("@"))
                    .map(e -> ItemStack.deserializeBytes(Tools.hexToBytes(e)))
                    .collect(Collectors.toList());

            NamespacedKey key = NamespacedKey.fromString(keyStr);
            if (key == null) {
                Bukkit.getLogger().info(String.format("NamespacedKey %s is null. Disabling...", keyStr));
                disable();
                throw new Error("NamespacedKey is null");
            }

            ShapelessRecipe recipe = new ShapelessRecipe(key, result);
            for (ItemStack ingredient : ingredients) {
                recipe.addIngredient(ingredient);
            }

            out.add(recipe);
        }

        for (String s : shaped) {
            String[] parts = s.split("\\{\\[]}}");
            String resultStr = parts[1];
            String[] shape = parts[2].split("@");
            String mapStr = parts[3];
            String keyStr = parts[4];

            ItemStack result = ItemStack.deserializeBytes(Tools.hexToBytes(resultStr));
            NamespacedKey key = NamespacedKey.fromString(keyStr);

            Map<String, String> ingredients = Splitter.on(",").withKeyValueSeparator("=").split(mapStr);

            if (key == null) {
                Bukkit.getLogger().info(String.format("NamespacedKey %s is null. Disabling...", keyStr));
                disable();
                throw new Error("NamespacedKey is null");
            }

            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape(shape);

            ingredients.forEach((k, v) -> {
                char shapeKey = k.charAt(0);
                ItemStack item = ItemStack.deserializeBytes(Tools.hexToBytes(v));

                recipe.setIngredient(shapeKey, item);
            });

            out.add(recipe);
        }

        return out;
    }

    @Override
    public void onReset() {
        super.onReset();
        Bukkit.getLogger().info("Deleting random recipe list");
        FileConfiguration config = Main.plugin.getConfig();
        config.set("random_recipe_list", new ArrayList<String>());
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        this.vanillaRecipes = Lists.newArrayList(Bukkit.getServer().recipeIterator());
        FileConfiguration config = Main.plugin.getConfig();
        List<String> randRecipe = config.getStringList("random_recipe_list");

        Bukkit.getLogger().info(String.format("Loaded %s vanilla recipes", this.vanillaRecipes.size()));
        if(randRecipe.size() == 0) {
            this.randomRecipes = getRandomRecipes(this.vanillaRecipes);
            Bukkit.getLogger().info(String.format("Recipe size is now %s",this.randomRecipes.size()));
            super.loadConfig();
            return;
        }

        Bukkit.getLogger().info("Parsing recipes from string...");
        this.randomRecipes = stringToRecipe(randRecipe);

    }

    @Override
    public void saveConfig() {
        FileConfiguration config = Main.plugin.getConfig();
        config.set("random_recipe_list", recipeToString(randomRecipes));

        super.saveConfig();
    }

    @Override
    public void onRegister() {
        Bukkit.getLogger().info(String.format("recipe size is %s", randomRecipes.size()));
        if (this.randomRecipes.size() == 0) {
            Bukkit.getLogger().info(String.format("Vanilla recipes are %s", this.vanillaRecipes.size()));
            this.randomRecipes = getRandomRecipes(this.vanillaRecipes);
        }

        Bukkit.getLogger().info(String.format("Registering %s recipes", randomRecipes.size()));
    }

    @Override
    public void onUnregister() {
        Bukkit.getLogger().info("Clearing recipes...");
    }
}
