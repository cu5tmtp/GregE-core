package net.cu5tmtp.GregECore.tag;

import net.cu5tmtp.GregECore.GregECore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTag {
    public static class Blocks{

        public static final TagKey<Block> MAGICAL_COILS_T1 = tag("magical_coils_t1");
        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(GregECore.MOD_ID, name));
        }
    }

    public static class Items{
        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(GregECore.MOD_ID, name));
        }

    }
}
