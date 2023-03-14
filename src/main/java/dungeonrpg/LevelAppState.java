/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dungeonrpg;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.ui.Picture;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ProgressBar;
import components.Monster;
import components.PlayerDress;
import components.PlayerStat;
import components.PlayerVisual;
import dungeonrpg.level.*;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.time.Instant;
import systems.DressSystem;
import systems.LootSystem;

/**
 *
 * @author ivan
 */
public class LevelAppState extends BaseAppState{
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          viewPort;
    private BulletAppState    physics;
    private Node guiNode;
    private EntityId playerId;
    private GameLevel level;
    private Vector3f moveTo = null;
    
    private Geometry mark;
    private Camera cam;
    private Node shootables;
    private Node npcNode;
    private Node lootNode;
    
    private Vector3f  walkDirection = new Vector3f(0,0,0);

    PlayerVisual playerVisual;
    EntityData data;
    PlayerStat playerStat;
    PlayerDress playerDress;
    Spatial target;
    LootSystem lootSystem;
    
    public LevelAppState(EntityId playerId, EntityData data, GameLevel level) {    
        this.playerId = playerId;
        this.level = level;
        this.data = data;

        Entity entity = data.getEntity(playerId, PlayerVisual.class);

        playerVisual = entity.get(PlayerVisual.class);
        
        entity = data.getEntity(playerId, PlayerStat.class);
        playerStat = entity.get(PlayerStat.class);

        entity = data.getEntity(playerId, PlayerDress.class);
        playerDress = entity.get(PlayerDress.class);
        
    }
    
    DressSystem dressSystem;
    @Override
    protected void initialize( Application app ) {
       this.app = (SimpleApplication) app; // can cast Application to something more specific
       

       
       this.rootNode     = this.app.getRootNode();
       this.assetManager = this.app.getAssetManager();
       this.stateManager = this.app.getStateManager();
       this.inputManager = this.app.getInputManager();
       this.viewPort     = this.app.getViewPort();
       this.cam          = app.getCamera();
       this.guiNode      = this.app.getGuiNode();
       
       target = assetManager.loadModel("Models/target.glb");
       //setup zay es id to staptial name for detec from collision
       
       
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int w = gd.getDisplayMode().getWidth();
        int h = gd.getDisplayMode().getHeight();
       
        dressSystem = new DressSystem(playerVisual,playerStat,playerDress,rootNode,inputManager,assetManager);

        Thread thread;
                thread = new Thread(new Runnable() { // <- Never override classes if it is not required.
                    public void run(){
                        app.enqueue(new Runnable() {
                            @Override
                            public void run() {

                                dressSystem.init();

                            }
                        });
                    }
                });
        thread.start();
       
       physics  = new BulletAppState();
       stateManager.attach(physics);
    
       shootables = new Node("Shootables");
       rootNode.attachChild(shootables);
       npcNode = new Node("NPC Node");
       rootNode.attachChild(npcNode);
       lootNode = new Node("Loot Node");
       rootNode.attachChild(lootNode);
       
       lootSystem = new LootSystem( lootNode, assetManager,physics);
       
        setUpLight();
        initKeys();
        initMark();
       
        level.setup(assetManager, physics,npcNode,data,playerId);
        level.load();
         
        
//        stateManager.attach(physics);
        
        SphereCollisionShape sphereShape = new SphereCollisionShape(1.2f); 
        playerVisual.characterControl = new CharacterControl( sphereShape , 1.2f );
        
        playerVisual.spatial.addControl(playerVisual.characterControl);
        
        physics.getPhysicsSpace().add(playerVisual.characterControl);
        
        playerVisual.characterControl.setPhysicsLocation(new Vector3f(0,0.1f,0));
        
        
        rootNode.attachChild(playerVisual.spatial);
        
         
        physics.getPhysicsSpace().add(playerVisual.characterControl);
        
        
        shootables.attachChild(level.spatial);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(level.spatial);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        level.spatial.addControl(landscape);
        physics.getPhysicsSpace().add(landscape);
        
        this.app.getFlyByCamera().setEnabled(false);
         
        
        ChaseCamera chaseCam = new ChaseCamera(cam, playerVisual.spatial, inputManager);
        chaseCam.setSmoothMotion(true);
        //inputManager.deleteMapping(CameraInput.CHASECAM_TOGGLEROTATE);
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(false);
        

        
        targetWindows = new Container();
        
        targetWindows.setLocalTranslation(w/2 -160 , h-270, 0);
        targetLabel = new Label("Hello, World.");

        targetWindows.addChild(targetLabel);
        targetWindows.setPreferredSize(new Vector3f(150,50,0));

        targetProgress = new ProgressBar("glass");
        targetProgress.setProgressValue(20);
        targetWindows.addChild(targetProgress);

        
        playerHp.setLocalTranslation(0 , h-270, 0);
        playerHp.setPreferredSize(new Vector3f(150,50,0));
        playerHp.addChild(new Label("Player"));
        playerProgress.setProgressValue(100);
        //playerProgress.setLocalScale(3, 0.75f, 0);
        playerHp.addChild(playerProgress);

        guiNode.attachChild(playerHp);
       
        
    }
    Container playerHp = new Container();
    
    ProgressBar playerProgress = new ProgressBar("glass");

    private boolean isNoTrager = true;
    private Container targetWindows;
    private ProgressBar targetProgress;
    private Label targetLabel;
    
    private void setUpLight() {

        
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(5.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
        
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White);
       dl2.setDirection(new Vector3f(100,3,26).normalizeLocal());
       rootNode.addLight(dl2);
       
        DirectionalLight dl3 = new DirectionalLight();
        dl3.setColor(ColorRGBA.White);
       dl3.setDirection(new Vector3f(-100,3,26).normalizeLocal());
       rootNode.addLight(dl3);
       
    }
    private Monster targetMonster;
        
  private void initMark() {
    Sphere sphere = new Sphere(30, 30, 0.2f);
    mark = new Geometry("BOOM!", sphere);
    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mark_mat.setColor("Color", ColorRGBA.White);
    mark.setMaterial(mark_mat);
  }
  
   private void initKeys() {
      inputManager.addMapping("Run",new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
      inputManager.addMapping("Target",new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // trigger 2: left-button click
      inputManager.addMapping("Attack",new KeyTrigger(KeyInput.KEY_F1)); // trigger 2: left-button click
      inputManager.addMapping("Fury",new KeyTrigger(KeyInput.KEY_F2)); // trigger 2: left-button click
      inputManager.addMapping("Kick",new KeyTrigger(KeyInput.KEY_F3)); // trigger 2: left-button click
      inputManager.addMapping("Shild",new KeyTrigger(KeyInput.KEY_F4)); // trigger 2: left-button click
      inputManager.addMapping("Inv",new KeyTrigger(KeyInput.KEY_I)); // trigger 2: left-button click

      
      inputManager.addListener(actionListener, "Run","Target","Attack","Fury","Kick","Shild","Inv");
  }
   
    private long toStandTime = 0;
    private long nextAttack = 0;

    final private ActionListener actionListener = new ActionListener() {
    
    private boolean isIventroy = false;
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
         if (name.equals("Inv") && !keyPressed) {
             if(isIventroy == false){
                guiNode.attachChild(dressSystem.itemsWindow);
                guiNode.attachChild(dressSystem.infoWindow);
                guiNode.attachChild(dressSystem.invWindow);
                guiNode.attachChild(dressSystem.statWindow);
                dressSystem.reloadInv();
                
                isIventroy = true;
                
             }else{
                guiNode.detachChild(dressSystem.itemsWindow);
                guiNode.detachChild(dressSystem.infoWindow);
                guiNode.detachChild(dressSystem.invWindow);  
                guiNode.detachChild(dressSystem.statWindow);  

                isIventroy = false;
             }
         }
        
        if (name.equals("Shild") && !keyPressed) {
          // if you have target
          if(targetMonster != null){
            //get location and distance
            Vector3f playerLocation = playerVisual.characterControl.getPhysicsLocation();
            Vector3f monsterLocation = targetMonster.control.getPhysicsLocation();
            float distance = playerLocation.distance(monsterLocation);
            //if distance and nex attack time
            if(distance < 4f && nextAttack < Instant.now().getEpochSecond()){
                
                isAttack = true;
                //run animation hit shil
                playerVisual.anim.setCurrentAction("shild");
                //time to change animation to stand
                toStandTime = System.currentTimeMillis() +500;
                
                //get direction to wiev to monster
                Vector3f direction = monsterLocation.subtract(playerLocation).normalize();
                
                Vector3f  correction = new Vector3f(direction.x,direction.y-0.35f,direction.z);
                //set view direction
                playerVisual.characterControl.setViewDirection(correction);
                
                //remove player attak from monster hp
                targetMonster.currentHP = targetMonster.currentHP - playerStat.damage - playerStat.con;
                
                //calculate % hp to pgoress bar
                double hp = targetMonster.hp;

                double onePercent = hp/100;
                double percent = targetMonster.currentHP/onePercent;
                //setup % to pgoress bat
                targetProgress.setProgressValue(percent);
                //monster die
                if(targetMonster.currentHP <=0){
                    setLoot();
                    targetMonster.anim.setCurrentAction("die");
                    guiNode.detachChild(targetWindows);
                    isNoTrager = true;
                    playerStat.agr = playerStat.agr - 1;
                    playerStat.exp += targetMonster.lvl * 50 ;
                }
                nextAttack = Instant.now().getEpochSecond() + 1;
                
            }
          }
      }
        
        if (name.equals("Kick") && !keyPressed) {
          if(targetMonster != null){

            Vector3f playerLocation = playerVisual.characterControl.getPhysicsLocation();
            Vector3f monsterLocation = targetMonster.control.getPhysicsLocation();
            float distance = playerLocation.distance(monsterLocation);
            
            if(distance < 4f && nextAttack < Instant.now().getEpochSecond()){
                
                isAttack = true;
                playerVisual.anim.setCurrentAction("kick");
                toStandTime = System.currentTimeMillis() +500;
                Vector3f direction = monsterLocation.subtract(playerLocation).normalize();
                
                Vector3f  correction = new Vector3f(direction.x,direction.y-0.35f,direction.z);
                
                playerVisual.characterControl.setViewDirection(correction);
                
                targetMonster.currentHP = targetMonster.currentHP - playerStat.damage - playerStat.str;
                
                
                double hp = targetMonster.hp;

                double onePercent = hp/100;
                double percent = targetMonster.currentHP/onePercent;
                
                targetProgress.setProgressValue(percent);
                
                if(targetMonster.currentHP <=0){
                    setLoot();
                    targetMonster.anim.setCurrentAction("die");
                    guiNode.detachChild(targetWindows);
                    isNoTrager = true;
                    playerStat.agr = playerStat.agr - 1;
                    playerStat.exp += targetMonster.lvl * 50 ;

                }
                nextAttack = Instant.now().getEpochSecond() + 1;
                
            }
          }
      }
        
        if (name.equals("Fury") && !keyPressed) {
          if(targetMonster != null){

            Vector3f playerLocation = playerVisual.characterControl.getPhysicsLocation();
            Vector3f monsterLocation = targetMonster.control.getPhysicsLocation();
            float distance = playerLocation.distance(monsterLocation);
            
            if(distance < 4f && nextAttack < Instant.now().getEpochSecond()){
                
                isAttack = true;
                playerVisual.anim.setCurrentAction("fury");
                toStandTime = System.currentTimeMillis() +1000;
                Vector3f direction = monsterLocation.subtract(playerLocation).normalize();
                
                Vector3f  correction = new Vector3f(direction.x,direction.y-0.35f,direction.z);
                
                playerVisual.characterControl.setViewDirection(correction);
                
                targetMonster.currentHP = targetMonster.currentHP - playerStat.damage- playerStat.dex;
                
                
                double hp = targetMonster.hp;

                double onePercent = hp/100;
                double percent = targetMonster.currentHP/onePercent;

                targetProgress.setProgressValue(percent);
                
                if(targetMonster.currentHP <=0){
                    setLoot();
                    targetMonster.anim.setCurrentAction("die");
                    guiNode.detachChild(targetWindows);
                    isNoTrager = true;
                    playerStat.agr = playerStat.agr - 1;
                    playerStat.exp += targetMonster.lvl * 50 ;

                }
                nextAttack = Instant.now().getEpochSecond() + 1;
                
            }
          }
      }
        if (name.equals("Attack") && !keyPressed) {
          if(targetMonster != null){

            Vector3f playerLocation = playerVisual.characterControl.getPhysicsLocation();
            Vector3f monsterLocation = targetMonster.control.getPhysicsLocation();
            float distance = playerLocation.distance(monsterLocation);
            
            if(distance < 4f && nextAttack < Instant.now().getEpochSecond()){
                
                isAttack = true;
                playerVisual.anim.setCurrentAction("attack");
                toStandTime = System.currentTimeMillis() +110;
                Vector3f direction = monsterLocation.subtract(playerLocation).normalize();
                
                Vector3f  correction = new Vector3f(direction.x,direction.y-0.35f,direction.z);
                
                playerVisual.characterControl.setViewDirection(correction);
                
                targetMonster.currentHP = targetMonster.currentHP - playerStat.damage - (playerStat.damage * 0.6f);
                
                
                double hp = targetMonster.hp;

                double onePercent = hp/100;
                double percent = targetMonster.currentHP/onePercent;
                
                targetProgress.setProgressValue(percent);
                
                if(targetMonster.currentHP <=0){
                    setLoot();
                    targetMonster.anim.setCurrentAction("die");
                    guiNode.detachChild(targetWindows);
                    isNoTrager = true;
                    playerStat.agr = playerStat.agr - 1;
                    playerStat.exp += targetMonster.lvl * 50 ;
                    
                }
                nextAttack = Instant.now().getEpochSecond() + 1;
                
            }
          }
      }
      
      if (name.equals("Target") && !keyPressed) {
        if(dressSystem.isTrash){
            dressSystem.setDefaultCursor();
            dressSystem.isTrash = false;
        }
        
        //mouse click direction
        Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        
        Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);

        direction.subtractLocal(origin).normalizeLocal();

        
        CollisionResults results = new CollisionResults();
        //create ray
        Ray ray = new Ray( cam.getLocation() , direction );
        

        //collide mouse click wit npc node
        npcNode.collideWith( ray, results );
        
        if( results.size() > 0 ){
            if(targetMonster != null){
                Node targetMonsterNode = (Node)targetMonster.spatial;
                targetMonsterNode.detachChild(target);
            }
            //get clicked spatil
            Spatial npcSpatial = (Spatial)results.getCollision(0).getGeometry().getParent().getParent();
            
            //get target monster
            EntityId targetId = new EntityId(Long.parseLong(npcSpatial.getName()));
            
            Entity entity = data.getEntity(targetId, Monster.class);

            targetMonster = entity.get(Monster.class);
            
            Node targetMonsterNode = (Node)targetMonster.spatial;
            targetMonsterNode.attachChild(target);
            //setup progress label
            targetLabel.setText(targetMonster.name);

            //calsulate progress bar % form monste hp
            double hp = targetMonster.hp;

            double onePercent = hp/100;
            double percent = targetMonster.currentHP/onePercent;
            //setup % value
            targetProgress.setProgressValue(percent);
            
                
            //attach videows
            if(isNoTrager){
                guiNode.attachChild(targetWindows);
            }
            
            
        }

      }
        
       if (name.equals("Run") && !keyPressed) {
        
        Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        
        Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);

        direction.subtractLocal(origin).normalizeLocal();

        
        CollisionResults results = new CollisionResults();

        Ray ray = new Ray( cam.getLocation() , direction );


        //pick up item
        lootNode.collideWith( ray, results );

        if( results.size() > 0 ){
            Spatial itemSpatial = (Spatial)results.getCollision(0).getGeometry().getParent();
            String itemName = itemSpatial.getName();
            
            Item item = lootSystem.items.get(itemName);
            playerDress.addItem(item);
            lootSystem.items.remove(itemName);
            dressSystem.reloadInv();
            lootNode.detachChild(itemSpatial);
        }
        
        
        results = new CollisionResults();

        ray = new Ray( cam.getLocation() , direction );
        //run at level
        shootables.collideWith( ray, results );
        
        if( results.size() > 0 ){

          CollisionResult closest = results.getClosestCollision();
          // Let's interact - we mark the hit with a red dot.
          Vector3f contactPoint = closest.getContactPoint();
          mark.setLocalTranslation(contactPoint);
          rootNode.attachChild(mark);
          Vector3f  correction = new Vector3f(contactPoint.x,contactPoint.y+1,contactPoint.z);
          
          
          moveTo = correction;
        }

        
      }
    }
  };
    
    private void setLoot(){
        
        lootSystem.setLoot(targetMonster.lvl, targetMonster.control.getPhysicsLocation());
    }
    
    @Override
    public void update(float tpf) {
        if(playerStat.exp >= playerStat.nextLevel){
            playerStat.exp = 0;
            playerStat.level +=1;
            playerStat.nextLevel = (int)((playerStat.level + 1)  * 50 * 0.6f);
            playerStat.hp += 50;
            playerStat.str += 5;
            playerStat.dex += 5;
            playerStat.con += 5;
        }
        
        
        double hp = playerStat.hp;

        double onePercent = hp/100;
        //regen
        if(playerStat.agr == 0){
           if(playerStat.currentHp < playerStat.hp){
               playerStat.currentHp = playerStat.currentHp + onePercent*0.3f;
           }
        }
        
        double percent = playerStat.currentHp/onePercent;
        
        

        playerProgress.setProgressValue(percent);
        
        
        if(isAttack){
            if(toStandTime < System.currentTimeMillis() ){
                playerVisual.anim.setCurrentAction("stand");

                isAttack = false;
            }
        }
        walkDirection.set(0, 0, 0);
        if(moveTo != null){
           
            //set loock at mark
            
            //get player position
            Vector3f playerPosition = playerVisual.characterControl.getPhysicsLocation();
            float distance = playerPosition.distance(moveTo);
            if(distance >= 1f){
                if(playerVisual.isRun == false){
                    playerVisual.anim.setCurrentAction("run");
                    playerVisual.isRun = true;
                }
                
                Vector3f direction = moveTo.subtract(playerPosition).normalize();
                
                Vector3f  correction = new Vector3f(direction.x,direction.y+0.15f,direction.z);
                
                playerVisual.characterControl.setViewDirection(correction);

                walkDirection.addLocal(direction);
                playerVisual.characterControl.setWalkDirection(walkDirection.mult(0.2f));
                
            }else{
                
                if(isAttack == false){
                    playerVisual.anim.setCurrentAction("stand");
                }
               // System.out.println("in point");
                walkDirection.set(0, 0, 0);
                playerVisual.characterControl.setWalkDirection(walkDirection.mult(0.2f));
                playerVisual.isRun = false;
            }
        }
        
    }
    
    private boolean isAttack = false;
    
    @Override
    protected void cleanup( Application app ) {
       // System.out.println("I'm being cleaned up:" + this);
    }
    
    @Override
    protected void onEnable() {
       // System.out.println("I'm being enabled:" + this);
    }
    
    @Override
    protected void onDisable() {
       // System.out.println("I'm being disabled:" + this);
    }

}
