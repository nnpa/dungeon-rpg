/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import com.jme3.anim.AnimLayer;
import com.jme3.anim.tween.action.Action;
import com.jme3.animation.AnimChannel;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import components.Monster;
import components.PlayerStat;
import components.PlayerVisual;
import java.time.Instant;
import java.util.Set;

/**
 *
 * @author ivan
 */
public class EnemyControl extends AbstractControl{
    EntityData data;
    EntityId playerId;
    Monster monster;
    PlayerVisual playerVisual;
    PlayerStat playerStat;
    AnimChannel attackChanel;
    
    public EnemyControl(EntityData data, EntityId playerId,Monster monster) {
        this.data = data;
        this.playerId = playerId;
        this.monster = monster;
        
        Entity entity = data.getEntity(playerId, PlayerVisual.class);

        playerVisual = entity.get(PlayerVisual.class);
       
        entity = data.getEntity(playerId, PlayerStat.class);
        
        playerStat = entity.get(PlayerStat.class);
        

    }

    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }
    
    private float interp = 0.0f;
    private float mySpeed = 0.05f;
    private Vector3f  walkDirection = new Vector3f(0,0,0);
    private float agrDistance = 15f;
    private boolean agr = false;
    private boolean isRun = false;
    private boolean isAttack = false;
    private boolean isAlive = true;
    
    @Override
    protected void controlUpdate(float f) {
        
        //
        if(monster.currentHP <= 0){
            isAlive = false;
        }
        walkDirection.set(0, 0, 0);

        Vector3f monsterPosition = monster.control.getPhysicsLocation();
        Vector3f playerPosition = playerVisual.characterControl.getPhysicsLocation();     

        float distance = playerPosition.distance(monsterPosition);
        interp += (mySpeed/distance) * f;
        //if moster alive
        
        if(isAlive){
            //monster agr
            if(distance < agrDistance && agr == false){
                agr = true;
                playerStat.agr = playerStat.agr +1;
            }
            
            //move monster
            if ( distance > 3 && agr) {
                //direction to player
                Vector3f direction = playerPosition.subtract(monsterPosition).normalize();
                //set monster view direction to player
                monster.control.setViewDirection(direction);

                //add direction 
                walkDirection.addLocal(direction);
                //set walk direction
                monster.control.setWalkDirection(walkDirection.mult(0.05f));
                if(isRun == false){

                    monster.anim.setCurrentAction("run");
                    isRun = true;
                }
                isAttack = false;
            }else if(distance <= 3){
                //monster stand and attack
                
                //set walk direction 0
                walkDirection.set(0, 0, 0);
                monster.control.setWalkDirection(walkDirection);
               
                isRun = false;
                //attak animation
                if(isAttack == false){
                   monster.anim.setCurrentAction("attack");
                   
                    isAttack= true;
                }
                //remove mosnter damage from player
                if(nextAttack < Instant.now().getEpochSecond()){
                    playerStat.currentHp = playerStat.currentHp + playerStat.def - monster.damage;
                    nextAttack = Instant.now().getEpochSecond()+1;
                    
                }

            }
        }else{
            //monster die and remove
            if(isRemove == false){
                
                if(removeTime == 0){
                    removeTime = Instant.now().getEpochSecond() +7;

                }
                if(removeTime == Instant.now().getEpochSecond()){
                    monster.npcNode.detachChild(spatial);
                    data.removeEntity(monster.id);

                    isRemove = true;
                }
            }
        }
        
        
        
    }
    long removeTime = 0;
    boolean isRemove = false;
    
    public long nextAttack = 0;
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    
    }
    
}
