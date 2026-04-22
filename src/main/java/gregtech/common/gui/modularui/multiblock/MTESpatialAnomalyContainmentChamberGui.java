package gregtech.common.gui.modularui.multiblock;

import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.multi.MTESpatialAnomalyContainmentChamber;

public class MTESpatialAnomalyContainmentChamberGui extends MTEMultiBlockBaseGui<MTESpatialAnomalyContainmentChamber> {

    public MTESpatialAnomalyContainmentChamberGui(MTESpatialAnomalyContainmentChamber multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            "anomalyActive",
            new BooleanSyncValue(multiblock::isAnomalyActive, val -> multiblock.toggleAnomaly()));
        syncManager.syncValue("anomalyTier", new IntSyncValue(multiblock::getAnomalyTier));
        syncManager.syncValue("fociCount", new IntSyncValue(multiblock::getFociCount));
        syncManager.syncValue("anomalyName", new StringSyncValue(multiblock::getAnomalyName));
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createButtonColumn(panel, syncManager).child(createAnomalyButton(syncManager));
    }

    protected IWidget createAnomalyButton(PanelSyncManager syncManager) {
        BooleanSyncValue activeSyncer = syncManager.findSyncHandler("anomalyActive", BooleanSyncValue.class);
        return new ButtonWidget<>().size(18)
            .marginBottom(2)
            .tooltip(
                t -> t.addLine(translateToLocal("GT5U.SACC.anomalybutton"))
                    .addLine(EnumChatFormatting.GRAY + translateToLocal("GT5U.SACC.anomalybuttontooltip.0"))
                    .addLine(EnumChatFormatting.GRAY + translateToLocal("GT5U.SACC.anomalybuttontooltip.1")))
            .overlay(new DynamicDrawable(() -> {
                if (activeSyncer.getBoolValue()) {
                    return GTGuiTextures.TT_SAFE_VOID_ON;
                }
                return GTGuiTextures.TT_SAFE_VOID_OFF;
            }))
            .onMousePressed(mouseButton -> {
                if (mouseButton == 0) {
                    activeSyncer.setValue(!activeSyncer.getBoolValue());
                }
                return true;
            });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected com.cleanroommc.modularui.widgets.ListWidget<IWidget, ?> createTerminalTextWidget(
        PanelSyncManager syncManager, ModularPanel parent) {
        var baseWidget = super.createTerminalTextWidget(syncManager, parent);

        StringSyncValue nameSyncer = syncManager.findSyncHandler("anomalyName", StringSyncValue.class);
        IntSyncValue fociSyncer = syncManager.findSyncHandler("fociCount", IntSyncValue.class);

        baseWidget.child(
            new TextWidget<>(
                IKey.dynamic(
                    () -> EnumChatFormatting.AQUA + "Anomaly: "
                        + EnumChatFormatting.WHITE
                        + nameSyncer.getStringValue())).widthRel(1)
                            .alignment(Alignment.CenterLeft)
                            .marginBottom(2));

        baseWidget.child(
            new TextWidget<>(
                IKey.dynamic(
                    () -> EnumChatFormatting.AQUA + "Parallels: "
                        + EnumChatFormatting.WHITE
                        + (2 * fociSyncer.getIntValue()))).widthRel(1)
                            .alignment(Alignment.CenterLeft)
                            .marginBottom(2));

        return baseWidget;
    }
}
