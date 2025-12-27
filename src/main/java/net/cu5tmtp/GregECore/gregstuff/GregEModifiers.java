package net.cu5tmtp.GregECore.gregstuff;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

public class GregEModifiers {
    public static final RecipeModifier WEAK_MAGICAL_COIL = GregEModifiers::weakMagicalCoil;
    public static final RecipeModifier AVERAGE_MAGICAL_COIL = GregEModifiers::averageMagicalCoil;
    public static final RecipeModifier STRONG_MAGICAL_COIL = GregEModifiers::strongMagicalCoil;

    public static ModifierFunction weakMagicalCoil(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof IMultiController controller && controller.isFormed()) {

            return ModifierFunction.builder()
                    .durationModifier(ContentModifier.multiplier(0.75))
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }

    public static ModifierFunction averageMagicalCoil(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof IMultiController controller && controller.isFormed()) {

            return ModifierFunction.builder()
                    .durationModifier(ContentModifier.multiplier(0.6))
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }

    public static ModifierFunction strongMagicalCoil(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof IMultiController controller && controller.isFormed()) {

            return ModifierFunction.builder()
                    .durationModifier(ContentModifier.multiplier(0.5))
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }
}

