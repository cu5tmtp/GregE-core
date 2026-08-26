package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.cu5tmtp.GregECore.block.GreggyBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public class GregEPredicates {

    private static final String MAGICAL_COIL_TYPE = "MagicalCoilType";

    private static final MagicalCoil MANASTEEL = new MagicalCoil(GreggyBlocks.MANASTEEL_COIL, 1800);
    private static final MagicalCoil TWILIGHT = new MagicalCoil(GreggyBlocks.TWILIGHT_COIL, 3600);
    private static final MagicalCoil DESH = new MagicalCoil(GreggyBlocks.DESH_COIL, 5400);
    private static final MagicalCoil MALACHITE = new MagicalCoil(GreggyBlocks.MALACHITE_COIL, 7400);
    private static final MagicalCoil FORGOTTEN = new MagicalCoil(GreggyBlocks.FORGOTTEN_COIL, 9300);
    private static final MagicalCoil SUPERELEMENT = new MagicalCoil(GreggyBlocks.SUPERELEMENT_COIL, 11000);

    public static TraceabilityPredicate tierOneMagicalCoils() {
        return magicalCoils(MANASTEEL, TWILIGHT, DESH);
    }

    public static TraceabilityPredicate tierTwoMagicalCoils() {
        return magicalCoils(MALACHITE, FORGOTTEN, SUPERELEMENT);
    }

    private static TraceabilityPredicate magicalCoils(MagicalCoil... coils) {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (MagicalCoil coil : coils) {
                if (blockState.is(coil.block().get())) {
                    Object currentCoil = blockWorldState.getMatchContext().getOrPut(MAGICAL_COIL_TYPE, coil);
                    if (!currentCoil.equals(coil)) {
                        blockWorldState.setError(new PatternStringError("gtceu.multiblock.pattern.error.coils"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> Arrays.stream(coils)
                .sorted(Comparator.comparingInt(MagicalCoil::temperature))
                .map(coil -> BlockInfo.fromBlockState(coil.block().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.coils"));
    }

    public static int getMagicalCoilTemperature(PatternMatchContext context) {
        Object coil = context.get(MAGICAL_COIL_TYPE);
        return coil instanceof MagicalCoil magicalCoil ? magicalCoil.temperature() : 0;
    }

    private record MagicalCoil(Supplier<? extends Block> block, int temperature) {}
}
