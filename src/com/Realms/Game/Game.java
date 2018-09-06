package com.Realms.Game;

import javafx.animation.AnimationTimer;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.HexagonalRenderer;
import org.mapeditor.view.IsometricRenderer;
import org.mapeditor.view.MapRenderer;
import org.mapeditor.view.OrthogonalRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;


public class Game{
    Map map;
    private boolean up, down, left, right = false;
    int move = 1;
    Player player;
    BufferedImage[] sprites = new BufferedImage[3];
    int xOffset, yOffset = 0;

    public Game()  {
        try {
            sprites[0] = ImageIO.read(new FileInputStream("src\\com\\Realms\\Game\\PlayerTest" + "1.png"));
            sprites[1] = ImageIO.read(new FileInputStream("src\\com\\Realms\\Game\\PlayerTest" + "2.png"));
            sprites[2] = ImageIO.read(new FileInputStream("src\\com\\Realms\\Game\\PlayerTest" + "3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new Player("Player", sprites, 100, 100);
        try {
            TMXMapReader reader = new TMXMapReader();
            //System.out.println(new File(loc + "realms_dungeon_crypt.tmx").toPath().toString() + '\n' + "Does the file exist: " + new File("com.Realms.Game/realms_dungeon_crypt.tmx").exists());
            map = reader.readMap("src\\com\\Realms\\Game\\realms_dungeon_crypt.tmx");
            xOffset = map.getLayer(0).getOffsetX();
            yOffset = map.getLayer(0).getOffsetY();
            System.out.println("Map Loaded");
        } catch (Exception e) {
            System.out.println("Map failed to load");
        }
        //System.out.println(map.getFilename());
        JPanel panel = new MapView(map);
        JFrame window = new JFrame("Window");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //window.setFocusable(true);
        window.add(panel);
        window.setSize(1000, 700);
        window.setVisible(true);
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'W'){
                    for (MapLayer m: map.getLayers()) {
                        m.translate(0, 1);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println(e.getKeyChar());
               /* try {
                    //up = down = left = right = false;
                    Thread.sleep(4);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }*/
                 switch (e.getKeyCode()) {
                    case 'W':
                        //case VK_UP:
                        up = true;
                        down = false;
                        break;
                    case 'A':
                        //case VK_LEFT:
                        left = true;
                        right = false;
                        break;
                    case 'S':
                        //case VK_DOWN:
                        down = true;
                        up = false;
                        break;
                    case 'D':
                        //case VK_RIGHT:
                        right = true;
                        left = false;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 'W':
                    //case VK_UP:
                        up = false;
                        break;
                    case 'A':
                    //case VK_LEFT:
                        left = false;
                        break;
                    case 'S':
                    //case VK_DOWN:
                        down = false;
                        break;
                    case 'D':
                    //case VK_RIGHT:
                        right = false;
                        break;
                }
            }
        });


        Thread gameLoop = new Thread() {
            @Override
            public void run() {

                while(true) {
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (up) {
                            yOffset += move;
                            for (MapLayer m: map.getLayers()) {
                                m.translate(0, move);
                            }
                            System.out.println("up");
                            //up = false;
                        }
                        if (down) {
                            yOffset -= move;
                            for (MapLayer m: map.getLayers()) {
                                    m.translate(0, -move);
                            }
                            System.out.println("down");
                            //down = false;
                        }
                        if (left) {
                            xOffset += move;
                            for (MapLayer m : map.getLayers()) {
                                m.translate(move, 0);
                            }
                            //left = false;
                        }
                        if (right) {
                            xOffset -= move;
                            for (MapLayer m: map.getLayers()) {
                                m.translate(-move, 0);
                            }
                            //right = false;
                        }
                    right = left = up = down = false;


                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Timer timer = new Timer(160, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //panel.revalidate();
                panel.repaint();
                player.setFrame(player.getFramesLength() - 1 - player.getFrame());
                player.repaint();
            }
        });
        timer.setRepeats(true);
        timer.start();

        gameLoop.setPriority(Thread.MAX_PRIORITY);
        gameLoop.start();

        System.out.println("Initialized");

    }

    class MapView extends JPanel{
        private final Map map;
        private MapRenderer mp;


        MapView(Map map) {
            this.map = map;
            switch (map.getOrientation()) {
                case ORTHOGONAL:
                    mp = new OrthogonalRenderer(map);
                    break;
                case ISOMETRIC:
                    mp = new IsometricRenderer(map);
                    break;
                case HEXAGONAL:
                    mp = new HexagonalRenderer(map);
                    break;
                default:
                    mp = null;
                    break;
            }
            //mp = new IsometricRenderer(map);
            setPreferredSize(mp.getMapSize());
        }

        public void paintComponent(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g.create();
            final Rectangle clip = g2.getClipBounds();
            g2.scale(.62, .6);
            g2.fill(clip);
            for (MapLayer mapLayer : map.getLayers()) {
                if (mapLayer instanceof TileLayer) {
                    mp.paintTileLayer(g2, (TileLayer) mapLayer);
                } else if (mapLayer instanceof ObjectGroup) {
                    mp.paintObjectGroup(g2, (ObjectGroup) mapLayer);
                }
            }
            g2.scale(1.7, 1.7);
            g2.drawImage(player.getCurrentFrame(), 100, 100, null);
            //player.nextFrame();
        }
    }
}
