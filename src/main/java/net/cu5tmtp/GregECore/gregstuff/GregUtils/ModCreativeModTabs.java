package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import net.cu5tmtp.GregECore.block.ModBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.AcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.GiantAcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
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
                        pOutput.accept(AcceleratedEBF.ACCELERATEDEBF.getItem());
                        pOutput.accept(GiantAcceleratedEBF.GIANTACCELERATEDEBF.getItem());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
