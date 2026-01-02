package net.cu5tmtp.GregECore.item;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.common.data.GTElements;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore;

public class GreggyItems {
    public static Material MYTHRIL, NOBELIUM, FRANKLINITE, LITHIUM_HYDROXIDE, LITHIUM_AMALGAMATION, PLUTONIUM_HEXAFLUORIDE;
    public static Material MOLTEN_MYTHRIL, MOLTEN_FRANKLINITE, DEIONIZED_WATER, XENOZENE, NOBARULEIUM, QUDRACTIK, JELENOGAS, VERCI_54, MANOPERED_36, QUENZIN;

    public static Material buildFluidMaterial(String name, int color, String formula) {
        return new Material.Builder(GregECore.id(name))
                .fluid()
                .color(color)
                .iconSet(MaterialIconSet.FLUID)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .formula(formula)
                .buildAndRegister();
    }

    public static void register(){
        //items
        MYTHRIL = new Material.Builder(GregECore.id("mythril"))
                .ingot()
                .color(0x63A2B0)
                .iconSet(MaterialIconSet.SHINY)
                .formula("Mt")
                .buildAndRegister();

        NOBELIUM = new Material.Builder(GregECore.id("nobelium"))
                .ingot()
                .color(0xFFA500)
                .iconSet(MaterialIconSet.SHINY)
                .flags(MaterialFlags.GENERATE_BOLT_SCREW)
                .formula("No")
                .buildAndRegister();

        FRANKLINITE = new Material.Builder(GregECore.id("franklinite"))
                .ingot()
                .color(0x964B00)
                .iconSet(MaterialIconSet.ROUGH)
                .formula("(Zn,Mn,Fe)2+(Fe,Mn)3+2O4")
                .buildAndRegister();

        LITHIUM_HYDROXIDE = new Material.Builder(GregECore.id("lithium_hydroxide"))
                .dust()
                .color(0x7393B3)
                .formula("LiOH")
                .buildAndRegister();

        LITHIUM_AMALGAMATION = new Material.Builder(GregECore.id("lithium_amalgamation"))
                .dust()
                .color(0x739FF3)
                .formula("LiHg₃")
                .buildAndRegister();

        PLUTONIUM_HEXAFLUORIDE = new Material.Builder(GregECore.id("plutonium_hexafluoride"))
                .dust()
                .color(0x141414)
                .formula("PuF₆")
                .buildAndRegister();

        //fluids
        DEIONIZED_WATER = new Material.Builder(GregECore.id("deionized_water"))
                .fluid()
                .color(0xdddddd)
                .iconSet(MaterialIconSet.FLUID)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .formula("H₂O")
                .buildAndRegister();

        //₀ ₁ ₂ ₃ ₄ ₅ ₆ ₇ ₈ ₉

        buildFluidMaterial("molten_mythril", 0x63A2B0, "Mt");
        buildFluidMaterial("molten_franklinite", 0x964B00, "(Zn,Mn,Fe)2+(Fe,Mn)3+2O4");
        buildFluidMaterial("xenozene_gas", 0xAAFF00, "C₆H₅Xe");
        buildFluidMaterial("nobalureium_gas", 0x6495ED, "Nb₂");
        buildFluidMaterial("quadractik_gas", 0xB4C424, "Qt₄Cl₂");
        buildFluidMaterial("jelenogas_gas", 0x808000,"C₂H₅Je");
        buildFluidMaterial("verci_54", 0x9F2B68, "Vr₅₄He₂");
        buildFluidMaterial("manopered_36", 0x5D3FD3,"Mp₃₆ArLi");
        buildFluidMaterial("quenzin", 0x770737, "Qz₂N₄");
        buildFluidMaterial("superheated_solar", 0xFDDA0D, "H₄He₂So*⁺");
    }
}
