package com.mountainlandscape.models;

import java.io.Serializable;

public class Layer implements Serializable {
    private int resourceId;
    private int shadowResourceId;
    private String layerName;
    private int scrollX;

    public Layer() {
    }

    public Layer(String layerName, int resourceId, int shadowResourceId, int scrollX) {
        this.resourceId = resourceId;
        this.shadowResourceId = shadowResourceId;
        this.layerName = layerName;
        this.scrollX = scrollX;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getShadowResourceId() {
        return shadowResourceId;
    }

    public void setShadowResourceId(int shadowResourceId) {
        this.shadowResourceId = shadowResourceId;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public int getScrollX() {
        return scrollX;
    }

    public void setScrollX(int scrollX) {
        this.scrollX = scrollX;
    }
}
