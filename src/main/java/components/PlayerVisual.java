/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityComponent;
import dungeonrpg.Item;

/**
 *
 * @author ivan
 */
public class PlayerVisual implements EntityComponent{
    public Spatial spatial;
    public CharacterControl characterControl;
    public AnimComposer anim;
    

    public boolean isRun = false;
    
    public PlayerVisual(AssetManager assetManager){
        spatial = (Spatial) assetManager.loadModel("Models/char.j3o");
        spatial.scale(1.5f);
        Node spatialNode = (Node)spatial;
        Node armature = (Node) spatialNode.getChild(0);
        
        anim = armature.getControl(AnimComposer.class);
        anim.setCurrentAction("stand");
    }
    
    public void setCharacterControl(CharacterControl characterControl){
        this.characterControl = characterControl;
    }
    
}
