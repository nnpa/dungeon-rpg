/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dungeonrpg;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author ivan
 */
public class Item {
    public String spatialName;
    public String type = "";
    public String name = "";
    public String description = "";
    
    public int str = 0;
    public int dex = 0;
    public int con = 0;
    public double hp = 0;
    public int def = 0;
    public double damage = 0;

    public Spatial spatial;
    public String icon = "";
    public AssetManager assetManager;
    public void loadSpatial(){
        spatial = (Spatial) assetManager.loadModel("Models/items/"+spatialName+".j3o");
    }
    
    
}
