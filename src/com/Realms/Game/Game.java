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
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class Game{

    Dimension display = Toolkit.getDefaultToolkit().getScreenSize();
    Map map;
    private boolean up, down, left, right = false;
    Player player;
    BufferedImage[] sprites = new BufferedImage[3];
    float xOffset, yOffset = 0f;
    ObjectGroup group = null;
    JFrame window;

    public Game()  {
        try {
            sprites[0] = ImageIO.read(new FileInputStream("src\\com\\Realms\\Game\\PlayerTest" + "1.png"));
            sprites[1] = ImageIO.read(new FileInputStream("src\\com\\Realms\\Game\\PlayerTest" + "2.png"));
            sprites[2] = ImageIO.read(new FileInputStream("src\\com\\Realms\\Game\\PlayerTest" + "3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new Player("Player", sprites, 600, 600);
        try {
            TMXMapReader reader = new TMXMapReader();
            //System.out.println(new File(loc + "realms_dungeon_crypt.tmx").toPath().toString() + '\n' + "Does the file exist: " + new File("com.Realms.Game/realms_dungeon_crypt.tmx").exists());
            map = reader.readMap("src\\com\\Realms\\Game\\realms_dungeon_crypt_test_02.tmx");
            //map = reader.readMap("C:\\Users\\op3er\\Documents\\Realms\\src\\com\\Realms\\Game\\realms_dungeon_crypt.tmx");
            System.out.println("Map Loaded");
        } catch (Exception e) {
            System.out.println("Map failed to load");
            System.exit(0);
        }
        group = (ObjectGroup) map.getLayer(1);
        System.out.println("This map contains " + group.getObjects().size() + " objects");
        JPanel panel = new MapView(map);
        window = new JFrame("Window"); //if breaks just remove declaration at the top and set up window here
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
            if (left && dx > -3){
                dx -= 1;
            }
            if (right && dx < 3){
                dx += 1;
            }
            if (up && dy > -3){
                dy -= 1;
            }
            if (down && dy < 3){
                dy += 1;
            }

            xOffset -= dx/14.0f;
            yOffset -= dy/14.0f;

            map.getLayer(0).setOffset((int)xOffset, (int)yOffset);

            //this is commented out because Im going to try nestalgia style movement where only the map moves
         /*if (player.getY() + (dy * delta) < window.getHeight() - 400
                && player.getY() + (dy * delta) > 400
                && player.getX() + (dx * delta) > 400
                && player.getX() + (dx * delta) < window.getWidth() - 400) {

                boolean collided = false;
                Rectangle rect = player.getBounds();
                rect.setRect(player.getX() + (dx * delta), player.getY() + (dy * delta), player.width, player.height);
                for (MapObject obj : group.getObjects()) {
                    if (obj.getShape().intersects(rect)) {
                        System.out.println("Collided with " + obj.getName());
                        collided = true;
                        break;
                    }

                }
                if (!collided) {
                    System.out.println(player.getX() + " " + player.getY());
                    //System.out.println(player.getX() + xOffset + " " + player.getY() + yOffset);
                    //System.out.println("Did not collide");
                        player.moveX((delta * dx));
                        player.moveY((delta * dy));
                }
        } else { //moving the map is broken currently
            for (MapLayer ml:map.getLayers()) {
                System.out.println(ml.getOffsetX() + " " + ml.getOffsetY());
                if (dx != 0) {
                    ml.translate((int) (-dx / Math.abs(dx)), 0);
                }
                if (dy != 0){
                    ml.translate(0, (int) (-dy / Math.abs(dy)));
                }
            }
        } */
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
            g2.fill(clip);
            for (MapLayer mapLayer : map.getLayers()) {
                if (mapLayer instanceof TileLayer) {
                    //g2.setColor(g2.getColor().darker());
                    mp.paintTileLayer(g2, (TileLayer) mapLayer);
                } else if (mapLayer instanceof ObjectGroup) {
                    mp.paintObjectGroup(g2, (ObjectGroup) mapLayer);
                }
            }
            g2.drawImage(player.getCurrentFrame(), player.getX(), player.getY(), null);
            //player.nextFrame();
        }
    }

   // public static void main(String[] args) {
   //     new Game();
   // }
}
