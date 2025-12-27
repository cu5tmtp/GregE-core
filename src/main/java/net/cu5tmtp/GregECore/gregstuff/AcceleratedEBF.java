package net.cu5tmtp.GregECore.gregstuff;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_PTFE_INERT;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class AcceleratedEBF extends WorkableElectricMultiblockMachine {

    private TickableSubscription tickSubscription;
    public AcceleratedEBF(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        checkCoil();
        if (!isRemote()) {
            //tickSubscription = this.subscribeServerTick(this::turnGreenery);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (!isRemote()) {
            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    private void checkCoil() {
        Level level = getRecipeLogic().getMachine().getHolder().self().getLevel();

        BlockPos currentPosition = getRecipeLogic().getMachine().getHolder().pos();
        BlockPos abovePosition = currentPosition.above();

        BlockState stateAbove = level.getBlockState(abovePosition);
        Block blockAbove = stateAbove.getBlock();

        String registryName = ForgeRegistries.BLOCKS.getKey(blockAbove).toString();

        if(registryName.equals("kubejs:manasteel_coil"){

        }

    }

    public static final MultiblockMachineDefinition ACCELERATEDEBF = REGISTRATE
            .multiblock("acceleratedebf", AcceleratedEBF::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GregEModifiers.WEAK_MAGICAL_COIL)
            .appearanceBlock(() -> CASING_PTFE_INERT)
            .pattern(definition -> {
                var casing = blocks(CASING_PTFE_INERT).setMinGlobalLimited(10);
                var abilities = Predicates.autoAbilities(definition.getRecipeTypes())
                        .or(Predicates.autoAbilities(true, false, false));
                return FactoryBlockPattern.start()
                        .aisle("XSX", "XXX", "XXX")
                        .aisle("XXX", "XXX", "XXX")
                        .where('S', Predicates.controller(blocks(definition.getBlock())))
                        .where('X', casing.or(abilities))
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_inert_ptfe"),
                    GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register();

}
