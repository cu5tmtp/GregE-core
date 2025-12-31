package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.*;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.managers.DysonSwarmManager;

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
            case 3600 -> 0.7;
            case 5400 -> 0.55;
            default -> 0.85;
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
            default -> 0.8;
        };

        int parallelsMax = switch (blastFurnaceTemperature) {
            case 7400 -> 2;
            case 9200 -> 4;
            case 11000 -> 8;
            default -> 2;
        };

        //This part of the code is a inspiration from StarT core - Throughput Boosting, I modified it to work with my custom coils.
        int parallelsAvailable = Math.max(0, ParallelLogic.getParallelAmountFast(machine, recipe, parallelsMax));

        if (parallelsAvailable >= parallelsMax) {
            return ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(parallelsMax))
                    .eutMultiplier(speedMultiplier)
                    .durationMultiplier(speedMultiplier)
                    .parallels(parallelsMax)
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }

    public static ModifierFunction giantChemicalReactor(MetaMachine machine, GTRecipe recipe){

        if (!(machine instanceof GiantChemicalReactor gcr)) {
            return ModifierFunction.NULL;
        }

        int parallelsAvailableGCR = Math.max(0, ParallelLogic.getParallelAmountFast(machine, recipe, gcr.parallelBoost));

        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallelsAvailableGCR))
                .eutMultiplier(gcr.energyBoost)
                .durationMultiplier(gcr.speedBoost)
                .parallels(parallelsAvailableGCR)
                .build();

    }

    public static ModifierFunction dysonSwarmGenBoost(MetaMachine machine, GTRecipe recipe){

        if (!(machine instanceof DysonSwarmEnergyCollector ds)) {
            return ModifierFunction.NULL;
        }

        return ModifierFunction.builder()
                .eutMultiplier(DysonSwarmManager.getBoost())
                .build();
    }
}

