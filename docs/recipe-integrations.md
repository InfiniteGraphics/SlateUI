# Recipe and Item Ecosystem

Recipe integrations are represented by `RecipeIntegration` with ids for JEI, REI, EMI, and XEI. SlateUI does not require these mods at runtime.

`ItemTooltipBridge` lets loader-specific code provide tooltip lines. `IngredientView`, `RecipeLayout`, and `GhostIngredient` are the UI components for ingredients, recipe layouts, and ghost ingredients.

`RecipeTransferAction` describes transfer intent without directly mutating inventories.
