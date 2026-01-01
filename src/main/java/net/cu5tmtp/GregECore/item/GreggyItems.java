package net.cu5tmtp.GregECore.item;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;

public class GreggyItems {
    public static Material MYTHRIL, NOBELIUM, FRANKLINITE, LITHIUM_HYDROXIDE, LITHIUM_AMALGAMATION, PLUTONIUM_HEXAFLUORIDE;
    public static Material MOLTEN_MYTHRIL, MOLTEN_FRANKLINITE, DEIONIZED_WATER, XENOZENE, NOBARULEIUM, QUDRACTIK, JELENOGAS, VERCI_54, MANOPERED_36, QUENZIN;

    public static Material buildFluidMaterial(String name, int color) {
        return new Material.Builder(GregECore.id(name))
                .fluid()
                .color(color)
                .iconSet(MaterialIconSet.FLUID)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }

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

        LITHIUM_HYDROXIDE = new Material.Builder(GregECore.id("lithium_hydroxide"))
                .dust()
                .color(0x7393B3)
                .buildAndRegister();

        LITHIUM_AMALGAMATION = new Material.Builder(GregECore.id("lithium_amalgamation"))
                .dust()
                .color(0x739FF3)
                .buildAndRegister();

        PLUTONIUM_HEXAFLUORIDE = new Material.Builder(GregECore.id("plutonium_hexafluoride"))
                .dust()
                .color(0x141414)
                .buildAndRegister();

        //fluids
        DEIONIZED_WATER = new Material.Builder(GregECore.id("deionized_water"))
                .fluid()
                .color(0xdddddd)
                .iconSet(MaterialIconSet.FLUID)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        buildFluidMaterial("molten_mythril", 0x63A2B0);
        buildFluidMaterial("molten_franklinite", 0x964B00);
        buildFluidMaterial("xenozene_gas", 0xAAFF00);
        buildFluidMaterial("nobalureium_gas", 0x6495ED);
        buildFluidMaterial("quadractik_gas", 0xB4C424);
        buildFluidMaterial("jelenogas_gas", 0x808000);
        buildFluidMaterial("verci_54", 0x9F2B68);
        buildFluidMaterial("manopered_36", 0x5D3FD3);
        buildFluidMaterial("quenzin", 0x770737);
        buildFluidMaterial("superheated_solar", 0xFDDA0D);
    }
}
