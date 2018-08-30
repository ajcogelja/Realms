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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;

import static java.awt.event.KeyEvent.*;

public class Game {
    Map map;
    private boolean up, down, left, right = false;
    int move = 2;
    Player player;
    Image[] sprites = new Image[3];

    public Game() {
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
            System.out.println("Map Loaded");
        } catch (Exception e) {
            System.out.println("Map failed to load");
        }
        //System.out.println(map.getFilename());
        Canvas canvas = new MapView(map);
        Panel panel = new Panel();
        JFrame window = new JFrame("Window");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setFocusable(true);
        panel.add(canvas);
        window.add(panel);
        window.setSize(1000, 700);
        window.setVisible(true);
        canvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println(e.getKeyChar());
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
                        player.repaint();
                        if (up) {
                            canvas.setLocation(canvas.getX(), canvas.getY() + 2);
                        }
                        if (down) {
                            canvas.setLocation(canvas.getX(), canvas.getY() - 2);
                        }
                        if (left) {
                            canvas.setLocation(canvas.getX() + 2, canvas.getY());
                        }
                        if (right) {
                            canvas.setLocation(canvas.getX() - 2, canvas.getY());
                        }
                        //canvas.repaint();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                        panel.repaint();
                        player.getFrame();
                        player.repaint();
                }
            }
        };

        gameLoop.start();

        System.out.println("Initialized");

    }

    class MapView extends Canvas implements Scrollable {
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

        public void paint(Graphics g) {
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
            g2.drawImage(player.getSprites()[player.getFrame()], 100, 100, null);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            if (orientation == SwingConstants.HORIZONTAL)
                return map.getTileWidth();
            else
                return map.getTileHeight();
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            if (orientation == SwingConstants.HORIZONTAL) {
                final int tileWidth = map.getTileWidth();
                return (visibleRect.width / tileWidth - 1) * tileWidth;
            } else {
                final int tileHeight = map.getTileHeight();
                return (visibleRect.height / tileHeight - 1) * tileHeight;
            }
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
