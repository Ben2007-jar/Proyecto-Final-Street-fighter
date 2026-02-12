package com.MBM.KOMaster.data;

/**
 * Almacena la información de un mapa del juego.
 */
public class MapData {
    
    private final int id;
    private final String name;
    private final String backgroundPath;
    
    public MapData(int id, String name, String backgroundPath) {
        this.id = id;
        this.name = name;
        this.backgroundPath = backgroundPath;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBackgroundPath() { return backgroundPath; }
}