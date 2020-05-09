package com.mountainlandscape.models;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.mountainlandscape.R;
import com.mountainlandscape.interfaces.CanvasOptionsObserver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CanvasOptions implements Serializable {
    private boolean hasCraters;
    private int starsCount;
    private int layerOffset;
    private int startColor;
    private int endColor;
    private int backgroundColorStart;
    private int backgroundColorEnd;
    private List<Layer> layers;
    protected transient Context mContext;
    private boolean showPlanet;
    private int planetColor;
    private transient static CanvasOptions canvasOptions;
    private transient static List<CanvasOptions> savedCanvasOptions = new ArrayList<>(); //the user's presets
    private transient List<CanvasOptionsObserver> observers;
    private boolean useGradient = false;
    private boolean notifyEnabled = true;
    private String name;
    private boolean isUserPreset;
    private int starsColor;
    private final static String FILE_NAME = "canvasoptions.ser";

    public static int DEFAULT_OFFSET = 100;

    public static CanvasOptions getInstance(Context context) {
        if(canvasOptions == null)
            canvasOptions = new CanvasOptions(context);

        return canvasOptions;
    }

    public CanvasOptions(Context context) {
        mContext = context;
        observers = new ArrayList<>();

        DEFAULT_OFFSET = context.getResources().getInteger(R.integer.default_layer_offset);
    }

    public static CanvasOptions applyPreset(CanvasOptions options) {
        canvasOptions.setNotifyEnabled(false);
        canvasOptions.setBackgroundColorStart(options.getBackgroundColorStart());
        canvasOptions.setBackgroundColorEnd(options.getBackgroundColorEnd());
        List<Layer> layers = new ArrayList<>();
        for(Layer optionLayer: options.getLayers()) {
            layers.add(new Layer(optionLayer.getLayerName(), optionLayer.getResourceId(), optionLayer.getShadowResourceId(), optionLayer.getScrollX()));
        }
        canvasOptions.setLayers(layers);
        canvasOptions.setStartColor(options.getStartColor());
        canvasOptions.setEndColor(options.getEndColor());
        canvasOptions.setHasCraters(options.hasCraters());
        canvasOptions.setLayerOffset(options.getLayerOffset(), true);
        canvasOptions.setPlanetColor(options.getPlanetColor());
        canvasOptions.setShowPlanet(options.isShowPlanet());
        canvasOptions.setStarsColor(options.getStarsColor());
        canvasOptions.setStarsCount(options.getStarsCount());
        canvasOptions.setUseGradient(options.isUseGradient());
        canvasOptions.setNotifyEnabled(true);
        canvasOptions.setName(options.getName());

        canvasOptions.notifyObservers(true, true);
        return canvasOptions;
    }

    public static CanvasOptions newInstance(CanvasOptions options) {
        CanvasOptions newOptions = new CanvasOptions(options.mContext);
        newOptions.setNotifyEnabled(false);
        newOptions.setBackgroundColorStart(options.getBackgroundColorStart());
        newOptions.setBackgroundColorEnd(options.getBackgroundColorEnd());
        List<Layer> layers = new ArrayList<>();
        for(Layer optionLayer: options.getLayers()) {
            layers.add(new Layer(optionLayer.getLayerName(), optionLayer.getResourceId(), optionLayer.getShadowResourceId(), optionLayer.getScrollX()));
        }
        newOptions.setLayers(layers);
        newOptions.setStartColor(options.getStartColor());
        newOptions.setEndColor(options.getEndColor());
        newOptions.setHasCraters(options.hasCraters());
        newOptions.setLayerOffset(options.getLayerOffset(), true);
        newOptions.setPlanetColor(options.getPlanetColor());
        newOptions.setShowPlanet(options.isShowPlanet());
        newOptions.setStarsColor(options.getStarsColor());
        newOptions.setStarsCount(options.getStarsCount());
        newOptions.setUseGradient(options.isUseGradient());
        newOptions.setNotifyEnabled(true);
        newOptions.setName(options.getName());

        newOptions.notifyObservers(true, true);
        return newOptions;
    }

    public int getStarsColor() {
        return starsColor;
    }

    public void setStarsColor(int starsColor) {
        this.starsColor = starsColor;
        notifyObservers(false, false);
    }

    public boolean isUserPreset() {
        return isUserPreset;
    }

    public void setUserPreset(boolean userPreset) {
        isUserPreset = userPreset;
    }

    public boolean isShowPlanet() {
        return showPlanet;
    }

    public void setShowPlanet(boolean showPlanet) {
        this.showPlanet = showPlanet;
        notifyObservers(false, false);
    }

    public boolean isNotifyEnabled() {
        return notifyEnabled;
    }

    public void setNotifyEnabled(boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasCraters() {
        return hasCraters;
    }

    public void setHasCraters(boolean hasCraters) {
        this.hasCraters = hasCraters;
        notifyObservers(false, false);
    }

    public int getLayerOffset() {
        return layerOffset;
    }

    public void setLayerOffset(int layerOffset, boolean isPreset) {
        this.layerOffset = layerOffset;
        notifyOffsetUpdated(isPreset);
    }

    public int getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(int starsCount) {
        this.starsCount = starsCount;
        notifyObservers(false, false);
    }

    public boolean isUseGradient() {
        return useGradient;
    }

    public void setUseGradient(boolean useGradient) {
        this.useGradient = useGradient;
        notifyObservers(false, false);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
        notifyObservers(true, false);
    }

    public void addLayer(Layer layer) {
        this.layers.add(layer);
        notifyObservers(true, false);
    }

    public void editLayer(int position, Layer updatedLayer) {
        this.layers.set(position, updatedLayer);
        notifyObservers(true, false);
    }

    public void removeLayer(Layer layer) {
        this.layers.remove(layer);
        notifyObservers(true, false);
    }

    public int getStartColor() {
        return startColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
        notifyObservers(false, false);
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
        notifyObservers(false, false);
    }

    public int getBackgroundColorEnd() {
        return backgroundColorEnd;
    }

    public void setBackgroundColorEnd(int backgroundColorEnd) {
        this.backgroundColorEnd = backgroundColorEnd;
        notifyObservers(false, false);
    }

    public int getBackgroundColorStart() {
        return backgroundColorStart;
    }

    public void setBackgroundColorStart(int backgroundColorStart) {
        this.backgroundColorStart = backgroundColorStart;
        notifyObservers(false, false);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColorStart = backgroundColor;
        this.backgroundColorEnd = backgroundColor;
        setUseGradient(false);
        notifyObservers(false, false);
    }

    public int getPlanetColor() {
        return planetColor;
    }

    public void setPlanetColor(int planetColor) {
        this.planetColor = planetColor;
        notifyObservers(false, false);
    }

    public void setAsBlueTheme() {
        notifyEnabled = false;
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_theme_background));
        setStartColor(ContextCompat.getColor(mContext, R.color.blue_theme_first_layer));
        setEndColor(ContextCompat.getColor(mContext, R.color.blue_theme_last_layer));
        setPlanetColor(Color.WHITE);
        setShowPlanet(true);
        setStarsCount(300);
        setStarsColor(Color.WHITE);
        setHasCraters(true);
        setLayerOffset(DEFAULT_OFFSET, true);
        setUseGradient(false);
        setUserPreset(false);
        setName(mContext.getString(R.string.blue_night_theme));

        List<Layer> layers = new ArrayList();
        Layer layer1 = new Layer(mContext.getString(R.string.high_mountains_with_shadows), R.drawable.mountain_layer_3_crop, R.drawable.mountain_layer_3_shadow_crop, 250);
        Layer layer2 = new Layer(mContext.getString(R.string.mountains_1), R.drawable.mountain_layer_1_crop, 0, 0);
        Layer layer3 = new Layer(mContext.getString(R.string.mountains_2), R.drawable.mountain_layer_2_crop, 0, 0);
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        setLayers(layers);
        notifyEnabled = true;
        notifyObservers(true, true);
    }

    public void setAsRedTheme() {
        notifyEnabled = false;
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.sunny_theme_background));
        setStartColor(ContextCompat.getColor(mContext, R.color.sunny_theme_first_layer));
        setEndColor(ContextCompat.getColor(mContext, R.color.sunny_theme_last_layer));
        setPlanetColor(ContextCompat.getColor(mContext, R.color.sunny_theme_sun));
        setShowPlanet(true);
        setStarsCount(0);
        setStarsColor(Color.WHITE);
        setHasCraters(false);
        setLayerOffset(DEFAULT_OFFSET, true);
        setUseGradient(false);
        setUserPreset(false);
        setName(mContext.getString(R.string.red_sunny_theme));

        List<Layer> layers = new ArrayList();
        Layer layer1 = new Layer(mContext.getString(R.string.high_mountains_with_shadows), R.drawable.mountain_layer_3_crop, R.drawable.mountain_layer_3_shadow_crop, 250);
        Layer layer2 = new Layer(mContext.getString(R.string.mountains_1), R.drawable.mountain_layer_1_crop, 0, 0);
        Layer layer3 = new Layer(mContext.getString(R.string.mountains_2), R.drawable.mountain_layer_2_crop, 0, 0);
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        setLayers(layers);
        notifyEnabled = true;
        notifyObservers(true, true);
    }

    public void setAsGreenTheme() {
        notifyEnabled = false;
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_theme_background));
        setStartColor(ContextCompat.getColor(mContext, R.color.green_theme_first_layer));
        setEndColor(ContextCompat.getColor(mContext, R.color.green_theme_last_layer));
        setPlanetColor(ContextCompat.getColor(mContext, R.color.green_theme_sun));
        setShowPlanet(true);
        setStarsCount(0);
        setStarsColor(Color.WHITE);
        setHasCraters(false);
        setLayerOffset(DEFAULT_OFFSET, true);
        setUseGradient(false);
        setUserPreset(false);
        setName(mContext.getString(R.string.green_forest_theme));

        List<Layer> layers = new ArrayList();
        Layer layer1 = new Layer(mContext.getString(R.string.high_mountains_with_shadows), R.drawable.mountain_layer_3_crop, R.drawable.mountain_layer_3_shadow_crop, 250);
        Layer layer2 = new Layer(mContext.getString(R.string.mountains_1), R.drawable.mountain_layer_1_crop, 0, 0);
        Layer layer3 = new Layer(mContext.getString(R.string.mountains_2), R.drawable.mountain_layer_2_crop, 0, 0);
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        setLayers(layers);
        notifyEnabled = true;
        notifyObservers(true, true);
    }

    public void setAsPurpleTheme() {
        notifyEnabled = false;
        setBackgroundColorStart(ContextCompat.getColor(mContext, R.color.purple_theme_background_start));
        setBackgroundColorEnd(ContextCompat.getColor(mContext, R.color.purple_theme_background_end));
        setStartColor(ContextCompat.getColor(mContext, R.color.purple_theme_first_layer));
        setEndColor(ContextCompat.getColor(mContext, R.color.purple_theme_last_layer));
        setPlanetColor(ContextCompat.getColor(mContext, R.color.purple_theme_moon));
        setShowPlanet(true);
        setStarsCount(300);
        setStarsColor(Color.WHITE);
        setHasCraters(true);
        setLayerOffset(DEFAULT_OFFSET, true);
        setUseGradient(true);
        setUserPreset(false);
        setName(mContext.getString(R.string.purple_night_theme));

        List<Layer> layers = new ArrayList();
        Layer layer1 = new Layer(mContext.getString(R.string.high_mountains_with_shadows), R.drawable.mountain_layer_3_crop, R.drawable.mountain_layer_3_shadow_crop, 250);
        Layer layer2 = new Layer(mContext.getString(R.string.mountains_1), R.drawable.mountain_layer_1_crop, 0, 0);
        Layer layer3 = new Layer(mContext.getString(R.string.mountains_2), R.drawable.mountain_layer_2_crop, 0, 0);
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        setLayers(layers);
        notifyEnabled = true;
        notifyObservers(true, true);
    }


    public void setAsBlueWinterTheme() {
        notifyEnabled = false;
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_winter_theme_background));
        setStartColor(ContextCompat.getColor(mContext, R.color.blue_winter_theme_first_layer));
        setEndColor(ContextCompat.getColor(mContext, R.color.blue_winter_theme_last_layer));
        setPlanetColor(ContextCompat.getColor(mContext, R.color.blue_winter_theme_moon));
        setShowPlanet(true);
        setStarsCount(0);
        setStarsColor(Color.WHITE);
        setHasCraters(false);
        setLayerOffset(DEFAULT_OFFSET, true);
        setUseGradient(false);
        setUserPreset(false);
        setName(mContext.getString(R.string.blue_winter_theme));

        List<Layer> layers = new ArrayList();
        Layer layer1 = new Layer(mContext.getString(R.string.high_mountains_with_shadows), R.drawable.mountain_layer_3_crop, R.drawable.mountain_layer_3_shadow_crop, 250);
        Layer layer2 = new Layer(mContext.getString(R.string.mountains_1), R.drawable.mountain_layer_1_crop, 0, 0);
        Layer layer3 = new Layer(mContext.getString(R.string.mountains_2), R.drawable.mountain_layer_2_crop, 0, 0);
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        setLayers(layers);
        notifyEnabled = true;
        notifyObservers(true, true);
    }

    private List<CanvasOptionsObserver> getObservers() {
        return this.observers;
    }

    public void addObserver(CanvasOptionsObserver observer) {
        if(observers == null)
            observers = new ArrayList<>();
        observers.add(observer);
    }

    public void notifyObservers(boolean layerUpdated, boolean isPreset) {
        if(notifyEnabled) {
            for (CanvasOptionsObserver observer : observers) {
                observer.onCanvasOptionsUpdated(layerUpdated, isPreset);
            }
        }
    }

    public void notifyOffsetUpdated(boolean isPreset) {
        if(notifyEnabled) {
            for (CanvasOptionsObserver observer : observers) {
                observer.onLayerOffsetUpdated(isPreset);
            }
        }
    }

    public static List<CanvasOptions> getSavedCanvasOptions() {
        return savedCanvasOptions;
    }

    public void saveCurrentOptions(){

        try {
            FileOutputStream fos = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);

            CanvasOptions.savedCanvasOptions.add(CanvasOptions.newInstance(CanvasOptions.getInstance(mContext)));

            out.writeObject(CanvasOptions.savedCanvasOptions);

            out.close();
            fos.close();

            System.out.println("Object has been serialized");

        } catch(IOException ex) {
            System.out.println("IOException is caught");
        }

    }

    public void deleteSavedCanvasOptions(CanvasOptions options) {
        try {
            FileOutputStream fos = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);

            CanvasOptions.savedCanvasOptions.remove(options);

            out.writeObject(CanvasOptions.savedCanvasOptions);

            out.close();
            fos.close();

            System.out.println("Object has been serialized");

        } catch(IOException ex) {
            System.out.println("IOException is caught");
        }
    }

    public void loadSavedCanvasOptions(){

        try {
            FileInputStream fis = mContext.openFileInput(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fis);

            ArrayList<CanvasOptions> options = (ArrayList<CanvasOptions>)in.readObject();

            CanvasOptions.getInstance(mContext).savedCanvasOptions = options;

            in.close();
            fis.close();
        } catch(IOException ex) {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }

    }
}
