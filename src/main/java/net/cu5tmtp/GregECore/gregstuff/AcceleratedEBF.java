package net.cu5tmtp.GregECore.gregstuff;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.cu5tmtp.GregECore.tag.ModTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

        java.util.Set<Block> foundCoils = new java.util.HashSet<>();

        for (int y = 1; y <= 2; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = 0; z >= -2; z--) {
                    if (x == 0 && z == -1) continue;
                    BlockPos coilPos = getPos().offset(x, y, z);
                    Block block = level.getBlockState(coilPos).getBlock();

                    foundCoils.add(block);
                }
            }
        }

        if (foundCoils.size() > 1) {
            this.currentModifier = GregEModifiers::wrongCoils;
            return;
        }

        Block coilBlock = foundCoils.iterator().next();
        String registryName = ForgeRegistries.BLOCKS.getKey(coilBlock).toString();

        switch (registryName) {
            case "gregecore:manasteel_coil" -> currentModifier = GregEModifiers::weakMagicalCoil;
            case "gregecore:twilight_coil"  -> currentModifier = GregEModifiers::averageMagicalCoil;
            case "gregecore:desh_coil"      -> currentModifier = GregEModifiers::strongMagicalCoil;
            default                         -> currentModifier = GregEModifiers::wrongCoils;
            }
    }

    public static final MachineDefinition ACCELERATEDEBF = REGISTRATE
            .multiblock("acceleratedebf", AcceleratedEBF::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(currentModifier)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("BBB", "DDD", "DDD", "BBB")
                        .aisle("BBB", "D D", "D D", "BCB")
                        .aisle("BAB", "DDD", "DDD", "BBB")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(CASING_INVAR_HEATPROOF.get())
                                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)))
                        .where('C', Predicates.abilities(PartAbility.MUFFLER).setMaxGlobalLimited(1))
                        .where('D', Predicates.blockTag(ModTag.Blocks.MAGICAL_COILS_T1))
                        .where(' ', Predicates.any())

                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_heatproof"), GTCEu.id("block/multiblock/electric_blast_furnace"))

            .register();

    public static void init() {}
}
