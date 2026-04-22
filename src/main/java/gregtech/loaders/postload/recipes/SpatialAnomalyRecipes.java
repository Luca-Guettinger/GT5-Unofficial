package gregtech.loaders.postload.recipes;

import static gregtech.api.recipe.RecipeMaps.SpatialAnomalyRecipes;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.metadata.SpatialAnomalyTierKey;

public class SpatialAnomalyRecipes implements Runnable {

    private static final SpatialAnomalyTierKey ANOMALY_TYPE = SpatialAnomalyTierKey.INSTANCE;

    @Override
    public void run() {

        // T1: Spatial Anomaly - produce Fractal Anomaly
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.WeakInfinityCatalyst.get(11), ItemList.CrystalCube.get(1))
            .itemOutputs(ItemList.FractalAnomaly.get(1))
            .duration(5 * SECONDS)
            .metadata(ANOMALY_TYPE, 1)
            .eut(0)
            .addTo(SpatialAnomalyRecipes);

        // T2: Fractal Rift - produce Entangled Singularity from Harmonic Infinity
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.HarmonicInfinity.get(1))
            .fluidInputs(Materials.Causality.getFluid(1))
            .itemOutputs(ItemList.EntangledSingularity.get(1))
            .duration(5 * SECONDS)
            .metadata(ANOMALY_TYPE, 2)
            .eut(0)
            .addTo(SpatialAnomalyRecipes);

        // T2: Fractal Rift - produce Entangled Singularity from Hyperbolic Infinity
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.HyperbolicInfinity.get(1))
            .fluidInputs(Materials.Causality.getFluid(1))
            .itemOutputs(ItemList.EntangledSingularity.get(1))
            .duration(5 * SECONDS)
            .metadata(ANOMALY_TYPE, 2)
            .eut(0)
            .addTo(SpatialAnomalyRecipes);
    }
}
