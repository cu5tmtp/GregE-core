package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.single;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.cleanroom.DimensionSimulator;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class EpicParallelControllHatch {

    public static MachineDefinition EPIC_PARALLEL_HATCH = null;

    static {
            EPIC_PARALLEL_HATCH = REGISTRATE.machine("epic_parallel_hatch",
                            holder -> new ParallelHatchPartMachine(holder, GTValues.UHV))
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.PARALLEL_HATCH)
                    .tooltips(Component.literal("Allows to run up to 1024 recipes in parallel.").withStyle(ChatFormatting.WHITE),
                            Component.literal("Multiblock sharing" + ChatFormatting.DARK_RED + "Disabled"))
                    .tier(GTValues.UHV)
                    .colorOverlayTieredHullModel(GregECore.id("block/overlay/parallelnormal/overlay_front"))
                    .register();
    }

    public static void init() {}

}
