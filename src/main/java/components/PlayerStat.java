/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import com.simsilica.es.EntityComponent;
import dungeonrpg.Item;

/**
 *
 * @author ivan
 */
public class PlayerStat  implements EntityComponent {
    
    public String location = "StartLevel";
    
    public double hp = 100;
    public int level = 1;
    public int corretnHp = 100;
    
    public int str = 10;
    public int dex = 10;
    public int con = 10;
    
    public int exp = 0;
    public int nextLevel = 10;
    public int def = 10;
    public double damage = 25;
    public double currentHp = 100;
    
    public int agr = 0;
    
    
}
