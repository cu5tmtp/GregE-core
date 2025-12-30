package net.cu5tmtp.GregECore.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.MOD_ID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> PARALLEL_BACTERIA = ITEMS.register("parallel_bacteria",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENERGY_BACTERIA = ITEMS.register("energy_bacteria",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPEED_BACTERIA = ITEMS.register("speed_bacteria",
            () -> new Item(new Item.Properties()));
}
