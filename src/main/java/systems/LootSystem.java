/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package systems;

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
import controls.EnemyControl;
import controls.LootControl;
import dungeonrpg.Item;
import java.util.HashMap;

/**
 *
 * @author ivan
 */
public class LootSystem {
    public Node lootNode;
    AssetManager assetManager;
    BulletAppState    physics;
    public HashMap<String,Item> items = new HashMap<String,Item>();
    String[] weaponArmor;
    String[] weapon10Case;
    String[] armorCase;
    String[] modifier;
    String[] ofModifier;

    public LootSystem(Node lootNode,AssetManager assetManager,BulletAppState    physics){
        this.assetManager = assetManager;
        this.lootNode = lootNode;
        this.physics  = physics;
        
        weaponArmor  = new String[3];
        weaponArmor[0] = "Armor"; 
        weaponArmor[1] = "Weapon";
        
        weapon10Case  = new String[1];
        weapon10Case[0] = "sword"; 
        
        armorCase = new String[6];
        armorCase[0] = "leg";
        armorCase[1] = "boots";
        armorCase[2] = "armor";
        armorCase[3] = "helmet";
        armorCase[4] = "barassers";
        armorCase[5] = "shild";
        
        modifier = new String[3];
        modifier[0] = "Sumply";
        modifier[1] = "Good";
        modifier[2] = "Excelent";
        
        ofModifier  = new String[4];
        ofModifier[0] = "Str";
        ofModifier[1] = "Dex";
        ofModifier[2] = "Con";
        ofModifier[3] = "Def";

        
    }
    
    public void setLoot(int level, Vector3f positoion){
        double points = level * 15;
        String prefix = "leather-";
        int weaponArmorSwitch = getRandomNumber(0,1);
        
        if(level < 10){
            prefix = "leather-";
            if(weaponArmor[weaponArmorSwitch] == "Weapon"){
                createWeapon(points,positoion,level);
            }
            if(weaponArmor[weaponArmorSwitch] == "Armor"){
                createArmor(points,positoion,prefix);
            }
        }
    }
    
    public void createArmor(double points,Vector3f positoion,String prefix){ Item armor = new Item();
        
    
        String modfier = modifier[getRandomNumber(0,modifier.length)];
        int forse = 1;
        if(modfier == "Sumply"){
            forse = 1;
        }
        if(modfier == "Good"){
            forse = 2;
        }
        if(modfier == "Excelent"){
            forse = 3;
        }
        
        String ofModifierStr = ofModifier[getRandomNumber(0,ofModifier.length)];

        
        String type = armorCase[getRandomNumber(0,armorCase.length)];
        
        armor.icon = prefix + type;
        armor.spatialName = prefix + type;
        armor.type = type;
        armor.assetManager = assetManager;
        
        int pointsTo = (int)(points /5)*forse;
        if(ofModifierStr == "Str"){
            armor.str = pointsTo * 2;
        }else{
            armor.str = pointsTo;
        }
        if(ofModifierStr == "Dex"){
            armor.dex = pointsTo * 2;
        }else{
            armor.dex = pointsTo;
        }
        if(ofModifierStr == "Con"){
            armor.con = pointsTo * 2;
        }else{
            armor.con = pointsTo;
        }
        if(ofModifierStr == "Def"){
            armor.def = pointsTo * 2;
        }else{
            armor.def = pointsTo;
        }
        armor.hp  = points /5;
        
        armor.description = modfier + " " +armor.spatialName + " of " + ofModifierStr +" " + armor.hp + " HP " + pointsTo + " stats";

        armor.loadSpatial();
        
        String id = getRandomString();
        items.put(id, armor);
        
        
        
        //load mosnter by name
        Spatial model = assetManager.loadModel("Models/items/chest.glb");
        //setup zay es id to staptial name for detec from collision
        model.setName( id);

        //setum monster CharacterControl control
       
        model.addControl(new LootControl(this));
        
        physics.getPhysicsSpace().add(model);
        
        //setum monster control with monster logic
        model.setLocalTranslation(positoion);
        
        model.scale(1.2f);
        lootNode.attachChild(model);
        
    }
    
    
    public void createWeapon(double points,Vector3f positoion,int level){
        String modfier = modifier[getRandomNumber(0,modifier.length)];

        int forse = 1;
        if(modfier == "Sumply"){
            forse = 1;
        }
        if(modfier == "Good"){
            forse = 2;
        }
        if(modfier == "Excelent"){
            forse = 3;
        }
        String spatialName ="";
        if(level < 10){
             spatialName = weapon10Case[getRandomNumber(0,weapon10Case.length)];
        }
        
        Item weapon = new Item();
        weapon.icon = spatialName;
        weapon.description = spatialName + (points/2) * forse + " damage";
        weapon.damage = (points/2)*forse;
        weapon.spatialName = spatialName;
        weapon.type = "weapon";
        
        weapon.assetManager = assetManager;
        weapon.loadSpatial();
        
        String id = getRandomString();
        items.put(id, weapon);
        
        //load mosnter by name
        Spatial model = assetManager.loadModel("Models/items/chest.glb");
        //setup zay es id to staptial name for detec from collision
        model.setName( id);
        
        //setum monster CharacterControl control
        SphereCollisionShape sphereShape = new SphereCollisionShape(2.0f);
        CharacterControl myThing_phys = new CharacterControl( sphereShape , 1.2f );
        model.addControl(myThing_phys);
        
        
        physics.getPhysicsSpace().add(myThing_phys);

        physics.getPhysicsSpace().add(model);
        
        //setum monster control with monster logic
        myThing_phys.setPhysicsLocation(positoion);
        
        model.scale(3.2f);
        lootNode.attachChild(model);
    }
    
    
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
    private String getRandomString(){
        
        return Double.toString((float) Math.random());
    }
    
}
