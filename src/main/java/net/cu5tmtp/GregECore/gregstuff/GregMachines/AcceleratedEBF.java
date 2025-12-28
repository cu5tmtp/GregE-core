package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregEModifiers;
import net.cu5tmtp.GregECore.tag.ModTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_INVAR_HEATPROOF;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class AcceleratedEBF extends WorkableElectricMultiblockMachine {

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

        var back = getFrontFacing().getOpposite();

        BlockPos scanStart = getPos().above().relative(back);

        java.util.Set<Block> foundCoils = new java.util.HashSet<>();

        for (int y = 0; y <= 1; y++) {
            BlockPos currentCenter = scanStart.above(y);

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;

                    BlockPos coilPos = currentCenter.offset(x, 0, z);
                    Block block = level.getBlockState(coilPos).getBlock();

                    foundCoils.add(block);
                }
            }
        }

        if (foundCoils.size() != 1) {
            this.coilTemp = 0;
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
    }

    public int getMaxTemp() {
        return this.coilTemp + (100 * Math.max(0, getTier() - GTValues.MV));
    }

    public static MachineDefinition ACCELERATEDEBF = REGISTRATE
            .multiblock("acceleratedebf", AcceleratedEBF::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GregEModifiers::acceleratedEBFModifier, GTRecipeModifiers.OC_PERFECT)
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
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("The machine starts speeding up with the power of magic remnants in the coils." +
                    " Depending on the coil, the machine speeds up faster. The coils give you the exact amount of recipe reduction.").withStyle(style -> style.withColor(0x00FFFF)))
            .tooltips()
            .register();

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            textList.add(Component.translatable("Coil temperature: " + coilTemp + "K").withStyle(ChatFormatting.AQUA));
            switch (coilTemp){
                case 1800 -> textList.add(Component.translatable("Recipes are shortened by 15%." ).withStyle(ChatFormatting.GREEN));
                case 3600 -> textList.add(Component.translatable("Recipes are shortened by 30%.").withStyle(ChatFormatting.GREEN));
                case 5400 -> textList.add(Component.translatable("Recipes are shortened by 45%.").withStyle(ChatFormatting.GREEN));
                default -> textList.add(Component.translatable("Different coils detected!").withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void init() {}
}
