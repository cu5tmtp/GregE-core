package net.cu5tmtp.GregECore.gregstuff.GregMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mojang.logging.LogUtils;
import mezz.jei.api.recipe.RecipeType;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.managers.DysonSwarmManager;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class DysonSwarmLauncher extends WorkableElectricMultiblockMachine {
    public DysonSwarmLauncher(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DysonSwarmLauncher.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER
    );
    @Persisted
    protected double sailsShot = 0;

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        DysonSwarmManager.setTotalSails(sailsShot);
        super.onLoad();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        DysonSwarmManager.addSail();
        sailsShot++;
        this.markAsDirty();
        return super.beforeWorking(recipe);
    }

    public static MachineDefinition DYSON_SWARM_LAUNCHER = REGISTRATE
            .multiblock("dysonswarmlauncher", DysonSwarmLauncher::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(GregERecipeTypes.LAUNCH_SAILS)
            .appearanceBlock(COMPUTER_CASING)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("GGGGGGG", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", " H   H ", " H   H ", " H   H ", " H   H ", " H   H ", " H   H ", "       ", "       ", "       ", "       ", "  NNN  ", "       ", "       ", "  NNN  ", "       ", "       ", "  NNN  ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", "  III  ", "  IJI  ", "  III  ", "   I   ", "       ", "       ", "  H H  ", "  H H  ", "       ", "       ", " N   N ", "       ", "       ", " N   N ", "       ", "       ", " N   N ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", "  III  ", "  JKJ  ", "  IKI  ", "  IKI  ", "   K   ", "   K   ", "   K   ", "   K   ", "   K   ", "   K   ", " N L N ", "   K   ", "   K   ", " N L N ", "   K   ", "   K   ", " N L N ", "   K   ", "   K   ", "   K   ", "   M   ")
                        .aisle("GGGGGGG", "  III  ", "  IJI  ", "  III  ", "   I   ", "       ", "       ", "  H H  ", "  H H  ", "       ", "       ", " N   N ", "       ", "       ", " N   N ", "       ", "       ", " N   N ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", " H   H ", " H   H ", " H   H ", " H   H ", " H   H ", " H   H ", "       ", "       ", "       ", "       ", "  NNN  ", "       ", "       ", "  NNN  ", "       ", "       ", "  NNN  ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", " BFFFB ", " BFFFB ", " BFFFB ", " BBBBB ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", " BEEEB ", " BADDB ", " BDDDB ", " BBBBB ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ")
                        .aisle("GGGGGGG", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ", "       ")
                        .where('A', Predicates.controller(blocks(definition.getBlock())))
                        .where('B', Predicates.blocks(COMPUTER_CASING.get())
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(1)))
                        .where('D', Predicates.blocks(ADVANCED_COMPUTER_CASING.get()))
                        .where('E', Predicates.blocks(HIGH_POWER_CASING.get()))
                        .where('F', Predicates.blocks(COMPUTER_HEAT_VENT.get()))
                        .where('G', Predicates.blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                        .where('H', Predicates.blocks(CASING_STEEL_TURBINE.get()))
                        .where('I', Predicates.blocks(GCYMBlocks.CASING_STRESS_PROOF.get()))
                        .where('J', Predicates.blocks(CASING_EXTREME_ENGINE_INTAKE.get()))
                        .where('K', Predicates.blocks(FUSION_CASING.get()))
                        .where('L', Predicates.blocks(FUSION_COIL.get()))
                        .where('M', Predicates.blocks(GCYMBlocks.MOLYBDENUM_DISILICIDE_COIL_BLOCK.get()))
                        .where('N', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("minecraft:cyan_stained_glass"))))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/hpca/computer_casing/front"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Dyson Swarm Launcher").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("This machine launches solar sails into the sun orbit. Depending on the number of them, " +
                    "various boosts are given to Dyson Swarm Energy Collector.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("The breakpoints are: 500 -> 1x, 50000 -> 5x, 150000 -> 50x, 500000 -> 500x.").withStyle(style -> style.withColor(0x90EE90)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("Number of solar sails in orbit: " + (int) DysonSwarmManager.getTotalSails()).withStyle(ChatFormatting.AQUA));
            textList.add(Component.translatable("Energy generation boost: " + (int) DysonSwarmManager.getBoost()).withStyle(ChatFormatting.AQUA));
        }
    }

    public static void init() {}
}
