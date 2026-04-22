package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTAuthors;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.recipe.metadata.SpatialAnomalyTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.SimpleShutDownReason;
import gregtech.common.blocks.BlockCasings12;
import gregtech.common.gui.modularui.multiblock.MTESpatialAnomalyContainmentChamberGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class MTESpatialAnomalyContainmentChamber
    extends MTEExtendedPowerMultiBlockBase<MTESpatialAnomalyContainmentChamber> implements ISurvivalConstructable {

    private static Textures.BlockIcons.CustomIcon ScreenON;
    private static Textures.BlockIcons.CustomIcon ScreenOFF;
    private boolean anomalyActive = false;
    private int numberOfFoci;
    private ItemStack catalyst;
    private MTEHatchInput stabilizerHatch;
    private final FluidStack stabilizer = Materials.Vyroxeres.getMolten(1);
    private byte anomalyTier = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<MTESpatialAnomalyContainmentChamber> STRUCTURE_DEFINITION = StructureDefinition
        .<MTESpatialAnomalyContainmentChamber>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            // spotless:off
            new String[][]{
                { "  B    ", " BB  B ", "B BBBBB", "  B~B  ", "BBBBB B", " B  BB ", "    B  " },
                { " BB  B ", "BAAAAAB", "BA   A ", " A   A ", " A   AB", "BAAAAAB", " B  BB " },
                { "  BBBBB", " A   AB", "B     B", "B     B", "B     B", "BA   A ", "BBBBB  " },
                { "  BCB  ", " A   A ", "B     B", "C     C", "B     B", " A   A ", "  BCB  " },
                { "BBBBB  ", "BA   A ", "B     B", "B     B", "B     B", " A   AB", "  BBBBB" },
                { " B  BB ", "BAAAAAB", " A   AB", " A   A ", "BA   A ", "BAAAAAB", " BB  B " },
                { "    B  ", " B  BB ", "BBBBB B", "  BDB  ", "B BBBBB", " BB  B ", "  B    " } })
        // spotless:on
        .addElement(
            'C',
            buildHatchAdder(MTESpatialAnomalyContainmentChamber.class).atLeast(InputBus, OutputBus, InputHatch)
                .casingIndex(((BlockCasings12) GregTechAPI.sBlockCasings12).getTextureIndex(5))
                .hint(1)
                .buildAndChain(
                    onElementPass(
                        MTESpatialAnomalyContainmentChamber::onCasingAdded,
                        ofBlock(GregTechAPI.sBlockCasings12, 5))))
        .addElement('A', ofBlock(GregTechAPI.sBlockGlass1, 9))
        .addElement('B', ofBlock(GregTechAPI.sBlockCasings12, 5))
        .addElement(
            'D',
            buildHatchAdder(MTESpatialAnomalyContainmentChamber.class).atLeast(InputHatch)
                .adder(MTESpatialAnomalyContainmentChamber::addStabilizerHatch)
                .casingIndex(((BlockCasings12) GregTechAPI.sBlockCasings12).getTextureIndex(5))
                .hint(2)
                .build())
        .build();

    public MTESpatialAnomalyContainmentChamber(final int aID, final String aName, final String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MTESpatialAnomalyContainmentChamber(String aName) {
        super(aName);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (catalyst != null) {
            NBTTagCompound catalystTag = new NBTTagCompound();
            catalyst.writeToNBT(catalystTag);
            aNBT.setTag("Catalyst", catalystTag);
        }
        aNBT.setBoolean("AnomalyActive", anomalyActive);
        aNBT.setByte("AnomalyTier", anomalyTier);
        aNBT.setInteger("FociCount", numberOfFoci);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("Catalyst")) {
            this.catalyst = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("Catalyst"));
        }
        this.anomalyActive = aNBT.getBoolean("AnomalyActive");
        this.anomalyTier = aNBT.getByte("AnomalyTier");
        this.numberOfFoci = aNBT.getInteger("FociCount");
    }

    @Override
    public IStructureDefinition<MTESpatialAnomalyContainmentChamber> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTESpatialAnomalyContainmentChamber(this.mName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenON = new Textures.BlockIcons.CustomIcon("iconsets/GODFORGE_MODULE_ACTIVE");
        ScreenOFF = new Textures.BlockIcons.CustomIcon("iconsets/SCREEN_OFF");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings12, 5)),
                    TextureFactory.builder()
                        .addIcon(ScreenON)
                        .extFacing()
                        .build(),
                    TextureFactory.builder()
                        .addIcon(ScreenON)
                        .extFacing()
                        .glow()
                        .build() };
            } else {
                return new ITexture[] {
                    Textures.BlockIcons
                        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings12, 5)),
                    TextureFactory.builder()
                        .addIcon(ScreenOFF)
                        .extFacing()
                        .build() };
            }
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings12, 5)) };
    }

    private boolean addStabilizerHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof MTEHatchInput hatch) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            stabilizerHatch = hatch;
            return true;
        }
        return false;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Spatial Anomaly Containment Chamber")
            .addInfo("Processes materials within a spatial anomaly field")
            .addInfo("Place a catalyst in the controller slot and activate the anomaly")
            .addInfo("Tier 1: Laser Lens (Special) - Spatial Anomaly")
            .addInfo("Tier 2: Fractal Seed - Fractal Rift")
            .addInfo("Tier 3: Energised Tesseract - Abnormality Field")
            .addInfo("Parallels = 2 * catalyst stack size (max 16)")
            .addInfo("Requires Molten Vyroxeres as stabilizer fluid")
            .beginStructureBlock(7, 7, 7, false)
            .addController("Front Center")
            .addCasingInfoMin("Anomaly Breach Containment Casing", 100, false)
            .addCasingInfoExactly("Fractality Constraining Glass", 36, false)
            .addInputBus("Any Casing (dot 1)", 1)
            .addOutputBus("Any Casing (dot 1)", 1)
            .addInputHatch("Any Casing (dot 1) / Stabilizer Hatch (dot 2)", 1)
            .toolTipFinisher(GTAuthors.AuthorNoc.get());
        return tt;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 3, 3, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 3, 3, 0, elementBudget, env, false, true);
    }

    private int mCasingAmount;

    private void onCasingAdded() {
        mCasingAmount++;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        stabilizerHatch = null;
        return checkPiece(STRUCTURE_PIECE_MAIN, 3, 3, 0) && stabilizerHatch != null;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);

        if (!aBaseMetaTileEntity.isServerSide()) return;

        // Skip on first seconds of server to avoid crashing on restart
        if ((!anomalyActive || aTick % 20 != 0) || (aTick < 100)) return;

        if (drain(stabilizerHatch, stabilizer, false)) {
            drain(stabilizerHatch, stabilizer, true);
        } else {
            anomalyTier = 0;
            anomalyActive = false;
            if (catalyst != null) {
                catalyst.stackSize = Math.round((float) (numberOfFoci / 2));
                if (catalyst.stackSize > 0) {
                    mInventory[getControllerSlotIndex()] = catalyst;
                }
                catalyst = null;
            }
            numberOfFoci = 0;
        }
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (!anomalyActive) {
            stopMachine(SimpleShutDownReason.ofCritical("anomaly_unstable"));
            return false;
        }
        return super.onRunningTick(aStack);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(0L);
        logic.setAvailableAmperage(1L);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                maxParallel = 2 * numberOfFoci;

                int requiredRecipeTier = recipe.getMetadataOrDefault(SpatialAnomalyTierKey.INSTANCE, 0);
                if (requiredRecipeTier > anomalyTier) {
                    return switch (requiredRecipeTier) {
                        case 1 -> SimpleCheckRecipeResult.ofFailure("no_anomaly.0");
                        case 2 -> SimpleCheckRecipeResult.ofFailure("no_anomaly.1");
                        case 3 -> SimpleCheckRecipeResult.ofFailure("no_anomaly.2");
                        default -> CheckRecipeResultRegistry.NO_RECIPE;
                    };
                }

                if (anomalyTier == 2) {
                    List<FluidStack> fluidInputs = getStoredFluids();
                    ItemStack[] itemInputs = inputItems;
                    boolean foundHydrance = false;
                    @Nullable
                    Fluid foundConcept = null;
                    int foundHydranceMeta = -1;

                    int hydrance1meta = ItemList.HolographicInfinity.get(1)
                        .getItemDamage();
                    int hydrance2meta = ItemList.HyperbolicInfinity.get(1)
                        .getItemDamage();
                    int hydrance3meta = ItemList.HarmonicInfinity.get(1)
                        .getItemDamage();
                    Fluid concept1 = Materials.Cardinality.mFluid;
                    Fluid concept2 = Materials.Causality.mFluid;
                    Fluid concept3 = Materials.Chirality.mFluid;

                    for (ItemStack aItem : itemInputs != null ? itemInputs : new ItemStack[0]) {
                        int meta = aItem.getItemDamage();
                        if (meta == foundHydranceMeta) continue;
                        if (meta == hydrance1meta || meta == hydrance2meta || meta == hydrance3meta) {
                            if (foundHydrance) {
                                return SimpleCheckRecipeResult.ofFailure("anomaly_conflict.0");
                            }
                            foundHydrance = true;
                            foundHydranceMeta = meta;
                        }
                    }
                    for (FluidStack aFluid : fluidInputs) {
                        Fluid fluid = aFluid.getFluid();
                        if (fluid == foundConcept) continue;
                        if ((fluid.equals(concept1) || fluid.equals(concept2) || fluid.equals(concept3))
                            && foundConcept != fluid) {
                            if (foundConcept != null) {
                                return SimpleCheckRecipeResult.ofFailure("anomaly_conflict.1");
                            }
                            foundConcept = fluid;
                        }
                    }
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    /** Toggles the anomaly on/off. Called from the GUI button. */
    public void toggleAnomaly() {
        ItemStack controllerStack = this.getControllerSlot();
        if (controllerStack != null && !anomalyActive) {
            if (controllerStack.isItemEqual(GregtechItemList.Laser_Lens_Special.get(1))) {
                anomalyTier = 1;
            } else if (controllerStack.isItemEqual(ItemList.FractalSeed.get(1))) {
                anomalyTier = 2;
            } else if (controllerStack.isItemEqual(ItemList.EnergisedTesseract.get(1))) {
                anomalyTier = 3;
            }
            if (anomalyTier != 0) {
                numberOfFoci = controllerStack.stackSize;
                catalyst = controllerStack.copy();
                mInventory[getControllerSlotIndex()] = null;
                anomalyActive = true;
            }
        } else if (controllerStack == null && anomalyActive) {
            anomalyTier = 0;
            numberOfFoci = 0;
            anomalyActive = false;
            if (catalyst != null) {
                mInventory[getControllerSlotIndex()] = catalyst;
                catalyst = null;
            }
        }
    }

    public boolean isAnomalyActive() {
        return anomalyActive;
    }

    public void setAnomalyActive(boolean active) {
        this.anomalyActive = active;
    }

    public int getAnomalyTier() {
        return anomalyTier;
    }

    public void setAnomalyTier(int tier) {
        this.anomalyTier = (byte) tier;
    }

    public int getFociCount() {
        return numberOfFoci;
    }

    public String getAnomalyName() {
        return switch (anomalyTier) {
            case 1 -> "Spatial Anomaly";
            case 2 -> "Fractal Rift";
            case 3 -> "Abnormality Field";
            default -> "None";
        };
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new MTESpatialAnomalyContainmentChamberGui(this);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 16;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.SpatialAnomalyRecipes;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }
}
