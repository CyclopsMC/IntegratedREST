package org.cyclops.integratedrest.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.Level;
import org.cyclops.integratedrest.IntegratedRest;
import org.cyclops.integratedrest.http.request.handler.RegistryItemRequestHandler;

public class EmiRecipeUtil {
    static {
        try {
            emiApiClz = RegistryItemRequestHandler.class.getClassLoader().loadClass("dev.emi.emi.api.EmiApi");
        } catch (ClassNotFoundException e) {
            IntegratedRest.clog(Level.ERROR, e.getMessage());
        }
    }

    private static Class<?> emiApiClz = null;


    public static boolean writeRecipesByOutputItemToNode(Item item, JsonObject rootNode) {
        // skip air
        if (item.toString().equals("minecraft:air") || emiApiClz == null) {
            return false;
        }

        var recipeManager = EmiApi.getRecipeManager();
        var stack = EmiStack.of(item);
        var recipesByOutput = recipeManager.getRecipesByOutput(stack);

        var arrNode = new JsonArray();
        for (EmiRecipe emiRecipe : recipesByOutput) {
            var categoryId = emiRecipe.getCategory().getId();
            // skip tag
            if (categoryId.toString().startsWith("minecraft:tag_recipes")) {
                continue;
            }

            var outputs = emiRecipe.getOutputs();
            boolean flag = false;
            for (EmiStack output : outputs) {
                if (output.getId().equals(stack.getId())) flag = true;
            }
            if (!flag) {
                continue;
            }

            var node = new JsonObject();
            node.addProperty("id", emiRecipe.getId().toString());

            node.addProperty("category", categoryId.toString());

            var catalystsArrNode = new JsonArray();
            emiRecipe.getCatalysts().stream().map(x -> {
                var jsonNode = new JsonObject();
                var stackArrNode = new JsonArray();
                x.getEmiStacks().stream().map(EmiIngredientSerializer::getSerialized).forEach(stackArrNode::add);
                jsonNode.add("inputs", stackArrNode);

                jsonNode.addProperty("amount", x.getAmount());
                jsonNode.addProperty("chance", x.getChance());

                return jsonNode;
            }).forEach(catalystsArrNode::add);
            node.add("catalysts", catalystsArrNode);

            var inputNode = new JsonArray();
            emiRecipe.getInputs().stream().map(EmiIngredientSerializer::getSerialized).forEach(inputNode::add);
            node.add("inputs", inputNode);

            arrNode.add(node);
        }
        rootNode.add("recipesByOutput", arrNode);
        return true;
    }
}
