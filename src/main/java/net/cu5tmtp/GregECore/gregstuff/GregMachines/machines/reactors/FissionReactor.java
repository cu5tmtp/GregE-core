package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.reactors;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.block.ModBlocks;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.coolant.CoolantInputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.coolant.CoolantOutputPartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

@SuppressWarnings("removal")
public class FissionReactor extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            FissionReactor.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private TickableSubscription heatSubscription;

    @DescSynced
    @Persisted
    public float controlRodInsertion = 100.0F;

    @DescSynced
    @Persisted
    public float heatLevel = 0.0F;

    @Persisted
    private int recipeTemp;

    @Persisted
    private int hullDmg = 100;

    private IFluidHandler coolantHandlerInput;
    private IFluidHandler coolantHandlerOutput;

    private static final float MAX_HEAT = 5000.0F;

    public FissionReactor(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        setInsertionPercent(controlRodInsertion);

        List<IFluidHandler> coolantContainers = new ArrayList<>();

        for (IMultiPart part : getParts()) {

            if(!(part instanceof CoolantInputPartMachine)){
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(coolantContainers::add);
            }
        }
        this.coolantHandlerInput = new FluidHandlerList(coolantContainers);

        coolantContainers.clear();

        for (IMultiPart part : getParts()) {

            if(!(part instanceof CoolantOutputPartMachine)){
                continue;
            }

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(coolantContainers::add);
            }
        }
        this.coolantHandlerOutput = new FluidHandlerList(coolantContainers);

        if (heatSubscription != null) {
            heatSubscription.unsubscribe();
            heatSubscription = null;
        }

        heatSubscription = this.subscribeServerTick(this::manageHeat);
    }

    private void manageHeat() {
        if (this.getLevel() != null && this.getLevel().isClientSide) return;

        if (getOffsetTimer() % 5 == 0) {

            if (this.getRecipeLogic().isWorking()) {
                float withdrawalPercent = 100f - getInsertionPercent();

                float baseHeat = this.recipeTemp * (withdrawalPercent / 100f) * 3.334f;
                float multiplier = 1.0f;

                if (withdrawalPercent > 60f) {
                    multiplier = 4.0f;
                } else if (withdrawalPercent > 30f) {
                    multiplier = 2.0f;
                }

                float generatedHeat = baseHeat * multiplier;
                this.heatLevel += generatedHeat;
            }

            if (this.heatLevel > 0 && this.coolantHandlerInput != null && this.coolantHandlerOutput != null) {
                int maxDrainAmount = 100;
                var simulatedDrain = this.coolantHandlerInput.drain(maxDrainAmount, IFluidHandler.FluidAction.SIMULATE);

                if (!simulatedDrain.isEmpty()) {
                    float coolingPower = getFluidCoolingPower(simulatedDrain);
                    FluidStack hotFluidOut = getHeatedCoolant(simulatedDrain);

                    if (coolingPower > 0 && hotFluidOut != null) {
                        float heatToDissipate = Math.min(this.heatLevel, simulatedDrain.getAmount() * coolingPower);
                        int fluidToConsume = (int) Math.ceil(heatToDissipate / coolingPower);

                        hotFluidOut.setAmount(fluidToConsume);

                        int acceptedFill = this.coolantHandlerOutput.fill(hotFluidOut, IFluidHandler.FluidAction.SIMULATE);

                        if (acceptedFill > 0) {
                            var actualDrained = this.coolantHandlerInput.drain(acceptedFill, IFluidHandler.FluidAction.EXECUTE);
                            hotFluidOut.setAmount(actualDrained.getAmount());
                            this.coolantHandlerOutput.fill(hotFluidOut, IFluidHandler.FluidAction.EXECUTE);

                            this.heatLevel -= (actualDrained.getAmount() * coolingPower);
                            if (this.heatLevel < 0) this.heatLevel = 0;
                        }
                    }
                }
            }

            if (this.heatLevel > 0 && !this.getRecipeLogic().isWorking()) {
                this.heatLevel -= 5.0f;
                if (this.heatLevel < 0) this.heatLevel = 0;
            }

            if (this.heatLevel > MAX_HEAT) {
                float excessHeat = this.heatLevel - MAX_HEAT;
                int damage = 1 + (int)(excessHeat / 5000f);
                this.hullDmg -= damage;

                if (this.hullDmg <= 0) {
                    explodeMachine();
                    return;
                }
            } else {
                if (this.heatLevel < (MAX_HEAT / 2) && this.hullDmg < 100) {
                    if (getOffsetTimer() % 100 == 0) {
                        this.hullDmg++;
                    }
                }
            }
        }
    }

    private void explodeMachine() {
        net.minecraft.world.level.Level level = getLevel();
        if (level != null && !level.isClientSide) {
            var pos = getPos();
            level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15.0F, net.minecraft.world.level.Level.ExplosionInteraction.BLOCK);
        }
    }

    private float getFluidCoolingPower(FluidStack fluidStack) {
        String fluidKey = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).toString();

        switch (fluidKey) {
            case "minecraft:water": return 1.0f;
            case "gtceu:distilled_water": return 5.0f;
            case "gtceu:sodium_coolant": return 25.0f;
            case "gtceu:bose_einstein_condensate": return 1000.0f;
            default: return 0.0f;
        }
    }

    private FluidStack getHeatedCoolant(FluidStack inputCoolant) {
        String fluidKey = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(inputCoolant.getFluid()).toString();
        net.minecraft.world.level.material.Fluid outputFluid = null;

        if (fluidKey.equals("minecraft:water") || fluidKey.equals("gtceu:distilled_water")) {
            outputFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtceu:steam"));
        }

        if (fluidKey.equals("gtceu:sodium_coolant")) {
            outputFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtceu:superheated_sodium"));
        }

        if (fluidKey.equals("gtceu:bose_einstein_condensate")) {
            outputFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("gtceu:superheated_bose_einstein_condensate"));
        }

        if (outputFluid != null && outputFluid != Fluids.EMPTY) {
            return new FluidStack(outputFluid, 1);
        }
        return null;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        assert recipe != null;
        this.recipeTemp = recipe.data.getInt("heatgen");
        if(getInsertionPercent() >= 100.0F){
            return false;
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();

        if (widget instanceof WidgetGroup group) {

            WidgetGroup buttonGroup = new WidgetGroup(-70, 140, 60, 14);

            LabelWidget percentLabel = new LabelWidget(36, 3, () -> String.valueOf(Component.literal(Math.round(getInsertionPercent()) + "%").withStyle(ChatFormatting.BLACK))) {
                @Override
                public void updateScreen() {
                    super.updateScreen();
                    this.setComponent(Component.literal(Math.round(getInsertionPercent()) + "%").withStyle(ChatFormatting.BLACK));
                    if(Math.round(getInsertionPercent()) == 100){
                        setSelfPosition(36, 3);
                    } else if(Math.round(getInsertionPercent()) < 10) {
                        setSelfPosition(42, 3);
                    } else{
                        setSelfPosition(40, 3);
                    }
                }
            };

            percentLabel.setDropShadow(false);

            ButtonWidget interactButton = new ButtonWidget(32, -8, 31, 28, ResourceBorderTexture.BORDERED_BACKGROUND, click -> {}) {
                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (this.isMouseOverElement(mouseX, mouseY)) {
                        boolean isShift = false;
                        try {
                            isShift = net.minecraft.client.gui.screens.Screen.hasShiftDown();
                        } catch (Throwable ignored) {}

                        boolean finalIsShift = isShift;
                        writeClientAction(1, writer -> {
                            writer.writeInt(button);
                            writer.writeBoolean(finalIsShift);
                        });

                        float current = getInsertionPercent();
                        float delta = isShift ? 10f : 1f;

                        if (button == 0) {
                            setInsertionPercent(current + delta);
                        } else if (button == 1) {
                            setInsertionPercent(current - delta);
                        }

                        playButtonClickSound();
                        return true;
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                @Override
                public void handleClientAction(int id, FriendlyByteBuf buffer) {
                    if (id == 1) {
                        int button = buffer.readInt();
                        boolean isShift = buffer.readBoolean();

                        float current = getInsertionPercent();
                        float delta = isShift ? 10f : 1f;

                        if (button == 0) {
                            setInsertionPercent(current + delta);
                        } else if (button == 1) {
                            setInsertionPercent(current - delta);
                        }
                    }
                    super.handleClientAction(id, buffer);
                }
            };

            interactButton.appendHoverTooltips(Component.literal("Control Rod Insertion Percentage Value").withStyle(ChatFormatting.GOLD));
            interactButton.appendHoverTooltips(Component.literal("Left Click: +1% | Shift + Left Click: +10%"));
            interactButton.appendHoverTooltips(Component.literal("Right Click: -1% | Shift + Right Click: -10%"));

            buttonGroup.addWidget(interactButton);
            buttonGroup.addWidget(percentLabel);

            group.addWidget(buttonGroup);
        }
        return widget;
    }

    private float getInsertionPercent() {
        float percent = 100f - ((this.controlRodInsertion - 0.1f) / 5.2f) * 100f;
        return Math.max(0f, Math.min(100f, percent));
    }

    private void setInsertionPercent(float percent) {
        float clampedPercent = Math.max(0f, Math.min(100f, percent));
        this.controlRodInsertion = 0.1f + ((100f - clampedPercent) / 100f) * 5.2f;
    }

    public static MachineDefinition FISSIONREACTOR = REGISTRATE
            .multiblock("fissionreactor", FissionReactor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.FISSION_REACTION)
            .recipeModifier(GregEModifiers::fissionBoost)
            .appearanceBlock(CASING_TUNGSTENSTEEL_TURBINE)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("ggggg", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "hhhhh")
                        .aisle("gaaag", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "heeeh")
                        .aisle("gaaag", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "heeeh")
                        .aisle("gaaag", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "cdddc", "heeeh")
                        .aisle("ggfgg", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "accca", "hhhhh")
                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing"))))
                        .where("b", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:ptfe_pipe_casing"))))
                        .where("c", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:laminated_glass"))))
                        .where("d", Predicates.any())
                        .where("e", Predicates.blocks(ModBlocks.NOZZLE.get()))
                        .where('f', Predicates.controller(blocks(definition.getBlock())))
                        .where('g', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(CoolantInputPartMachine.COOLANT_INPUT).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where('h', Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:tungstensteel_turbine_casing")))
                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(2))
                                .or(Predicates.abilities(CoolantOutputPartMachine.COOLANT_OUTPUT).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/mechanic/machine_casing_turbine_tungstensteel"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createFissionRodRender)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Nuclear Fission").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Open the controller GUI and locate the button with a percentage value on the left side, which indicates how deeply the control rods are inserted into the fuel rods. " +
                    "Higher percentage means the rods are pushed further in, which slows down the fission process, while a lower percentage retracts them, causing your multiblock to run faster." +
                    " Control rods have to be retracted by at least 1% to start the craft.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("TLDR: lower percentage = better.").withStyle(ChatFormatting.RED))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("You also need to cool the multiblock while its working. Heat is generated every 5 ticks with this formula:  ").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("(recipeTemperature * rodsWithdrawalPercent * 3.334) * 2 (if rodsInsertedPercent < 70%) or * 4 (if rodsInsertedPercent < 40%).").withStyle(s -> s.withColor(0xfff000)))
            .tooltips(Component.literal("Coolants have different cooling power values, these are written below. Heat removed is calculated every 5 ticks, max inputed coolant is 100mb, and the formula looks like this: ").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("removedHeat = coolantConsumed * coolingPower.").withStyle(s -> s.withColor(0xfff000)))
            .tooltips(Component.literal("You need to keep the Coolant Output Hatch empty or heat will not dissipate. " +
                    "Failure in not providing coolant results in the hull being damaged, and eventual blow-up.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("TLDR: If recipe has 100K temperature, use Water to safely cool to 70%, then Distilled Water to 40%, then use Sodium Coolant.").withStyle(ChatFormatting.RED))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Available coolants:").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Water, 1 Cooling Power").withStyle(style -> style.withColor(0xBF40BF)))
            .tooltips(Component.literal("Distilled Water, 5 Cooling Power").withStyle(style -> style.withColor(0xBF40BF)))
            .tooltips(Component.literal("Sodium Coolant, 25 Cooling Power").withStyle(style -> style.withColor(0xBF40BF)))
            .tooltips(Component.literal("Bose-Einstein Condensate, 1000 Cooling Power").withStyle(style -> style.withColor(0xBF40BF)))
            .register();

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        if (isFormed()) {
            textList.add(Component.literal("Reactor Hull Health: " + this.hullDmg + "% / 100%").withStyle(ChatFormatting.AQUA));
            textList.add(Component.literal("Reactor Hull Heat: " + (int) this.heatLevel + "K / 5000K").withStyle(ChatFormatting.AQUA));
        }
        super.addDisplayText(textList);
    }

    public static void init() {}
}