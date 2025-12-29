package net.cu5tmtp.GregECore.item;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;

public class GreggyItems {
    public static Material MYTHRIL, NOBELIUM, FRANKLINITE;
    public static Material MOLTEN_MYTHRIL, MOLTEN_FRANKLINITE;
    public static void register(){
        //items
        MYTHRIL = new Material.Builder(GregECore.id("mythril"))
                .ingot()
                .color(0x63A2B0)
                .iconSet(MaterialIconSet.SHINY)
                .buildAndRegister();

        NOBELIUM = new Material.Builder(GregECore.id("nobelium"))
                .ingot()
                .color(0xFFA500)
                .iconSet(MaterialIconSet.SHINY)
                .flags(MaterialFlags.GENERATE_BOLT_SCREW)
                .buildAndRegister();

        FRANKLINITE = new Material.Builder(GregECore.id("franklinite"))
                .ingot()
                .color(0x964B00)
                .iconSet(MaterialIconSet.ROUGH)
                .buildAndRegister();

        //fluids

        MOLTEN_MYTHRIL = new Material.Builder(GregECore.id("molten_mythril"))
                .fluid()
                .color(0x63A2B0)
                .iconSet(MaterialIconSet.FLUID)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MOLTEN_FRANKLINITE = new Material.Builder(GregECore.id("molten_franklinite"))
                .fluid()
                .color(0x964B00)
                .iconSet(MaterialIconSet.FLUID)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
