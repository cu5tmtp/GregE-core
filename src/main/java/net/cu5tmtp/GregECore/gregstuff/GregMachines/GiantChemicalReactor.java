package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregEModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;
import static net.cu5tmtp.GregECore.item.ModItems.*;

public class GiantChemicalReactor extends WorkableElectricMultiblockMachine {
    public GiantChemicalReactor(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private final List<IItemHandler> cachedBoosterHandlers = new ArrayList<>();
    public double energyBoost = 1.0;
    public int parallelBoost = 1;
    public double speedBoost = 1.0;

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.cachedBoosterHandlers.clear();

        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE || io == IO.OUT) continue;

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;
                handlerList.getCapability(ItemRecipeCapability.CAP).stream()
                        .filter(IItemHandler.class::isInstance)
                        .map(IItemHandler.class::cast)
                        .forEach(this.cachedBoosterHandlers::add);
            }
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        for (IItemHandler handler : this.cachedBoosterHandlers) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                if (stack.is(ENERGY_BACTERIA.get())) {
                    this.energyBoost = 0.1;
                    this.parallelBoost = 1;
                    this.speedBoost = 1.0;
                    return super.beforeWorking(recipe);
                } else if (stack.is(PARALLEL_BACTERIA.get())) {
                    this.parallelBoost = 64;
                    this.energyBoost = 1.0;
                    this.speedBoost = 1.0;
                    return super.beforeWorking(recipe);
                } else if (stack.is(SPEED_BACTERIA.get())) {
                    this.speedBoost = 0.1;
                    this.energyBoost = 1.0;
                    this.parallelBoost = 1;
                    return super.beforeWorking(recipe);
                } else {
                    this.speedBoost = 1.0;
                    this.energyBoost = 1.0;
                    this.parallelBoost = 1;
                }
            }
        }

        return super.beforeWorking(recipe);
    }

    public static MachineDefinition GIANTCHR = REGISTRATE
            .multiblock("giantchr", GiantChemicalReactor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GTRecipeTypes.LARGE_CHEMICAL_RECIPES)
            .recipeModifiers(GregEModifiers::giantChemicalReactor, GTRecipeModifiers.OC_PERFECT)
            .appearanceBlock(CASING_STEEL_SOLID)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle(" GGG ", " EEE ", " EFE ", " EEE ", " III ", " EEE ", " EFE ", " EEE ", " GGG ")
                        .aisle("GHHHG", "E   E", "E F E", "E   E", "I F I", "E   E", "E F E", "E   E", "GHHHG")
                        .aisle("GHFHG", "E F E", "FFFFF", "E F E", "IFFFI", "E F E", "FFFFF", "E F E", "GHFHG")
                        .aisle("GHHHG", "E   E", "E F E", "E   E", "I F I", "E   E", "E F E", "E   E", "GHHHG")
                        .aisle(" GGG ", " EEE ", " EFE ", " EEE ", " III ", " EEE ", " EFE ", " EEE ", " GGG ")
                        .aisle("     ", "     ", "     ", "     ", "  I  ", "     ", "     ", "     ", "     ")
                        .aisle(" CCC ", " BIB ", " BIB ", "  I  ", "  I  ", "     ", "     ", "     ", "     ")
                        .aisle(" CCC ", " BAB ", " BBB ", "     ", "     ", "     ", "     ", "     ", "     ")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(CASING_STEEL_SOLID.get())
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)))
                        .where('C', Predicates.blocks(FIREBOX_STEEL.get()))
                        .where('E', Predicates.blocks(CASING_PTFE_INERT.get()))
                        .where('F', Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
                        .where('G', Predicates.blocks(CASING_EXTREME_ENGINE_INTAKE.get()))
                        .where('H', Predicates.blocks(CASING_TUNGSTENSTEEL_ROBUST.get()))
                        .where('I', Predicates.blocks(CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/distillation_tower"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Perfect Overclock and Bacterial Infestation").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("This machine is the pinnacle of bioengineering, thanks to special bred bacteria it can reach new heights." +
                    " Depending on the bacteria inserted in a input hatch, the machine posseses different abilities.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Energy bacteria: ").withStyle(s -> s.withColor(0x90EE90))
                    .append(Component.literal("90%").withStyle(s -> s.withColor(0xFF0000)))
                    .append(Component.literal(" energy discount.").withStyle(s -> s.withColor(0x90EE90))))
            .tooltips(Component.literal("Speed bacteria: ").withStyle(s -> s.withColor(0x90EE90))
                    .append(Component.literal("90%").withStyle(s -> s.withColor(0xFF0000)))
                    .append(Component.literal(" speed increase.").withStyle(s -> s.withColor(0x90EE90))))
            .tooltips(Component.literal("Parallel bacteria: ").withStyle(s -> s.withColor(0x90EE90))
                    .append(Component.literal("64").withStyle(s -> s.withColor(0xFF0000)))
                    .append(Component.literal(" parallels.").withStyle(s -> s.withColor(0x90EE90))))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("The machine can only have 1 active bacteria boost, " +
                    "it chooses the bacteria closest to the top left input hatch item slot.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.translatable("Energy multiplier: " + energyBoost).withStyle(ChatFormatting.AQUA));
            textList.add(Component.translatable("Speed multiplier: " + speedBoost).withStyle(ChatFormatting.AQUA));
            textList.add(Component.translatable("Parallels: " + parallelBoost).withStyle(ChatFormatting.AQUA));
        }
        super.addDisplayText(textList);
    }

    public static void init() {}
}
