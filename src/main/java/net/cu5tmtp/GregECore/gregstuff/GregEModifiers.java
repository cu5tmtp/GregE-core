package net.cu5tmtp.GregECore.gregstuff;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;

public class GregEModifiers {

    public static ModifierFunction acceleratedEBFModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof AcceleratedEBF ebf)) {
            return ModifierFunction.NULL;
        }

        int blastFurnaceTemperature = ebf.getMaxTemp();
        int recipeTemp = recipe.data.contains("ebf_temp") ? recipe.data.getInt("ebf_temp") : 0;

        if (recipeTemp > blastFurnaceTemperature) {
            return ModifierFunction.NULL;
        }

        if (RecipeHelper.getRecipeEUtTier(recipe) > ebf.getTier()) {
            return ModifierFunction.NULL;
        }

        double speedMultiplier = switch (blastFurnaceTemperature) {
            case 1800 -> 0.85;
            case 3600 -> 0.70;
            case 5400 -> 0.55;
            default -> 1.0;
        };

        return ModifierFunction.builder()
                .eutMultiplier(0.8)
                .durationMultiplier(speedMultiplier)
                .build();
    }
}

