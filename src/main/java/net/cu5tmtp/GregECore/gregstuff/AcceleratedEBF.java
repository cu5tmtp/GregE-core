package net.cu5tmtp.GregECore.gregstuff;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.mojang.logging.LogUtils;
import net.cu5tmtp.GregECore.tag.ModTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_INVAR_HEATPROOF;
import static net.cu5tmtp.GregECore.GregECore.REGISTRATE;

public class AcceleratedEBF extends WorkableElectricMultiblockMachine {

    private static final Logger LOGGER = LogUtils.getLogger();

    public AcceleratedEBF(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private int coilTemp = 0;

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.checkCoil();
    }

    private void checkCoil() {
        Level level = getLevel();
        if (level == null || level.isClientSide) return;

        // 1. Definujeme směr "dozadu" vzhledem k tomu, kam kouká controller
        var back = getFrontFacing().getOpposite();

        // 2. Najdeme startovní bod skenování: o 1 nahoru a o 1 dozadu od controlleru
        // Toto je středový prázdný blok v prvním patře cívek
        BlockPos scanStart = getPos().above().relative(back);

        java.util.Set<Block> foundCoils = new java.util.HashSet<>();

        // 3. Projdeme dvě patra (y=0 je první patro cívek, y=1 je druhé)
        for (int y = 0; y <= 1; y++) {
            BlockPos currentCenter = scanStart.above(y);

            // Kolem tohoto středu zkontrolujeme všech 8 sousedních bloků
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // Přeskočíme střed (vzduch, kde probíhá recept)
                    if (x == 0 && z == 0) continue;

                    // Najdeme blok cívky relativně k aktuálnímu středu patra
                    BlockPos coilPos = currentCenter.offset(x, 0, z);
                    Block block = level.getBlockState(coilPos).getBlock();

                    foundCoils.add(block);
                }
            }
        }

        if (foundCoils.size() != 1) {
            this.coilTemp = 0;
            LOGGER.info("Sken selhal: Nalezeno {} druhu bloku na pozicich civek.", foundCoils.size());
            return;
        }

        Block coilBlock = foundCoils.iterator().next();
        String registryName = ForgeRegistries.BLOCKS.getKey(coilBlock).toString();

        this.coilTemp = switch (registryName) {
            case "gregecore:manasteel_coil" -> 1800;
            case "gregecore:twilight_coil"  -> 3600;
            case "gregecore:desh_coil"      -> 5400;
            default -> 0;
        };

        LOGGER.info("Sken uspesny: {} | Teplota nastavena na: {}K", registryName, this.coilTemp);
    }

    public int getMaxTemp() {
        return this.coilTemp + (100 * Math.max(0, getTier() - GTValues.MV));
    }

    public static MachineDefinition ACCELERATEDEBF = REGISTRATE
            .multiblock("acceleratedebf", AcceleratedEBF::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GregEModifiers::acceleratedEBFModifier)
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
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_heatproof"),
                                 GTCEu.id("block/multiblock/electric_blast_furnace"))
            .register();

    public static void init() {}
}
