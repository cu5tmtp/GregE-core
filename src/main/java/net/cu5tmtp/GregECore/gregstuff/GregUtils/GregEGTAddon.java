package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class GregEGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GregECore.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
    }

    @Override
    public String addonModId() {
        return GregECore.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {}

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
    }

    @Override
    public void registerElements() {
        IGTAddon.super.registerElements();
    }

    @Override
    public void registerRecipeCapabilities() {
    }

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
    }
}
