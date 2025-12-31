package net.cu5tmtp.GregECore.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ModelFile;

import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class GreggyBlocks {
    protected static BlockEntry<ActiveBlock> createActiveCoil(String name, ResourceLocation baseTexture) {
        return REGISTRATE.block(name, ActiveBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, prov) -> {
                    ModelFile inactive = prov.models().cubeAll(ctx.getName(), baseTexture);
                    ResourceLocation activeTexLoc = new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath() + "_active");
                    ModelFile active = prov.models().withExistingParent(ctx.getName() + "_active", "minecraft:block/cube_all")
                            .texture("all", activeTexLoc);

                    prov.getVariantBuilder(ctx.get())
                            .partialState().with(GTBlockStateProperties.ACTIVE, false)
                            .modelForState().modelFile(inactive).addModel()
                            .partialState().with(GTBlockStateProperties.ACTIVE, true)
                            .modelForState().modelFile(active).addModel();
                })
                .item(BlockItem::new)
                .build()
                .register();
    }

    public static final BlockEntry<ActiveBlock> MANASTEEL_COIL = createActiveCoil("manasteel_coil",
            GregECore.id("block/coils/machine_coil_manasteel"));

    public static final BlockEntry<ActiveBlock> TWILIGHT_COIL = createActiveCoil("twilight_coil",
            GregECore.id("block/coils/machine_coil_twilight"));

    public static final BlockEntry<ActiveBlock> DESH_COIL = createActiveCoil("desh_coil",
            GregECore.id("block/coils/machine_coil_desh"));

    public static final BlockEntry<ActiveBlock> MALACHITE_COIL = createActiveCoil("malachite_coil",
            GregECore.id("block/coils/machine_coil_malachite"));

    public static final void init(){}
}


