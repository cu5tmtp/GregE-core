package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;

public class GregERecipeTypes {
    public static GTRecipeType LAUNCH_SAILS = GTRecipeTypes.register("launch_sails", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3,3,3,3)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);

    public static GTRecipeType GET_SOLAR_SAIL_ENERGY = GTRecipeTypes.register("get_solar_sail_energy", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3,3,3,3)
            .setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static void init(){
    }
}
