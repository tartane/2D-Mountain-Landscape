package com.mountainlandscape.live_wallpaper;

public enum ELiveWallpaperType {
    None(0),
    SnowFall(1),
    SlideShow(2);

    private int id; // Could be other data type besides int
    ELiveWallpaperType(int id) {
        this.id = id;
    }

    public static ELiveWallpaperType fromId(int id) {
        for (ELiveWallpaperType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }
}
