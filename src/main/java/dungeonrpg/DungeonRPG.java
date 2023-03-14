package dungeonrpg;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.Styles;
import components.PlayerDress;
import components.PlayerStat;
import components.PlayerVisual;
import dungeonrpg.level.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This is the Main Class of your Game. It should boot up your game and do initial initialisation
 * Move your Logic into AppStates or Controls or other java classes
 */
public class DungeonRPG extends SimpleApplication {

    public static void main(String[] args) {
        DungeonRPG app = new DungeonRPG();
        app.start();
    }

    @Override
    public void simpleInitApp() {
            GuiGlobals.initialize(this);
            GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
            BaseStyles.loadGlassStyle();
            //get player from save
            EntityData data = new DefaultEntityData();            
            EntityId playerId = data.createEntity();

            PlayerStat playerStat =  new PlayerStat();
            data.setComponent(playerId, new PlayerVisual(getAssetManager()));
            data.setComponent(playerId, playerStat);
            
            playerStat.exp = 0 ;
            playerStat.nextLevel = (int)((playerStat.level + 1)  * 50 * 0.6f);
            
            PlayerDress playerDress = new PlayerDress();

             Item armor = new Item();
            armor.spatialName = "leather-armor";
            armor.assetManager = assetManager;
            armor.loadSpatial();
            armor.hp = 10;
            armor.def = 5;
            armor.con = 5;
            armor.dex = 5;

            armor.str = 5;

            armor.icon = "leather-armor";
            armor.type = "armor";

            armor.description = "Leather armor 5 stats";

            playerDress.armor = armor;
            
            Item boots = new Item();
            boots.spatialName = "leather-boots";
            boots.assetManager = assetManager;
            boots.loadSpatial();
            boots.type = "boots";
            boots.hp = 10;
            boots.def =5;
            boots.con = 5;
            boots.dex = 5;

            boots.str = 5;
            boots.icon = "leather-boots";
            boots.description = "Leather boots 5 stats";
            playerDress.boots = boots;
            
            Item barassers = new Item();
            barassers.spatialName = "leather-barassers";
            barassers.assetManager = assetManager;
            barassers.loadSpatial();
            barassers.type = "barassers";
            barassers.hp = 10;
            barassers.def =5;
            barassers.con = 5;
            barassers.dex = 5;

            barassers.str = 5;
            barassers.icon = "leather-barassers";
            barassers.description = "Leather barassers 5 stats";

            playerDress.brassers = barassers;
            
            Item helmet = new Item();
            helmet.spatialName = "leather-helmet";
            helmet.assetManager = assetManager;
            helmet.loadSpatial();
            helmet.type = "helmet";
            helmet.hp = 10;
            helmet.def =5;
            helmet.con = 5;
            helmet.str = 5;
            helmet.dex = 5;

            helmet.icon = "leather-helmet";
            helmet.description = "Leather helmet 5 stats";
            //playerDress.addItem(helmet);
            playerDress.helmet = helmet;

            Item leggings = new Item();
            leggings.spatialName = "leather-leg";
            leggings.assetManager = assetManager;
            leggings.type = "leg";
            leggings.loadSpatial();
            leggings.hp = 10;
            leggings.def =5;
            leggings.con = 5;
            leggings.dex = 5;

            leggings.str = 5;
            leggings.icon = "leather-leg";
            leggings.description = "Leather leggings 5 stats";
            playerDress.leggings = leggings;   
            
            Item shild = new Item();
            
            shild.spatialName = "leather-shild";
            shild.assetManager = assetManager;
            shild.loadSpatial();  
            shild.type = "shild";
            shild.hp = 10;
            shild.def =5;
            shild.con = 5;
            shild.dex = 5;

            shild.str = 5;
            shild.icon = "leather-shild";
            shild.description = "Simpky shild 5 defence";
            
            playerDress.shild = shild;
            
            Item weapon = new Item();
            weapon.icon = "sword";
            weapon.description = "Simply sword 5 damage";
            weapon.damage = 5;
            weapon.spatialName = "sword";
            weapon.type = "weapon";

            weapon.assetManager = assetManager;
            weapon.loadSpatial();           
            playerDress.weapon = weapon;
            
            data.setComponent(playerId, playerDress);
            
            //Entity entity = data.getEntity(playerId, PlayerVisual.class);

           // PlayerVisual playerVisual = entity.get(PlayerVisual.class);

           // Player player = new Player(getAssetManager());
            
            //get game level from player
            GameLevel level = getGameLeveByName(playerStat.location);
            level.init(getAssetManager(),playerStat.location);
            
            LevelAppState appStateLevel = new LevelAppState(playerId,data,level);
            stateManager.attach(appStateLevel);
            
    }
    
    public GameLevel getGameLeveByName(String location){
        try {
            // Get Class instance
            Class<?> clazz = Class.forName("dungeonrpg.level." + location);
            
            // Get the private constructor.
            Constructor<?> cons = clazz.getDeclaredConstructor();

            // Since it is private, make it accessible.
            cons.setAccessible(true);
            
            // Create new object. 
            GameLevel level = (GameLevel) cons.newInstance();
            return level;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DungeonRPG.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void simpleUpdate(float tpf) {
   
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
}
