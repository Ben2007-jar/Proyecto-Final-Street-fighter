package com.MBM.KOMaster.data;

/**
 * Base de datos de mapas del juego.
 * Centraliza toda la información de los escenarios.
 */
public class MapDatabase {
    
    private static final MapData[] MAPS = {
        new MapData(0, "Aula", "images/aula.png"),
        new MapData(1, "Escenario", "images/escenario.png"),
        new MapData(2, "Patio Adentro", "images/patioAdentro.png"),
        new MapData(3, "Patio Afuera", "images/patioAfuera.png")
    };
    
    /**
     * Obtiene un mapa por su ID
     */
    public static MapData getMap(int id) {
        if (id >= 0 && id < MAPS.length) {
            return MAPS[id];
        }
        return null;
    }
    
    /**
     * Obtiene todos los mapas
     */
    public static MapData[] getAllMaps() {
        return MAPS;
    }
    
    /**
     * Obtiene la cantidad total de mapas
     */
    public static int getMapCount() {
        return MAPS.length;
    }
}