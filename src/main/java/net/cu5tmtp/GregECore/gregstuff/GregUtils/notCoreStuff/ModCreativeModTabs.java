package net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff;

import net.cu5tmtp.GregECore.block.ModBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.*;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.cu5tmtp.GregECore.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GregECore.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GREGE_TAB = CREATIVE_MODE_TABS.register("gregecoretab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.LINEARACCELERATOR.get()))
                    .title(Component.translatable("GregE core"))
                    .displayItems((pParameters, pOutput) -> {
                        ModBlocks.TAB_BLOCKS.forEach(block -> pOutput.accept(block.get()));
                        pOutput.accept(ModItems.ENERGY_BACTERIA.get());
                        pOutput.accept(ModItems.SPEED_BACTERIA.get());
                        pOutput.accept(ModItems.PARALLEL_BACTERIA.get());
                        pOutput.accept(ModItems.SOLAR_ACTIVATOR.get());
                        pOutput.accept(ModItems.SOLAR_SAIL.get());
                        pOutput.accept(ModItems.SOLAR_SAIL_CASE.get());
                        pOutput.accept(AcceleratedEBF.ACCELERATEDEBF.getItem());
                        pOutput.accept(GiantAcceleratedEBF.GIANTACCELERATEDEBF.getItem());
                        pOutput.accept(GiantChemicalReactor.GIANTCHR.getItem());
                        pOutput.accept(DysonSwarmLauncher.DYSON_SWARM_LAUNCHER.getItem());
                        pOutput.accept(DysonSwarmEnergyCollector.DYSON_SWARM_LAUNCHER.getItem());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
