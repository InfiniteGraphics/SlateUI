package top.huliawsl.slateui.ecosystem;

import java.util.List;

public record RecipeTransferAction(String recipeId, List<String> ingredients, String output) {

    public RecipeTransferAction {
        ingredients = ingredients == null ? List.of() : List.copyOf(ingredients);
        recipeId = recipeId == null ? "" : recipeId;
        output = output == null ? "" : output;
    }
}
