package com.mountainlandscape.interfaces;

public interface CanvasOptionsObserver {
    void onCanvasOptionsUpdated(boolean layerUpdated, boolean isPreset);
    void onLayerOffsetUpdated(boolean isPreset);
}
