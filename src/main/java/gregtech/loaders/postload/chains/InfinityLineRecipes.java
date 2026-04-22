package gregtech.loaders.postload.chains;

import static bartworks.API.recipe.BartWorksRecipeMaps.electricImplosionCompressorRecipes;
import static gregtech.api.enums.Mods.EternalSingularity;
import static gregtech.api.enums.Mods.NewHorizonsCoreMod;
import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.recipe.RecipeMaps.autoclaveRecipes;
import static gregtech.api.recipe.RecipeMaps.blastFurnaceRecipes;
import static gregtech.api.recipe.RecipeMaps.cannerRecipes;
import static gregtech.api.recipe.RecipeMaps.chemicalBathRecipes;
import static gregtech.api.recipe.RecipeMaps.compressorRecipes;
import static gregtech.api.recipe.RecipeMaps.distillationTowerRecipes;
import static gregtech.api.recipe.RecipeMaps.formingPressRecipes;
import static gregtech.api.recipe.RecipeMaps.fusionRecipes;
import static gregtech.api.recipe.RecipeMaps.hammerRecipes;
import static gregtech.api.recipe.RecipeMaps.laserEngraverRecipes;
import static gregtech.api.recipe.RecipeMaps.neutroniumCompressorRecipes;
import static gregtech.api.recipe.RecipeMaps.polarizerRecipes;
import static gregtech.api.recipe.RecipeMaps.vacuumFreezerRecipes;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;
import static gregtech.api.util.GTRecipeConstants.FUSION_THRESHOLD;
import static gtPlusPlus.api.recipe.GTPPRecipeMaps.vacuumFurnaceRecipes;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsElements;

public class InfinityLineRecipes {

    public static void run() {

        // ============================================
        // Taranium Draining Line
        // ============================================

        // Vyroxeres Canvas
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Vyroxeres, 1),
                getModItem(NewHorizonsCoreMod.ID, "LapotronDust", 64),
                getModItem(NewHorizonsCoreMod.ID, "LapotronDust", 64),
                getModItem(NewHorizonsCoreMod.ID, "LapotronDust", 64),
                getModItem(NewHorizonsCoreMod.ID, "LapotronDust", 64),
                getModItem(NewHorizonsCoreMod.ID, "LapotronDust", 64))
            .fluidInputs(Materials.ElectrumFlux.getMolten(576))
            .itemOutputs(ItemList.VyroxeresCanvas.get(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(vacuumFurnaceRecipes);

        // Inert Taranium (laser engraver)
        // TODO: Infinity Rework - replace Naquadria with Taranium once the material exists
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Naquadria, 64),
                ItemList.VyroxeresCanvas.get(1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Force, 0, false),
                getModItem(NewHorizonsCoreMod.ID, "MysteriousCrystalLens", 0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.InertTaranium, 64))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(laserEngraverRecipes);

        // Highly Stable Taranium (chemical bath with Neutronium)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.InertTaranium, 16))
            .fluidInputs(Materials.Neutronium.getMolten(576))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.HighlyStableTaranium, 16))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(chemicalBathRecipes);

        // Highly Reactive Taranium (chemical bath with Draconium Awakened)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.InertTaranium, 16))
            .fluidInputs(Materials.DraconiumAwakened.getMolten(576))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.HighlyReactiveTaranium, 16))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(chemicalBathRecipes);

        // ============================================
        // UHV Infinity Recipe
        // ============================================

        // Weak Infinity Catalyst (neutronium compressor)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfinityCatalyst, 64))
            .itemOutputs(ItemList.WeakInfinityCatalyst.get(1))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(neutroniumCompressorRecipes);

        // Infinity Ingot (hammer)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.CosmicNeutronium, 24),
                ItemList.FractalAnomaly.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Infinity, 1))
            .duration(10 * TICKS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(hammerRecipes);

        // ============================================
        // Non-Orientable Matter Line - Moebianite Processing
        // ============================================

        // Combined Catalyst (assembler)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Vulcanite, 64),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Ceruclase, 64),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Rubracium, 64),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Orichalcum, 64))
            .fluidInputs(MaterialMisc.ETHYL_CYANOACRYLATE.getFluidStack(64000))
            .itemOutputs(ItemList.Combined_Catalyst.get(64))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(assemblerRecipes);

        // Moebianite Slag (vacuum furnace)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Moebianite, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Moebianite, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Moebianite, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Moebianite, 64),
                ItemList.Combined_Catalyst.get(1))
            .fluidInputs(new FluidStack(MaterialsElements.getInstance().BROMINE.getPlasma(), 100))
            .fluidOutputs(Materials.MoebianiteSlag.getFluid(2500))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .metadata(COIL_HEAT, 11700)
            .addTo(vacuumFurnaceRecipes);

        // Moebianite Crystal Slurry (autoclave)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 64))
            .fluidInputs(Materials.MoebianiteSlag.getFluid(1250))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 64))
            .fluidOutputs(Materials.MoebianiteCrystalSlurry.getFluid(8000))
            .duration(150 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(autoclaveRecipes);

        // Manifold Gems + Purified Moebianite (electric implosion compressor)
        GTValues.RA.stdBuilder()
            .itemInputs(getModItem(NewHorizonsCoreMod.ID, "MysteriousCrystalGemExquisite", 32))
            .fluidInputs(Materials.MoebianiteCrystalSlurry.getFluid(64000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Manifold, 16))
            .fluidOutputs(Materials.PurifiedMoebianite.getFluid(8000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(electricImplosionCompressorRecipes);

        // Non-Orientable Matter (fusion)
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.PurifiedMoebianite.getFluid(125), Materials.Void.getMolten(144))
            .fluidOutputs(Materials.NonOrientableMatter.getFluid(1000))
            .duration(1 * SECONDS + 12 * TICKS)
            .eut(1562500)
            .metadata(FUSION_THRESHOLD, 1_000_000_000L)
            .addTo(fusionRecipes);

        // ============================================
        // Anomalous Processing - Fractal Line
        // ============================================

        // Parastable Fractal Helium (compressor)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.QuanticalDisturbance, 256))
            .fluidInputs(Materials.Helium3.getGas(4000000))
            .fluidOutputs(Materials.ParastableFractalHelium.getFluid(16000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_UV)
            .addTo(compressorRecipes);

        // Fractal Helium Bose-Einstein Condensate (vacuum freezer)
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.ParastableFractalHelium.getFluid(100000))
            .fluidOutputs(Materials.FractalHeliumBoseEinsteinCondensate.getFluid(100000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(vacuumFreezerRecipes);

        // Filled Fractal Cell (fluid canner)
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.FractalCell.get(1))
            .fluidInputs(Materials.FractalHeliumBoseEinsteinCondensate.getFluid(1000))
            .itemOutputs(ItemList.FilledFractalCell.get(1))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(cannerRecipes);

        // Transfinite Matter Cell (blast furnace, 11700K)
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.FilledFractalCell.get(1))
            .itemOutputs(ItemList.TransfiniteMatterCell.get(1))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .metadata(COIL_HEAT, 11700)
            .addTo(blastFurnaceRecipes);

        // Fractal Gem (forming press)
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.TransfiniteMatterCell.get(1),
                getModItem(EternalSingularity.ID, "eternal_singularity", 0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.gem, Materials.Fractal, 1), ItemList.FractalCell.get(1))
            .duration(42 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .addTo(formingPressRecipes);

        // Fractal Juice (polarizer)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gem, Materials.Fractal, 64))
            .fluidOutputs(Materials.FractalJuice.getFluid(9216))
            .duration(3 * SECONDS)
            .eut(TierEU.RECIPE_UXV)
            .addTo(polarizerRecipes);

        // ============================================
        // Stub Recipes (Phase 6)
        // ============================================

        // TODO: Infinity Rework - placeholder recipe for Hydrances
        // Holographic Infinity (forming press from Non-Orientable Matter + Diamond Lens)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1))
            .fluidInputs(Materials.NonOrientableMatter.getFluid(1000))
            .itemOutputs(ItemList.HolographicInfinity.get(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(formingPressRecipes);

        // TODO: Infinity Rework - placeholder recipe for Hydrances
        // Hyperbolic Infinity (forming press from Non-Orientable Matter + Ruby Lens)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.lens, Materials.Ruby, 1))
            .fluidInputs(Materials.NonOrientableMatter.getFluid(1000))
            .itemOutputs(ItemList.HyperbolicInfinity.get(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(formingPressRecipes);

        // TODO: Infinity Rework - placeholder recipe for Hydrances
        // Harmonic Infinity (forming press from Non-Orientable Matter + Emerald Lens)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.lens, Materials.Emerald, 1))
            .fluidInputs(Materials.NonOrientableMatter.getFluid(1000))
            .itemOutputs(ItemList.HarmonicInfinity.get(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(formingPressRecipes);

        // TODO: Infinity Rework - placeholder recipe for Concept fluids
        // Concept fluid distillation (distillation tower from FractalJuice)
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.FractalJuice.getFluid(3000))
            .fluidOutputs(
                Materials.Cardinality.getFluid(1000),
                Materials.Causality.getFluid(1000),
                Materials.Chirality.getFluid(1000))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(distillationTowerRecipes);

        // TODO: Infinity Rework - placeholder recipe for Fractal Seed
        // Fractal Seed (assembler)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Fractal, 4),
                ItemList.EntangledSingularity.get(1))
            .fluidInputs(Materials.NonOrientableMatter.getFluid(2000))
            .itemOutputs(ItemList.FractalSeed.get(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UIV)
            .addTo(assemblerRecipes);

        // TODO: Infinity Rework - placeholder recipe for UEV Infinity
        // UEV Infinity Ingot (forge hammer, reduced cosmic neutronium path)
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.CosmicNeutronium, 8))
            .fluidInputs(Materials.NonOrientableMatter.getFluid(4000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Infinity, 4))
            .duration(10 * TICKS)
            .eut(TierEU.RECIPE_UEV)
            .addTo(hammerRecipes);

        // TODO: Infinity Rework - placeholder recipe for Hollow Crystal Matrix Cube
        // Crystal Cube (assembler)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Diamond, 40),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 5))
            .itemOutputs(ItemList.CrystalCube.get(1))
            .duration(300 * SECONDS)
            .eut(TierEU.RECIPE_EV)
            .addTo(assemblerRecipes);
    }
}
