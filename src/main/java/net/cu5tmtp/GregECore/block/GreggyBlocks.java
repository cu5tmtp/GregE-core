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

    //Thanks to StarT core for teaching me how to do this
    protected static BlockEntry<ActiveBlock> createActiveBlock(String name, ResourceLocation baseTexture) {
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

    public static final BlockEntry<ActiveBlock> MANASTEEL_COIL = createActiveBlock("manasteel_coil",
            GregECore.id("block/coils/machine_coil_manasteel"));

    public static final BlockEntry<ActiveBlock> TWILIGHT_COIL = createActiveBlock("twilight_coil",
            GregECore.id("block/coils/machine_coil_twilight"));

    public static final BlockEntry<ActiveBlock> DESH_COIL = createActiveBlock("desh_coil",
            GregECore.id("block/coils/machine_coil_desh"));

    public static final BlockEntry<ActiveBlock> MALACHITE_COIL = createActiveBlock("malachite_coil",
            GregECore.id("block/coils/machine_coil_malachite"));

    public static final BlockEntry<ActiveBlock> FORGOTTEN_COIL = createActiveBlock("forgotten_coil",
            GregECore.id("block/coils/machine_coil_forgotten"));

    public static final BlockEntry<ActiveBlock> PTFE_ENGINE_INTAKE = createActiveBlock("ptfe_engine_intake",
            GregECore.id("block/engines/ptfe_engine_intake"));

    public static final BlockEntry<ActiveBlock> VIBRATION_ENGINE_INTAKE = createActiveBlock("vibration_engine_intake",
            GregECore.id("block/engines/vibration_engine_intake"));

    public static final BlockEntry<ActiveBlock> PTFE_FIREBOX = createActiveBlock("ptfe_firebox_casing",
            GregECore.id("block/firebox/ptfe_firebox"));

    public static final void init(){}
}


