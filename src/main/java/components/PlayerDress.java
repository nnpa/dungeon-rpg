/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import com.simsilica.es.EntityComponent;
import dungeonrpg.Item;
import java.util.HashMap;

/**
 *
 * @author ivan
 */
public class PlayerDress implements EntityComponent{
    public Item helmet = null;
    public Item armor = null;
    public Item boots = null;
    public Item brassers = null;
    public Item leggings = null;
    public Item shild = null;
    public Item weapon = null;

    public HashMap<Integer,Item> items = new  HashMap<Integer,Item>();
    
    public PlayerDress(){
        
        for(int i =0; i <9;i++){
            items.put(i, null);
        }
    }
    
    public void addItem(Item item){
        int slot = getFreeSlot();
        if(slot != -1){
             items.put(slot, item);
        }
    }
    
    public int getFreeSlot(){
        for(int i =0; i <9;i++){
            Item item = items.get(i);
            if(item == null){
                return i;
            }
        }
        return -1;
    }
}
