package net.cu5tmtp.GregECore.gregstuff.GregMachines.machines.misc;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.parts.misc.PressurePartMachine;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.renderer.renderRegistries.GregERenederRegistries;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEModifiers;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregEPredicates;
import net.cu5tmtp.GregECore.gregstuff.GregUtils.notCoreStuff.GregERecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static net.cu5tmtp.GregECore.gregstuff.GregUtils.GregECore.REGISTRATE;

public class PressureChamber extends WorkableElectricMultiblockMachine implements IRedstoneSignalMachine {

    private IFluidHandler pressureInputHandler;
    private TickableSubscription pressureSubscription;
    @Persisted
    private double currentPressure = 0.0;
    @Persisted
    public boolean safeMode = true;
    private double maxPressure;
    private boolean leakage = false;
    private double recipePa;

    public PressureChamber(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PressureChamber.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IFluidHandler> coolantContainers = new ArrayList<>();

        for (IMultiPart part : getParts()) {

            if(!(part instanceof PressurePartMachine)){
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
        this.pressureInputHandler = new FluidHandlerList(coolantContainers);

        if (pressureSubscription != null) {
            pressureSubscription.unsubscribe();
            pressureSubscription = null;
        }

        this.maxPressure = GregEPredicates.getMaxGlassPressure(getMultiblockState().getMatchContext());

        pressureSubscription = this.subscribeServerTick(this::pressureChecker);
    }

    @Override
    public void onStructureInvalid() {
        if (pressureSubscription != null) {
            pressureSubscription.unsubscribe();
            pressureSubscription = null;
        }
        this.maxPressure = 0;
        super.onStructureInvalid();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        assert recipe != null;
        recipePa = recipe.data.getDouble("pa");

        if(recipePa > currentPressure){
            return false;
        }

        return super.beforeWorking(recipe);
    }

    private void pressureChecker() {
        if (getOffsetTimer() % 20 != 0) {
            return;
        }

        if (this.pressureInputHandler == null) {
            return;
        }

        FluidStack steamStack = GTMaterials.Steam.getFluid(1000);

        FluidStack drained = this.pressureInputHandler.drain(steamStack, IFluidHandler.FluidAction.SIMULATE);

        if (!drained.isEmpty() && drained.getAmount() >= 1000) {
            this.pressureInputHandler.drain(steamStack, IFluidHandler.FluidAction.EXECUTE);
            this.currentPressure += 1.0;
        } else {
            this.currentPressure = Math.max(0.0, this.currentPressure - 0.1);
        }

        if(leakage){
            this.currentPressure -= Math.max(0.0, this.currentPressure - 1);
        }

        if (this.currentPressure > maxPressure) {
            if (!this.safeMode) {
                if (this.getLevel() != null) {
                    this.explodeMachine();
                }
                this.currentPressure = 0.0;
            } else {
                this.currentPressure = maxPressure;
            }
        }

        this.updateSignal();
    }

    private void explodeMachine() {
        Level level = getLevel();
        if (level != null && !level.isClientSide) {
            var pos = getPos();
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

            int radius = 3;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + y * y + z * z <= radius * radius) {
                            BlockPos targetPos = pos.offset(x, y, z);
                            BlockState state = level.getBlockState(targetPos);

                            if (!state.isAir() && state.getDestroySpeed(level, targetPos) >= 0) {
                                level.destroyBlock(targetPos, false);
                            }
                        }
                    }
                }
            }

            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15.0F, Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        return Math.min(15, (int) (this.currentPressure / 7.0));
    }

    @Override
    public int getAnalogOutputSignal() {
        return getOutputSignal(null);
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        return true;
    }

    @Override
    public Widget createUIWidget() {
        Widget widget = super.createUIWidget();

        if (widget instanceof WidgetGroup group) {
            WidgetGroup buttonGroup = new WidgetGroup(-70, 140, 60, 14);

            LabelWidget safeModeLabel = new LabelWidget(12, 3, () -> String.valueOf(Component.literal(safeMode ? "Safe: ON" : "Safe: OFF").withStyle(safeMode ? ChatFormatting.GREEN : ChatFormatting.RED))) {
                @Override
                public void updateScreen() {
                    super.updateScreen();
                    this.setComponent(Component.literal(safeMode ? "Safe:  ON" : "Safe: OFF").withStyle(safeMode ? ChatFormatting.GREEN : ChatFormatting.RED));
                }
            };
            safeModeLabel.setDropShadow(false);

            ButtonWidget interactButton = new ButtonWidget(8, -8, 55, 28, ResourceBorderTexture.BORDERED_BACKGROUND, click -> {}) {
                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (this.isMouseOverElement(mouseX, mouseY)) {
                        boolean newState = !safeMode;

                        writeClientAction(1, writer -> {
                            writer.writeBoolean(newState);
                        });

                        safeMode = newState;
                        playButtonClickSound();
                        return true;
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                @Override
                public void handleClientAction(int id, FriendlyByteBuf buffer) {
                    if (id == 1) {
                        safeMode = buffer.readBoolean();
                    }
                    super.handleClientAction(id, buffer);
                }
            };

            interactButton.appendHoverTooltips(Component.literal("Toggle Safe Mode").withStyle(ChatFormatting.GOLD));
            interactButton.appendHoverTooltips(Component.literal("Prevents explosion when pressure exceeds 100 Pa."));

            buttonGroup.addWidget(interactButton);
            buttonGroup.addWidget(safeModeLabel);

            group.addWidget(buttonGroup);
        }
        return widget;
    }

    public static MachineDefinition PRESSURECHAMBER = REGISTRATE
            .multiblock("pressurechamber", PressureChamber::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GregERecipeTypes.PRESSURECHAMCRAFT)
            .recipeModifiers(GregEModifiers::presureChamberBoost)
            .appearanceBlock(GCYMBlocks.CASING_ATOMIC)
            .pattern(definition -> {
                return FactoryBlockPattern.start()
                        .aisle("aaaa", "abba", "abba", "aaaa")
                        .aisle("aaaa", "bccb", "bccb", "aaaa")
                        .aisle("aaaa", "bccb", "bccb", "aaaa")
                        .aisle("haaa", "abba", "abba", "aaaa")
                        .where("a", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("gtceu:solid_machine_casing")))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.abilities(PressurePartMachine.getPartAbility()).setMaxGlobalLimited(1).setPreviewCount(1)))
                        .where("b", GregEPredicates.pressureGlass())
                        .where("c", Predicates.any())
                        .where('h', Predicates.controller(blocks(definition.getBlock())))
                        .build();
            })
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/assembly_line"))
                    .andThen(b -> b.addDynamicRenderer(GregERenederRegistries::createPressureChamberRender))
            )
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Abilities: Pressure Compacting").withStyle(style -> style.withColor(0xFFD700)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Provides you with the power to compact different materials very tightly together. But be careful, as the glasses might not be able to withstand the pressures.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Provide a Pressure Input with steam and the pressure inside the machine will start increasing with the rate of 1Pa per 1000mb of steam every second. Different glasses can withstand different amount of pressure. Do not mix them, as that will create leaks and the machine will not be able to work. You can also turn off the safe mode, which will result in halving the recipe times. But the machine can explode as consequence, if the pressure is too big.").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("----------------------------------------").withStyle(s -> s.withColor(0xff0000)))
            .tooltips(Component.literal("Glasses:").withStyle(ChatFormatting.LIGHT_PURPLE))
            .tooltips(Component.literal("Tempered Glass: 30Pa").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Laminated Glass: 60Pa").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Fusion Glass: 100Pa").withStyle(style -> style.withColor(0x90EE90)))
            .tooltips(Component.literal("Controller sends out redstone signal, with every 7Pa adds 1 redstone strength.").withStyle(ChatFormatting.RED))

            .register();

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            textList.add(Component.literal("Pressure: " + (int) currentPressure + "Pa / " + (int) maxPressure + "Pa").withStyle(ChatFormatting.AQUA));
            textList.add(Component.literal("Leakage: " + leakage).withStyle(ChatFormatting.RED));
            textList.add(Component.literal("Redstone Power: " + getOutputSignal(null)).withStyle(ChatFormatting.RED));

        }
    }

    public boolean isSafeMode() {
        return safeMode;
    }

    public static void init() {
    }
}