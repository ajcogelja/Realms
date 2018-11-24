package com.Realms.Game;

import org.mapeditor.core.*;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.HexagonalRenderer;
import org.mapeditor.view.IsometricRenderer;
import org.mapeditor.view.MapRenderer;
import org.mapeditor.view.OrthogonalRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class Game{

    Dimension display = Toolkit.getDefaultToolkit().getScreenSize();
    Map map;
    private boolean up, down, left, right = false;
    Player player;
    BufferedImage[] sprites = new BufferedImage[3];
    int xOffset, yOffset = 0;
    ObjectGroup group = null;

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
            map = reader.readMap("src\\com\\Realms\\Game\\realms_dungeon_crypt_02.tmx");
            //map = reader.readMap("C:\\Users\\op3er\\Documents\\Realms\\src\\com\\Realms\\Game\\realms_dungeon_crypt.tmx");
            System.out.println("Map Loaded");
        } catch (Exception e) {
            System.out.println("Map failed to load");
            System.exit(0);
        }
        //System.out.println(map.getFilename());
        group = new ObjectGroup(map);
        System.out.println(map.getLayerCount() + " layers");
        System.out.println("This map contains " + group.getObjects().size() + " objects");
        JPanel panel = new MapView(map);
        JFrame window = new JFrame("Window");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(panel);
        window.setSize(3*display.height/4, 3*display.height/4);
        window.setLocationRelativeTo(null);
        //window.setLocation(display.width/6, display.height/6);
        window.setVisible(true);
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {

                 switch (e.getKeyCode()) {
                    case 'W':
                        System.out.println("up");
                        up = true;
                        break;
                    case 'A':
                        System.out.println("left");
                        left = true;
                        break;
                    case 'S':
                        System.out.println("down");
                        down = true;
                        break;
                    case 'D':
                        System.out.println("right");
                        right = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 'W':
                    //case KeyEvent.VK_UP:
                        up = false;
                        break;
                    case 'A':
                    //case KeyEvent.VK_LEFT:
                        left = false;
                        break;
                    case 'S':
                    //case KeyEvent.VK_DOWN:
                        down = false;
                        break;
                    case 'D':
                    //case KeyEvent.VK_RIGHT:
                        right = false;
                        break;
                }
            }
        });


        Thread gameLoop = new Thread() {
            @Override
            public void run() {
                //control the time in between loops
                long last = System.nanoTime();

                while (true){
                    long delta = (System.nanoTime() - last) / 1000000;
                    last = System.nanoTime();
                    panel.repaint();
                    player.nextFrame();
                    player.repaint();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //for (int i = 0; i < delta/5; i++){
                        movement(5);
                    //}

                    if (delta%5 != 0){
                        movement(delta%5);
                    }

                }
            }
        };

        gameLoop.setPriority(Thread.MAX_PRIORITY);
        gameLoop.start();

        System.out.println("Initialized");

    }

    public void movement(long delta){
        float scale = .55f;
            int dx = 0;
            int dy = 0;
            if (left){
                dx -= 1;
            }
            if (right){
                dx += 1;
            }
            if (up){
                dy -= 1;
            }
            if (down){
                dy += 1;
            }

            boolean collided = false;
        Rectangle2D rect = new Rectangle();
        rect.setRect(player.getX() + (delta * dx * scale), player.getY() + (delta * dy * scale), player.width, player.height);
        for (MapObject obj:group.getObjects()) {
            System.out.println(obj.getName());
            if (obj.getShape().intersects(rect)){
                collided = true;
            }

        }
        if (!collided) {
            player.moveX(delta * dx * scale);
            player.moveY(delta * dy * scale);
        }
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
            g2.scale(1, 1);
            g2.fill(clip);
            for (MapLayer mapLayer : map.getLayers()) {
                if (mapLayer instanceof TileLayer) {
                    //g2.setColor(g2.getColor().darker());
                    mp.paintTileLayer(g2, (TileLayer) mapLayer);
                } else if (mapLayer instanceof ObjectGroup) {
                    mp.paintObjectGroup(g2, (ObjectGroup) mapLayer);
                }
            }
            g2.scale(1.7, 1.7);
            g2.drawImage(player.getCurrentFrame(), player.getX(), player.getY(), null);
            //player.nextFrame();
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
