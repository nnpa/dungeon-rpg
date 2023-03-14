/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.time.Instant;
import systems.LootSystem;

/**
 *
 * @author ivan
 */
public class LootControl extends AbstractControl{
    private float speed = 1f;
    
    public LootSystem lootSystem;
    
    public long deteleTime = 0;
    
    public boolean isDeleted = false;
    
    public LootControl(LootSystem lootSystem){
        this.lootSystem = lootSystem;
        deteleTime = Instant.now().getEpochSecond() + 20;
    }
    
    @Override
    protected void controlUpdate(float f) {
        spatial.rotate(0, speed*f, 0);
        
        if(isDeleted == false ){
            if(deteleTime<Instant.now().getEpochSecond()){
                lootSystem.lootNode.detachChild(spatial);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }
}
