package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.AcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.GiantAcceleratedEBF;

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
            case 1800 -> 0.8;
            case 3600 -> 0.6;
            case 5400 -> 0.4;
            default -> 1.0;
        };

        return ModifierFunction.builder()
                .eutMultiplier(0.8)
                .durationMultiplier(speedMultiplier)
                .build();
    }

    public static ModifierFunction giantAcceleratedEBFModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof GiantAcceleratedEBF ebf)) {
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
            case 7400 -> 0.8;
            case 9200 -> 0.6;
            case 11000 -> 0.4;
            default -> 1.0;
        };

        return ModifierFunction.builder()
                .eutMultiplier(0.6)
                .durationMultiplier(speedMultiplier)
                .outputModifier(ContentModifier.multiplier(2))
                .build();
    }
}

