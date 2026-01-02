package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.item.GreggyItems;
import net.cu5tmtp.GregECore.tag.ModTag;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class GiantAcceleratedEBF extends WorkableElectricMultiblockMachine {

    public GiantAcceleratedEBF(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private int coilTemp = 0;
    private IFluidHandler coolantHandler;

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.checkCoil();
        List<IFluidHandler> coolantContainers = new ArrayList<>();
        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE || io == IO.OUT) continue;
            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(coolantContainers::add);
            }
        }
        this.coolantHandler = new FluidHandlerList(coolantContainers);
    }

    @Override
    public boolean onWorking() {
        int amountToDrain = 1;
        Fluid coolant = GreggyItems.DEIONIZED_WATER.getFluid();
        FluidStack resource = new FluidStack(coolant, amountToDrain);

        if (getOffsetTimer() % 40 == 0) {

            FluidStack simulation = GTTransferUtils.drainFluidAccountNotifiableList(
                    coolantHandler,
                    resource,
                    IFluidHandler.FluidAction.SIMULATE
            );

            if(simulation.isEmpty()) {
                getRecipeLogic().interruptRecipe();
                return false;
            }

            GTTransferUtils.drainFluidAccountNotifiableList(
                    coolantHandler,
                    resource,
                    IFluidHandler.FluidAction.EXECUTE
            );
        }
        return super.onWorking();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {

        //Check if input hatches have enough coolant
        int amountToDrain = 1;
        Fluid coolant = GreggyItems.DEIONIZED_WATER.getFluid();
        FluidStack resource = new FluidStack(coolant, amountToDrain);

        FluidStack simulation = GTTransferUtils.drainFluidAccountNotifiableList(
                coolantHandler,
                resource,
                IFluidHandler.FluidAction.SIMULATE
        );

        if(simulation.isEmpty()) {
            return false;
        }

        return super.beforeWorking(recipe);
    }

    private void checkCoil() {

        //Check is coils are the same
        Level level = getLevel();
        if (level == null || level.isClientSide) return;

        var back = getFrontFacing().getOpposite();

        BlockPos scanStart = getPos().above().relative(back,2);

        java.util.Set<Block> foundCoils = new java.util.HashSet<>();

        for (int y = 0; y <= 4; y++) {
            BlockPos currentCenter = scanStart.above(y);
            if (y == 2) continue;

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
            case "gregecore:malachite_coil" -> 7400;
            case "gregecore:twilight_coil"  -> 9200;
            case "gregecore:desh_coil"      -> 11000;
            default -> 0;
        };
    }

    public int getMaxTemp() {
        return this.coilTemp + (100 * Math.max(0, getTier() - GTValues.MV));
    }

    public static MachineDefinition GIANTACCELERATEDEBF = REGISTRATE
            .multiblock("giantacceleratedebf", GiantAcceleratedEBF::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GregEModifiers::giantAcceleratedEBFModifier, GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("BFFFB", "G   G", "G   G", "G   G", "G   G", "G   G", "BFFFB")
                        .aisle("BBBBB", " DDD ", " DDD ", " EEE ", " DDD ", " DDD ", "BBBBB")
                        .aisle("BBBBB", " D D ", " D D ", " E E ", " D D ", " D D ", "BBCBB")
                        .aisle("BBBBB", " DDD ", " DDD ", " EEE ", " DDD ", " DDD ", "BBBBB")
                        .aisle("BFAFB", "G   G", "G   G", "G   G", "G   G", "G   G", "BFFFB")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(CASING_TUNGSTENSTEEL_ROBUST.get())
                                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2))
                                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)))
                        .where('C', Predicates.abilities(PartAbility.MUFFLER).setMaxGlobalLimited(1))
                        .where('D', Predicates.blockTag(ModTag.Blocks.MAGICAL_COILS_T2))
                        .where('E', Predicates.blocks(CASING_EXTREME_ENGINE_INTAKE.get()))
                        .where('F', Predicates.blocks(FIREBOX_TUNGSTENSTEEL.get()))
                        .where('G', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungsten_carbide_frame"))))
                        .where(' ', Predicates.any())

                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"),
                                 GTCEu.id("block/multiblock/distillation_tower"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Perfect Overclock and Magical Coils").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Avaible coils: Malachite").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("The machine starts speeding up with the power of the magic remnants in the coils." +
                    " Depending on the coil, the machine speeds up faster. The coils tell you the exact amount of recipe time reduction.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("The power of the coils grow. Now they are able to work in parallel, but due to the intense heat generated," +
                    " they require ").withStyle(style -> style.withColor(0x90EE90)).append(Component.literal("1mb of Deionized Water per 40 ticks.").withStyle(style -> style.withColor(0xFF0000))))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("The machine only accepts 32 of the same coil. Do not mix them. After the machine forms, " +
                    "you can see activated Magical Coil abilities in the controller,").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            textList.add(Component.translatable("Coil temperature: " + coilTemp + "K").withStyle(ChatFormatting.AQUA));
            switch (coilTemp){
                case 7400 -> textList.add(Component.translatable("Recipes are shortened by 20% and     2 parallels are applied." ).withStyle(ChatFormatting.GREEN));
                case 9200 -> textList.add(Component.translatable("Recipes are shortened by 40% and     4 parallels are applied.").withStyle(ChatFormatting.GREEN));
                case 11000 -> textList.add(Component.translatable("Recipes are shortened by 60% and     8 parallels are applied.").withStyle(ChatFormatting.GREEN));
                default -> textList.add(Component.translatable("Different coils detected!").withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void init() {}
}
