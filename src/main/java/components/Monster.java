/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import com.jme3.anim.AnimComposer;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author ivan
 */
public class Monster  implements EntityComponent{
    //node to attah
    public Node npcNode;
    //mosnte spatial
    public Spatial spatial;
    //monste control
    public CharacterControl control;
    //monster anim composer
    public AnimComposer anim;
    
    //monster stats
    public double hp= 100;
    public double damage = 15;
    public String name;
    
    public double currentHP = 100;
    public EntityId id;
    public int lvl = 1;
}
