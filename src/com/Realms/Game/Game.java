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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static java.awt.event.KeyEvent.*;

public class Game {
    Map map;
    private boolean up, down, left, right = false;
    float move = 0.2f;

    public Game() {
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
        window.setSize(2000, 1700);
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
                        canvas.repaint();
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

            g2.fill(clip);

            for (MapLayer mapLayer : map.getLayers()) {
                if (mapLayer instanceof TileLayer) {
                    mp.paintTileLayer(g2, (TileLayer) mapLayer);
                } else if (mapLayer instanceof ObjectGroup) {
                    mp.paintObjectGroup(g2, (ObjectGroup) mapLayer);
                }
            }
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
