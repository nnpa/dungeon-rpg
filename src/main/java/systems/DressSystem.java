/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package systems;

import com.jme3.anim.Armature;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;
import components.PlayerDress;
import components.PlayerStat;
import components.PlayerVisual;
import dungeonrpg.Item;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ivan
 */
public class DressSystem {
    PlayerVisual playerVisual;
    PlayerStat playerStat;
    PlayerDress playerDress;
    InputManager inputManager;
    Node rootNode;
    public Container itemsWindow = new Container("Items");
    public Container infoWindow = new Container("Info");
    public Container invWindow = new Container("Inventory");
    public Container statWindow = new Container("Stat");

    AssetManager assetManager;

    
    public DressSystem(PlayerVisual playerVisual,PlayerStat playerStat,PlayerDress playerDress,Node rootNode,InputManager inputManager,AssetManager assetManager){
        
        this.playerVisual = playerVisual;
        this.playerStat = playerStat;
        this.playerDress = playerDress;
        this.rootNode = rootNode;
        this.inputManager = inputManager;
        this.assetManager = assetManager;

      
        player = (Node)playerVisual.spatial;
       
        playerArmature = (Node) player.getChild("human");
        
        skinningControl =  playerArmature.getControl(SkinningControl.class);   
        
        
    }
    
    private void applyItem(Item item){
        
        playerStat.hp = playerStat.hp + item.hp;
        playerStat.dex = playerStat.dex + item.dex;
        playerStat.str = playerStat.str + item.str;
        playerStat.con = playerStat.con + item.con;
        playerStat.def = playerStat.def + item.def;
        playerStat.damage = playerStat.damage + item.def;

    }
    
    private void cancelItem(Item item){
        
        playerStat.hp = playerStat.hp - item.hp;
        playerStat.dex = playerStat.dex - item.dex;
        playerStat.str = playerStat.str - item.str;
        playerStat.con = playerStat.con - item.con;
        playerStat.def = playerStat.def - item.def;
        playerStat.damage = playerStat.damage - item.def;
    }
    
    Button buttonHelmet;
    Button buttonWeapon;
    Button buttonArmor;
    Button buttonShild;
    Button buttonBrass;
    Button buttonLeg;
    Button buttonBoots;

    Node player;
    Node playerArmature;
    SkinningControl skinningControl;   
    

    Command<Button> dressCommand;
    Command<Button> invCommand;
    private int toremove1 = 0;
    
    
    public void createButton(int indx){
        Button button = new Button("");
        button.setIcon(new IconComponent("Textures/empty.jpg"));

        Item item = playerDress.items.get(indx);

        if(item != null){
            button.setIcon(new IconComponent("Textures/"+item.icon +".jpg"));
            button.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                infoLabel.setText(item.description);
            });

            invCommand = new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    if(isTrash == false){
                        applyItem(item);

                        buttonLogic( item, source,indx);
                    }else{
                        playerDress.items.put(indx,null);
                        reloadInv();
                        setDefaultCursor();
                        isTrash = false;
                        
                    }
                }
            };
            button.addClickCommands(invCommand);
        }
        
        
        
        button.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
            infoLabel.setText("");
        });
        
        int row = 0;
        int column = 0;
        int k =0;
        for(int i =0;i<3;i++){
            for(int j=0;j<3;j++){
                if(k ==indx){
                    row = i;
                    column =j;
                }
                k++;
            }
        }
        invWindow.addChild(button, row,column);
        
    }
    public void reloadInv(){
        invWindow.detachAllChildren();
        statWindow.addChild(new Label("Level: " + playerStat.level),0,0);
        statWindow.addChild(new Label("Str: " + playerStat.str),1,0);
        statWindow.addChild(new Label("Dex: " + playerStat.dex),2,0);
        statWindow.addChild(new Label("Con: " + playerStat.con),3,0);
        statWindow.addChild(new Label("Exp: " + playerStat.exp + "/" + playerStat.nextLevel),4,0);
        
        invWindow.detachAllChildren();
        createButton(0);
        createButton(1);
        createButton(2);
        createButton(3);
        createButton(4);
        createButton(5);
        createButton(6);
        createButton(7);
        createButton(8);
        
        Button trashButton  = new Button("");
        trashButton.setIcon(new IconComponent("Textures/trash.jpg"));
        Command<Button> command = new Command<Button>() {
            @Override
            public void execute( Button source ) {
                Texture cursorTexture = assetManager.loadTexture("Textures/action.png");
                Image image = cursorTexture.getImage();
                ByteBuffer imgByteBuff = image.getData(0);
                imgByteBuff.rewind();
                IntBuffer imgIntBuff = imgByteBuff.asIntBuffer();
                
                JmeCursor c = new JmeCursor();
                c.setHeight(image.getHeight());
                c.setWidth(image.getWidth());
                c.setNumImages(1);
                c.setyHotSpot(image.getHeight()-3);
                c.setxHotSpot(3);
                c.setImagesData(imgIntBuff);
                
                inputManager.setMouseCursor(c);
                isTrash = true;
            }
        };
        trashButton.addClickCommands(command);
        invWindow.addChild(trashButton);
    }
    public boolean isTrash = false;
    
    public void setDefaultCursor(){
         Texture cursorTexture = assetManager.loadTexture("Textures/default.png");
            Image image = cursorTexture.getImage();
            ByteBuffer imgByteBuff = image.getData(0);
            imgByteBuff.rewind();
            IntBuffer imgIntBuff = imgByteBuff.asIntBuffer();

            JmeCursor c = new JmeCursor();
            c.setHeight(image.getHeight());
            c.setWidth(image.getWidth());
            c.setNumImages(1);
            c.setyHotSpot(image.getHeight()-3);
            c.setxHotSpot(3);
            c.setImagesData(imgIntBuff);

            inputManager.setMouseCursor(c);
    }
    
    private void buttonLogic(Item item,Button source,int indx){
        if(item.type == "helmet"){ 
            if(playerDress.helmet == null){
                playerDress.helmet = item;
                buttonHelmet.setIcon(new IconComponent("Textures/"+playerDress.helmet.icon +".jpg"));
                
                
                buttonHelmet.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.helmet != null){
                        infoLabel.setText(playerDress.helmet.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.helmet.loadSpatial();
                attachItem(playerDress.helmet.spatial,"helm");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.helmet != null){
                            detachItem("helm");
                            cancelItem(playerDress.helmet);
                            playerDress.addItem(playerDress.helmet);
                            playerDress.helmet = null;
                            buttonHelmet.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonHelmet.removeClickCommands(dressCommand);
                            reloadInv();
                        }else{

                        }
                     }

                };
                buttonHelmet.addClickCommands(dressCommand);
            }
        }
        
        if(item.type == "leg"){ 
            if(playerDress.leggings == null){

                playerDress.leggings = item;
                buttonLeg.setIcon(new IconComponent("Textures/"+playerDress.leggings.icon +".jpg"));

                
                buttonLeg.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.leggings != null){
                        infoLabel.setText(playerDress.leggings.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.leggings.loadSpatial();
                attachItem(playerDress.leggings.spatial,"leg");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.leggings != null){
                            cancelItem(playerDress.leggings);
                            detachItem("leg");
                            playerDress.addItem(playerDress.leggings);
                            playerDress.leggings = null;
                            buttonLeg.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonLeg.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }

                };
                buttonLeg.addClickCommands(dressCommand);
            }
        }
        if(item.type == "boots"){ 

            if(playerDress.boots == null){
                playerDress.boots = item;
                buttonBoots.setIcon(new IconComponent("Textures/"+playerDress.boots.icon +".jpg"));

                
                buttonBoots.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.boots != null){
                        infoLabel.setText(playerDress.boots.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.boots.loadSpatial();
                attachItem(playerDress.boots.spatial,"boots");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.boots != null){
                            cancelItem(playerDress.boots);

                            detachItem("boots");
                            playerDress.addItem(playerDress.boots);
                            playerDress.boots = null;
                            buttonBoots.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonBoots.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }

                };
                buttonBoots.addClickCommands(dressCommand);
            }
        }
        
        if(item.type == "armor"){ 

            if(playerDress.armor == null){
                playerDress.armor = item;
                buttonArmor.setIcon(new IconComponent("Textures/"+playerDress.armor.icon +".jpg"));

                
                buttonArmor.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.armor != null){
                        infoLabel.setText(playerDress.armor.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.armor.loadSpatial();
                attachItem(playerDress.armor.spatial,"armor");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.armor != null){
                            cancelItem(playerDress.armor);

                            detachItem("armor");
                            playerDress.addItem(playerDress.armor);
                            playerDress.armor = null;
                            buttonArmor.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonArmor.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }
                };
                buttonArmor.addClickCommands(dressCommand);
            }
        }
        
        if(item.type == "boots"){ 

            if(playerDress.boots == null){
                playerDress.boots = item;
                buttonBoots.setIcon(new IconComponent("Textures/"+playerDress.boots.icon +".jpg"));

                
                buttonBoots.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.boots != null){
                        infoLabel.setText(playerDress.boots.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.boots.loadSpatial();
                attachItem(playerDress.boots.spatial,"boots");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.boots != null){
                            cancelItem(playerDress.boots);

                            detachItem("boots");
                            playerDress.addItem(playerDress.boots);
                            playerDress.boots = null;
                            buttonBoots.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonBoots.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }

                };
                buttonBoots.addClickCommands(dressCommand);
            }
        }
        
        if(item.type == "barassers"){ 

            if(playerDress.brassers == null){
                playerDress.brassers = item;
                buttonBrass.setIcon(new IconComponent("Textures/"+playerDress.brassers.icon +".jpg"));

                
                buttonBrass.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.brassers != null){
                        infoLabel.setText(playerDress.brassers.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.brassers.loadSpatial();
                attachItem(playerDress.brassers.spatial,"bras");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.brassers != null){
                            cancelItem(playerDress.brassers);
                            detachItem("bras");
                            playerDress.addItem(playerDress.brassers);
                            playerDress.brassers = null;
                            buttonBrass.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonBrass.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }

                };
                buttonBrass.addClickCommands(dressCommand);
            }
        }
        
        if(item.type == "weapon"){ 

            if(playerDress.weapon == null){
                playerDress.weapon = item;
                buttonWeapon.setIcon(new IconComponent("Textures/"+playerDress.weapon.icon +".jpg"));

                
                buttonBrass.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.weapon != null){
                        infoLabel.setText(playerDress.weapon.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.weapon.loadSpatial();
                attachItem(playerDress.weapon.spatial,"weapon");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.weapon != null){
                            cancelItem(playerDress.weapon);
                            detachItem("weapon");
                            playerDress.addItem(playerDress.weapon);
                            playerDress.weapon = null;
                            buttonBrass.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonBrass.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }

                };
                buttonBrass.addClickCommands(dressCommand);
            }
        }
        
        if(item.type == "shild"){ 

            if(playerDress.shild == null){
                playerDress.shild = item;
                buttonShild.setIcon(new IconComponent("Textures/"+playerDress.shild.icon +".jpg"));

                
                buttonShild.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                    if(playerDress.shild != null){
                        infoLabel.setText(playerDress.shild.description);
                    }
                });
                playerDress.items.put(indx,null);
                playerDress.shild.loadSpatial();
                attachItem(playerDress.shild.spatial,"shild");
                source.setIcon(new IconComponent("Textures/empty.jpg"));
                source.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button btn) -> {
                     infoLabel.setText("");
                });
                infoLabel.setText("");
                
                reloadPlayer();
                dressCommand = new Command<Button>() {
                    @Override
                    public void execute( Button source ) {
                        if(playerDress.shild != null){
                            cancelItem(playerDress.shild);
                            detachItem("shild");
                            playerDress.addItem(playerDress.shild);
                            playerDress.shild = null;
                            buttonShild.setIcon(new IconComponent("Textures/empty.jpg"));
                            buttonShild.removeClickCommands(dressCommand);
                            reloadInv();
                        }
                     }

                };
                buttonShild.addClickCommands(dressCommand);
            }
        }
       
    }
    
    public void init(){
        
        invWindow.setLocalTranslation(600, 300, 1);
        itemsWindow.setLocalTranslation(300, 300, 2);
        infoWindow.setLocalTranslation(400, 400, 3);
        statWindow.setLocalTranslation(400, 600, 3);
        
        
        
        
        
        //inint inventory and player deress
        
        buttonHelmet = new Button("");
        buttonHelmet.setIcon(new IconComponent("Textures/empty.jpg"));
        itemsWindow.addChild(buttonHelmet, 0,1);
        

        if(playerDress.helmet != null){
            applyItem(playerDress.helmet);

            buttonHelmet.setIcon(new IconComponent("Textures/"+playerDress.helmet.icon +".jpg"));
            attachItem(playerDress.helmet.spatial,"helm");
            buttonHelmet.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                infoLabel.setText(playerDress.helmet.description);
            });
            dressCommand = new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    if(playerDress.helmet != null){
                        playerDress.addItem(playerDress.helmet);
                        reloadInv();
                        buttonHelmet.setIcon(new IconComponent("Textures/empty.jpg"));
                        cancelItem(playerDress.helmet);

                        playerDress.helmet = null;
                        detachItem("helm");
                        buttonHelmet.removeClickCommands(dressCommand);
                    }
                }
                
            };
            buttonHelmet.addClickCommands(dressCommand);

        }
        
        buttonWeapon = new Button("");
        buttonWeapon.setIcon(new IconComponent("Textures/empty.jpg"));
        itemsWindow.addChild(buttonWeapon, 1,0);
        
        if(playerDress.weapon != null){
            applyItem(playerDress.weapon);

            buttonWeapon.setIcon(new IconComponent("Textures/"+playerDress.weapon.icon +".jpg"));
            attachItem(playerDress.weapon.spatial,"weapon");
            playerDress.weapon.spatial = null;
            buttonWeapon.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                if(playerDress.weapon != null){
                    infoLabel.setText(playerDress.weapon.description);
                }
            });
            dressCommand = new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    if(playerDress.weapon != null){
                        playerDress.addItem(playerDress.weapon);
                        reloadInv();
                        cancelItem(playerDress.weapon);

                        detachItem("weapon");
                        buttonWeapon.setIcon(new IconComponent("Textures/empty.jpg"));
                        playerDress.weapon = null;
                        buttonWeapon.removeClickCommands(dressCommand);
                    }
                }
                
            };
            buttonWeapon.addClickCommands(dressCommand);

        }
        
        buttonArmor = new Button("");

        buttonArmor.setIcon(new IconComponent("Textures/empty.jpg"));
        
          itemsWindow.addChild(buttonArmor, 1,1);
         
        if(playerDress.armor != null){
            applyItem(playerDress.armor);

            buttonArmor.setIcon(new IconComponent("Textures/"+playerDress.armor.icon +".jpg"));
           
            buttonArmor.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                if(playerDress.armor != null){
                    infoLabel.setText(playerDress.armor.description);
                }
            });
            dressCommand = new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    if(playerDress.armor != null){
                        if(playerDress.armor != null){
                            playerDress.addItem(playerDress.armor);

                            reloadInv();
                            cancelItem(playerDress.armor);

                            detachItem("armor");

                            buttonArmor.setIcon(new IconComponent("Textures/empty.jpg"));
                            playerDress.armor = null;
                            buttonArmor.removeClickCommands(dressCommand);
                        }
                    }
                }
                
            };
            buttonArmor.addClickCommands(dressCommand);
           attachItem(playerDress.armor.spatial,"armor");
            playerDress.armor.spatial = null;
        }
       
        buttonShild = new Button("");
        buttonShild.setIcon(new IconComponent("Textures/empty.jpg"));
        itemsWindow.addChild(buttonShild, 1,2);

        
        
        if(playerDress.shild != null){
            applyItem(playerDress.shild);
            buttonShild.setIcon(new IconComponent("Textures/"+playerDress.shild.icon +".jpg"));
            buttonShild.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                if(playerDress.shild != null){
                    infoLabel.setText(playerDress.shild.description);
                }
            });
            dressCommand = new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    if(playerDress.shild != null){
                        cancelItem(playerDress.shild);

                        playerDress.addItem(playerDress.shild);
                        reloadInv();
                        buttonShild.setIcon(new IconComponent("Textures/empty.jpg"));
                        playerDress.shild = null;
                        buttonShild.removeClickCommands(dressCommand);
                        detachItem("shild");
                    }
                }
            };
            buttonShild.addClickCommands(dressCommand);
            attachItem(playerDress.shild.spatial,"shild");
            playerDress.shild.spatial = null;
        }
        
        buttonBrass = new Button("");
        buttonBrass.setIcon(new IconComponent("Textures/empty.jpg"));
        itemsWindow.addChild(buttonBrass, 3,0);

        if(playerDress.brassers != null){
            applyItem(playerDress.brassers);

            buttonBrass.setIcon(new IconComponent("Textures/"+playerDress.brassers.icon +".jpg"));
            buttonBrass.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
               if(playerDress.brassers != null){ 
                infoLabel.setText(playerDress.brassers.description);
               }
            });
            dressCommand = new Command<Button>() {
            @Override
                public void execute( Button source ) {
                    if(playerDress.brassers != null){
                        cancelItem(playerDress.brassers);

                        playerDress.addItem(playerDress.brassers);
                        reloadInv();
                        buttonBrass.setIcon(new IconComponent("Textures/empty.jpg"));
                        playerDress.brassers = null;
                        buttonBrass.removeClickCommands(dressCommand);
                        detachItem("bras");
                    }
                }
            };
            buttonBrass.addClickCommands(dressCommand);
            attachItem(playerDress.brassers.spatial,"bras");
            playerDress.brassers.spatial = null;
        }
        
        
        buttonLeg = new Button("");
        buttonLeg.setIcon(new IconComponent("Textures/empty.jpg"));
        itemsWindow.addChild(buttonLeg, 3,1);

        if(playerDress.leggings != null){
            applyItem(playerDress.leggings);

            buttonLeg.setIcon(new IconComponent("Textures/"+playerDress.leggings.icon +".jpg"));
            buttonLeg.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                if(playerDress.leggings != null){
                    infoLabel.setText(playerDress.leggings.description);

                }
            });
            dressCommand = new Command<Button>() {
            @Override
                public void execute( Button source ) {
                    if(playerDress.leggings != null){
                       cancelItem(playerDress.leggings);
                        playerDress.addItem(playerDress.leggings);
                        reloadInv();
                        buttonLeg.setIcon(new IconComponent("Textures/empty.jpg"));
                        playerDress.leggings = null;
                        buttonLeg.removeClickCommands(dressCommand);
                        detachItem("leg");
                    }
                }
            };
            buttonLeg.addClickCommands(dressCommand);
            attachItem(playerDress.leggings.spatial,"leg");
            playerDress.leggings.spatial = null;
        }
        
        buttonBoots = new Button("");
        buttonBoots.setIcon(new IconComponent("Textures/empty.jpg"));
        itemsWindow.addChild(buttonBoots, 3,2);

        if(playerDress.boots != null){
            applyItem(playerDress.boots);

            buttonBoots.setIcon(new IconComponent("Textures/"+playerDress.boots.icon +".jpg"));
            attachItem(playerDress.boots.spatial,"boots");
            playerDress.boots.spatial = null;
            dressCommand = new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    if(playerDress.boots != null){
                        cancelItem(playerDress.boots);

                        playerDress.addItem(playerDress.boots);
                        reloadInv();
                        buttonBoots.setIcon(new IconComponent("Textures/empty.jpg"));
                        playerDress.boots = null;
                        buttonBoots.removeClickCommands(dressCommand);
                        detachItem("boots");
                    }

                }
            };
            buttonBoots.addClickCommands(dressCommand);
            buttonBoots.addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
              if(playerDress.boots != null){
                infoLabel.setText(playerDress.boots.description);
              }
            });
        }
        
        reloadPlayer();
        
        infoWindow.addChild(infoLabel);

        
        buttonBoots.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        });
        buttonBrass.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        }); 
        buttonShild.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        });    
        buttonArmor.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        });   
        buttonWeapon.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        });  
        buttonHelmet.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        });  
        buttonLeg.addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                    infoLabel.setText("");
        });  
        reloadInv();
        
        
    }
    
    private Label infoLabel = new Label("");
    
    

    public void attachItem(Spatial spatial,String geoName){
        
       Node itemNode = (Node)spatial;
       Geometry geo = getGeometry(itemNode);
       geo.setName(geoName);
        
        playerArmature.attachChild(geo);
        
    }
    
    public void detachItem(String geoName){
        Geometry geo = (Geometry) playerArmature.getChild(geoName);
        
        playerArmature.detachChild(geo); 
    }
    
    public void reloadPlayer(){
        if(skinningControl != null){
             Armature armature = skinningControl.getArmature();
             playerArmature.removeControl(skinningControl);

             playerArmature.addControl(new SkinningControl(armature));
        }
    }
    
    public Geometry getGeometry(Node itemNode){

        Geometry geo = (Geometry)itemNode.getChild("mesh");
        return geo;
    }
    
    
    public Geometry getMyGeometry(Spatial spatial){
            //System.out.println("getMyGeometry()");
            Node node = (Node)spatial;
            Geometry g = null;

            final List<Spatial> ants = new LinkedList<Spatial>();
            //node.breadthFirstTraversal(new SceneGraphVisitor() {
            node.depthFirstTraversal(new SceneGraphVisitor() {
                    @Override
                    public void visit(Spatial spatial) {
                            //System.out.println("visit class is " + spatial.getClass().getName());
                            //System.out.println("visit spatial is " + spatial);
                            if (spatial.getClass().getName().equals("com.jme3.scene.Geometry")) {
                                    ants.add(spatial);
                            }
                    }
            });
            if (!ants.isEmpty()) {
                    //redundant - borrowed from Quixote TerrainTrackControl
                    for (int i = 0;i < ants.size();i++){
                            if (ants.get(i).getClass().getName().equals("com.jme3.scene.Geometry")){
                                    g = (Geometry)ants.get(i);
                                    //System.out.println("g (" + i + "/" + (ants.size() - 1) + ")=" + g);
                                    return(g);
                            }
                    }
            }
            else
            {
                    System.out.println("getMyGeometry()-Geometry not found");
            }
            return(g);
	}
}
