/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dungeonrpg.level;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;
import components.Monster;
import components.PlayerVisual;
import controls.EnemyControl;
import dungeonrpg.GameLevel;

/**
 *
 * @author ivan
 */
public class StartLevel extends GameLevel{
    public Vector3f startPosition = new Vector3f(0,2f,0);
    
    @Override
    public void load(){
        loadNPC(25,10,25,"skeleton",1);
        loadNPC(20,10,20,"skeleton",1);

    }
    
 
    
}
