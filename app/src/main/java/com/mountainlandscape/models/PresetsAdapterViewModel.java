package com.mountainlandscape.models;

import com.mountainlandscape.adapters.PresetsAdapter;

public class PresetsAdapterViewModel {
    private int itemType;
    private CanvasOptions preset;
    private String headerTitle;

    public PresetsAdapterViewModel(CanvasOptions preset, int type) {
        this.preset = preset;
        this.itemType = type;
    }

    public PresetsAdapterViewModel(String headerTitle) {
        this.headerTitle = headerTitle;
        this.itemType = PresetsAdapter.TYPE_HEADER;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public CanvasOptions getPreset() {
        return preset;
    }

    public void setPreset(CanvasOptions preset) {
        this.preset = preset;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }
}
