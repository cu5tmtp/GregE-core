package net.cu5tmtp.GregECore.gregstuff;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_INVAR_HEATPROOF;
import static net.cu5tmtp.GregECore.GregECore.REGISTRATE;

public class AcceleratedEBF extends WorkableElectricMultiblockMachine {

    public AcceleratedEBF(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private static RecipeModifier currentModifier = GregEModifiers::weakMagicalCoil;

    private @Nullable TickableSubscription tickSubscription;

    public RecipeModifier getRecipeModifier() {
        checkCoil();
        return currentModifier;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        checkCoil();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (tickSubscription != null) {
            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    private void checkCoil() {
        Level level = getLevel();
        if (level == null) return;

        BlockPos abovePosition = getPos().above();
        BlockState stateAbove = level.getBlockState(abovePosition);

        String registryName = ForgeRegistries.BLOCKS.getKey(stateAbove.getBlock()).toString();

        switch (registryName) {
            case "kubejs:manasteel_coil" -> currentModifier = GregEModifiers::weakMagicalCoil;
            case "kubejs:twilightcoil"   -> currentModifier = GregEModifiers::averageMagicalCoil;
            case "kubejs:deshcoil"       -> currentModifier = GregEModifiers::strongMagicalCoil;
            default                      -> currentModifier = GregEModifiers::weakMagicalCoil;
        }
    }
    public static final MachineDefinition ACCELERATEDEBF = REGISTRATE
            .multiblock("acceleratedebf", AcceleratedEBF::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(currentModifier)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> {
                var casing = blocks(CASING_INVAR_HEATPROOF.get()).setMinGlobalLimited(10);
                var abilities = Predicates.autoAbilities(definition.getRecipeTypes())
                        .or(Predicates.autoAbilities(true, false, false));
                return FactoryBlockPattern.start()
                        .aisle("XSX", "XXX", "XXX")
                        .aisle("XXX", "XXX", "XXX")
                        .where('S', Predicates.controller(blocks(definition.getBlock())))
                        .where('X', casing.or(abilities))
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_heatproof"), GTCEu.id("block/multiblock/electric_blast_furnace"))
            .register();

    public static void init() {}
}
