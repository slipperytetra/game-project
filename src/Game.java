//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.Timer;

public class Game extends GameEngine {
    private int LEVEL = 0;
    private int yRectangle;
    private Image bg;
    private Image key;
    private Image floor;
    private Image door;
    private Image dummy;
    private Image keyImage;
    private Image[] runFrames;
    private Image idle;
    private int currentFrameIndex;
    private Random mRandom;
    private double velY;
    private Timer animationTimer;
    private Timer startAnimationTimer;
    private Set<Integer> keysPressed = new HashSet();
    Player player;
    Level demoLevel;
    Image gifImage = Toolkit.getDefaultToolkit().createImage("resources/keyy.gif");
    Image gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/keyy.gif");
    Image level1 = Toolkit.getDefaultToolkit().createImage("resources/level1.gif");

    public Game() {
    }

    public static void main(String[] args) {
        createGame(new Game());
    }

    public void update(double dt) {
        this.processMovement();
        if (this.player.isJumping()) {
            this.velY += this.demoLevel.getGravity();
            this.player.getLocation().setY(this.player.getLocation().getY() + this.velY);
            if (this.player.getLocation().getY() >= (double)(this.yRectangle - this.idle.getHeight((ImageObserver)null))) {
                this.player.getLocation().setY((double)(this.yRectangle - this.idle.getHeight((ImageObserver)null)));
                this.player.setJumping(false);
                this.velY = 0.0;
            }
        } else {
            this.player.getLocation().setY(this.player.getLocation().getY() + this.velY);
            if (this.player.getLocation().getY() > (double)(this.yRectangle - this.idle.getHeight((ImageObserver)null))) {
                this.player.getLocation().setY((double)(this.yRectangle - this.idle.getHeight((ImageObserver)null)));
                this.velY = 0.0;
            }
        }

    }

    public void init() {
        this.player = new Player();
        this.demoLevel = new Level(0, new Location(0.0, 0.0), new Location(20.0, 20.0));
        this.setWindowSize(1500, 600);
        this.bg = this.loadImage("resources/background.jpg");
        this.mRandom = new Random();
        this.loadRunFrames("run");
        this.idle = this.loadImage("resources/idle.png");
        this.key = this.loadImage("resources/key.png");
        this.keyImage = this.loadImage("resources/keyy.gif");
        this.floor = this.loadImage("resources/floor.png");
        this.door = this.loadImage("resources/door.png");
        this.dummy = this.loadImage("resources/dummy.png");
        int rectangleHeight = -50;
        int windowHeight = this.height();
        this.yRectangle = windowHeight - rectangleHeight;
        this.player.getLocation().setX(0.0);
        this.player.getLocation().setY((double)(this.idle.getHeight((ImageObserver)null) + 480));
        System.out.println("Starting X position: " + this.player.getLocation().getX());
        System.out.println("Starting Y position: " + this.player.getLocation().getY());
        this.animationTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.this.currentFrameIndex = (Game.this.currentFrameIndex + 1) % Game.this.runFrames.length;
                Game.this.mFrame.repaint();
            }
        });
        this.startAnimationTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.this.animationTimer.start();
            }
        });
        if (this.LEVEL == 1) {
            this.player.getLocation().setX(0.0);
            this.player.getLocation().setY((double)(this.idle.getHeight((ImageObserver)null) + 480));
        }

    }

    private void loadRunFrames(String prefix) {
        this.runFrames = new Image[4];

        for(int i = 0; i < 4; ++i) {
            this.runFrames[i] = this.loadImage("resources/" + prefix + i + ".png");
        }

    }

    private void jumpAnimation() {
        this.loadRunFrames("jump");
        this.animationTimer.start();
        this.animationTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Game.this.currentFrameIndex == Game.this.runFrames.length - 1) {
                    Game.this.animationTimer.stop();
                }

            }
        });
    }

    public void keyPressed(KeyEvent event) {
        this.keysPressed.add(event.getKeyCode());
        this.processMovement();
    }

    public void keyReleased(KeyEvent event) {
        this.keysPressed.remove(event.getKeyCode());
        this.processMovement();
    }

    private void processMovement() {
        this.player.setMoving(false);
        this.player.setAttacking(false);
        if (this.keysPressed.contains(68)) {
            if (this.player.isJumping() && (!this.player.isJumping() || !this.player.isMoving())) {
                this.player.getLocation().setX(this.player.getLocation().getX() + 8.0);
            } else {
                this.player.getLocation().setX(this.player.getLocation().getX() + 10.0);
            }

            this.player.setMoving(true);
            this.player.setFlipping(false);
            this.loadRunFrames("run");
        }

        if (this.keysPressed.contains(65)) {
            if (this.player.isJumping() && (!this.player.isJumping() || !this.player.isMoving())) {
                this.player.getLocation().setX(this.player.getLocation().getX() - 8.0);
            } else {
                this.player.getLocation().setX(this.player.getLocation().getX() - 10.0);
            }

            this.player.setMoving(true);
            this.player.setFlipping(true);
            this.loadRunFrames("run");
        }

        if (this.keysPressed.contains(32) && !this.player.isJumping()) {
            this.player.setJumping(true);
            this.jumpAnimation();
            this.velY = this.player.getJumpVelocity();
        }

        if (this.keysPressed.contains(81) && !this.player.hasRegisteredAttack()) {
            this.player.setAttacking(true);
            this.player.setAttackRegistered(true);
            this.animationTimer.stop();
            this.currentFrameIndex = 0;
            this.loadRunFrames("attack");
            this.animationTimer.start();
        }

        if (!this.player.isMoving() && !this.player.isJumping() && !this.player.isAttacking()) {
            this.animationTimer.stop();
            this.currentFrameIndex = 0;
        } else if (!this.animationTimer.isRunning()) {
            this.animationTimer.start();
        }

        if (this.keysPressed.contains(69) && this.player.isTouchingDoor()) {
            System.out.println("Entering door...");
            this.LEVEL = 1;
        }

        this.mFrame.repaint();
    }




    private void welcome() {
        this.drawText(100.0, 100.0, "Welcome to our game!");
        this.drawText(100.0, 300.0, "Press 'D' to move right and 'A' to move left.");
        this.drawText(100.0, 330.0, "Hold 'Q' to attack with your sword.");
        this.drawText(100.0, 360.0, "Press 'Space' to jump!");
        this.drawText(700.0, 400.0, "Grab key to unlock door to proceed to next level!");
        this.drawText(1250.0, 450.0, "Press 'E' on door to enter!");
    }

    public void paintComponent() {
        this.drawImage(this.bg, 0.0, 0.0);
        this.drawImage(this.door, 1400.0, 483.0, 100.0, 100.0);
        this.drawImage(this.dummy, 500.0, 483.0, 100.0, 100.0);
        int keyHitboxWidth = 50;
        int keyHitboxHeight = 50;
        int dHitboxW = 100;
        int dHitboxH = 60;
        Rectangle characterBox = new Rectangle((int)this.player.getLocation().getX() - 40, (int)this.player.getLocation().getY(), this.idle.getWidth((ImageObserver)null), this.idle.getHeight((ImageObserver)null));
        Rectangle keyBox = new Rectangle(900, 475, keyHitboxWidth, keyHitboxHeight);
        Rectangle dummyBox = new Rectangle(500, 483, dHitboxH, dHitboxW);
        if (characterBox.intersects(keyBox)) {
            this.player.setKeyObtained(true);
        }

        if (this.player.hasObtainedKey()) {
            this.keyImage = null;
            this.drawText(1400.0, 50.0, "Key: ");
            this.drawImage(this.gifImage2, 1420.0, -10.0, 100.0, 100.0);
        }

        Rectangle doorBox = new Rectangle(1400, 483, 100, 100);
        if (characterBox.intersects(doorBox) && this.player.hasObtainedKey()) {
            this.player.setTouchingDoor(true);
        }

        this.drawText(1400.0, 50.0, "Key: ");
        this.welcome();
        int rectangleHeight = 50;
        int windowHeight = this.height();
        int yRectangle = windowHeight - rectangleHeight;
        this.drawSolidRectangle(0.0, (double)yRectangle, 1500.0, (double)rectangleHeight);
        Rectangle characterAttackBox;
        if (this.player.isAttacking() && this.runFrames != null && this.runFrames.length > 0) {
            characterAttackBox = new Rectangle((int)this.player.getLocation().getX(), (int)this.player.getLocation().getY(), this.runFrames[this.currentFrameIndex].getWidth((ImageObserver)null), this.runFrames[this.currentFrameIndex].getHeight((ImageObserver)null));
            if (characterAttackBox.intersects(dummyBox) && !this.player.hasRegisteredAttack()) {
                System.out.println("Hit");
                this.player.setAttackRegistered(true);
            }

            if (this.player.isFlipping()) {
                this.drawImage(this.flipImageHorizontal(this.runFrames[this.currentFrameIndex]), this.player.getLocation().getX(), this.player.getLocation().getY());
            } else {
                this.drawImage(this.runFrames[this.currentFrameIndex], this.player.getLocation().getX(), this.player.getLocation().getY());
            }
        } else if (!this.player.isMoving() && !this.player.isJumping()) {
            this.drawImage(this.idle, this.player.getLocation().getX(), this.player.getLocation().getY());
        } else if (this.player.isFlipping()) {
            this.drawImage(this.flipImageHorizontal(this.runFrames[this.currentFrameIndex]), this.player.getLocation().getX(), this.player.getLocation().getY());
        } else {
            this.drawImage(this.runFrames[this.currentFrameIndex], this.player.getLocation().getX(), this.player.getLocation().getY());
        }

        if (this.keyImage != null) {
            this.drawImage(this.gifImage, 900.0, 475.0, 100.0, 100.0);
        }

        if (this.LEVEL == 1) {
            this.changeBackgroundColor(Color.BLACK);
            this.clearBackground(1500, 800);
            if (this.player.getLocation().getX() == 0.0 && this.player.getLocation().getY() == (double)(this.idle.getHeight((ImageObserver)null) + 480)) {
                this.player.getLocation().setX(0.0);
                this.player.getLocation().setY((double)(this.idle.getHeight((ImageObserver)null) + 480));
            }

            this.drawImage(this.bg, 0.0, 0.0);
            this.drawImage(this.gifImage, 900.0, 250.0, 100.0, 100.0);
            this.drawSolidRectangle(0.0, (double)yRectangle, 1500.0, (double)rectangleHeight);
            this.drawSolidRectangle(100.0, 450.0, 100.0, 25.0);
            if (this.player.isAttacking() && this.runFrames != null && this.runFrames.length > 0) {
                characterAttackBox = new Rectangle((int)this.player.getLocation().getX(), (int)this.player.getLocation().getY(), this.runFrames[this.currentFrameIndex].getWidth((ImageObserver)null), this.runFrames[this.currentFrameIndex].getHeight((ImageObserver)null));
                if (characterAttackBox.intersects(dummyBox) && !this.player.hasRegisteredAttack()) {
                    System.out.println("Hit");
                    this.player.setAttackRegistered(true);
                }

                if (this.player.isFlipping()) {
                    this.drawImage(this.flipImageHorizontal(this.runFrames[this.currentFrameIndex]), this.player.getLocation().getX(), this.player.getLocation().getY());
                } else {
                    this.drawImage(this.runFrames[this.currentFrameIndex], this.player.getLocation().getX(), this.player.getLocation().getY());
                }
            } else if (!this.player.isMoving() && !this.player.isJumping()) {
                this.drawImage(this.idle, this.player.getLocation().getX(), this.player.getLocation().getY());
            } else if (this.player.isFlipping()) {
                this.drawImage(this.flipImageHorizontal(this.runFrames[this.currentFrameIndex]), this.player.getLocation().getX(), this.player.getLocation().getY());
            } else {
                this.drawImage(this.runFrames[this.currentFrameIndex], this.player.getLocation().getX(), this.player.getLocation().getY());
            }
        }

    }

    private Image flipImageHorizontal(Image image) {
        BufferedImage bufferedImage = (BufferedImage)image;
        AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
        tx.translate((double)(-bufferedImage.getWidth((ImageObserver)null)), 0.0);
        AffineTransformOp op = new AffineTransformOp(tx, 1);
        return op.filter(bufferedImage, (BufferedImage)null);
    }
}
