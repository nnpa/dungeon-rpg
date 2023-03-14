/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dungeonrpg;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import components.Monster;
import components.PlayerStat;
import components.PlayerVisual;
import controls.EnemyControl;
import java.util.ArrayList;

/**
 *
 * @author ivan
 */
public class GameLevel {
    Spatial spatial;

    public void init(AssetManager assetManager,String levelName) {
        spatial = (Spatial) assetManager.loadModel("Models/" + levelName+".glb");
        
    }
    
    public AssetManager assetManager;
    public BulletAppState    physics;
    public Node npcNode;
    public EntityData data;
    public  EntityId playerId;
    
    public void setup(AssetManager assetManager,BulletAppState    physics, Node npcNode, EntityData data, EntityId playerId){
        this.assetManager = assetManager;
        this.physics = physics;
        this.npcNode = npcNode;
        this.data = data;
        this.playerId = playerId;

    }
    
    public void load(){
               
    }
    
    public void loadNPC(float x, float y,float z, String name,int level){
        //create zay es entity
        EntityId monsterId = data.createEntity();
        
        //create new monster
        Monster monster = new Monster();
        monster.name = name;
        monster.npcNode = npcNode;
        monster.id = monsterId;
        monster.lvl = level;
        //10 + 30
        monster.damage = 40 + level * 5; 
        monster.hp = level * 250;
        monster.currentHP = level * 250;

        //load mosnter by name
        Spatial model = assetManager.loadModel("Models/monsters/"+name+".glb");
        //setup zay es id to staptial name for detec from collision
        String id = Long.toString(monsterId.getId()); 
        model.setName( id);
        
        
        
        //setum monster CharacterControl control
        SphereCollisionShape sphereShape = new SphereCollisionShape(2.0f);
        CharacterControl myThing_phys = new CharacterControl( sphereShape , 1.2f );
        model.addControl(myThing_phys);
        
        monster.control = myThing_phys;
        
        physics.getPhysicsSpace().add(myThing_phys);

        physics.getPhysicsSpace().add(model);
        
        //setum monster control with monster logic
        model.addControl(new EnemyControl(data,playerId,monster));
        myThing_phys.setPhysicsLocation(new Vector3f(x,y,z));
        
        //setup anima composer
        Node monsterNode = (Node)model;
        Node armature = (Node)monsterNode.getChild(0);
            
        
        
        AnimComposer animComposer = armature.getControl(AnimComposer.class);        
        monster.anim = animComposer;
        monster.anim .setCurrentAction("stand");
        
        model.scale(1.2f);
        npcNode.attachChild(model);
        
        monster.spatial = model;
        
        //add monster component to zay
        data.setComponent(monsterId, monster);

        
    
    }
 
}
