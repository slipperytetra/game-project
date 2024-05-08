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
    private Image plantMonster;
    private Image idleMonster;
    private Image plantMonsterDef;
    private static final int HEALTH_DECREMENT = 4;
    // Define the cooldown duration in milliseconds
    private static final int COOLDOWN_DURATION = 500; // 2 seconds


    // Define the health timer and a boolean flag to track cooldown
    private Timer healthTimer;
    private boolean isCooldown = false;



    private Image key;
    private Image floor;
    private Image door;
    private Image dummy;
    private Image keyImage;
    private Image ground;
    private Image[] runFrames;
    private Image idle;
    private int currentFrameIndex;
    private Random mRandom;
    private double velY;
    private Timer animationTimer;
    private Timer startAnimationTimer;
    private Set<Integer> keysPressed = new HashSet();
    private boolean isAttacking;
    Player player;
    Image gifImage = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
    Image plantAttack = Toolkit.getDefaultToolkit().createImage("resources/images/plantAttack.gif");

    Image gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
    Image level1 = Toolkit.getDefaultToolkit().createImage("resources/images/level1.gif");
    LevelManager lvlManager;

    public static int WIDTH = 800;
    public static int HEIGHT = 800;

    public Game() {
    }

    public static void main(String[] args) {
        createGame(new Game());
    }

    public void update(double dt) {
        this.processMovement();
        if (this.player.isJumping()) {
            this.velY += lvlManager.DEMO.getGravity();
            this.player.moveY(velY);
            if (this.player.getLocation().getY() >= (double) (this.yRectangle - this.idle.getHeight((ImageObserver) null))) {
                this.player.getLocation().setY((double) (this.yRectangle - this.idle.getHeight((ImageObserver) null)));
                this.player.setJumping(false);
                this.velY = 0.0;
            }
        } else {
            this.player.moveY(this.velY);
            if (this.player.getLocation().getY() > (double) (this.yRectangle - this.idle.getHeight((ImageObserver) null))) {
                this.player.getLocation().setY((double) (this.yRectangle - this.idle.getHeight((ImageObserver) null)));
                this.velY = 0.0;
            }
        }

    }

    public void init() {
        this.setWindowSize(WIDTH, HEIGHT);
        this.player = new Player();
        this.lvlManager = new LevelManager(this);
        this.bg = this.loadImage("resources/images/background.jpg");
        this.mRandom = new Random();
        this.loadRunFrames("run");
        this.idleMonster = this.loadImage("resources/images/plantAttack.gif");
        this.idle = this.loadImage("resources/images/idle.png");
        this.key = this.loadImage("resources/images/key.png");
        this.keyImage = this.loadImage("resources/images/keyy.gif");
        this.floor = this.loadImage("resources/images/floor.png");
        this.door = this.loadImage("resources/images/door.png");
        this.ground = this.loadImage("resources/images/ground.png");
        this.plantMonster = this.loadImage("resources/images/plantMonster.png");
        this.plantMonsterDef = this.loadImage("resources/images/plantMonster.png");
        int keyHitboxWidth = 50;
        int keyHitboxHeight = 50;

        this.dummy = this.loadImage("resources/images/dummy.png");
        int rectangleHeight = -50;
        Rectangle plantBox = new Rectangle((int) lvlManager.DEMO.getPlantLoc().getX(), (int) lvlManager.DEMO.getPlantLoc().getY(), keyHitboxWidth, keyHitboxHeight);
        int windowHeight = this.height();
        this.yRectangle = windowHeight - rectangleHeight;
        this.player.getLocation().setX(10.0);
        this.player.getLocation().setY(514.0);


        //this.player.setLocation(lvlManager.DEMO.getSpawnPoint().getX(), lvlManager.DEMO.getSpawnPoint().getY());
        System.out.println(lvlManager.DEMO.getSpawnPoint().getY());
        int playerHeight = this.idle.getHeight((ImageObserver)null); // Get the height of the player's image
       // this.player.getLocation().setY((double)(windowHeight - playerHeight)); // Set the player's Y position to the bottom of the window
        System.out.println("Starting X position: " + this.player.getLocation().getX());
        System.out.println("Starting Y position: " + this.player.getLocation().getY());
        Rectangle characterBox = new Rectangle((int) this.player.getLocation().getX() - 10, (int) this.player.getLocation().getY(), this.idle.getWidth((ImageObserver) null), this.idle.getHeight((ImageObserver) null));

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
            this.player.getLocation().setY((double) (this.idle.getHeight((ImageObserver) null) + 480));
        }

        // Initialize the health timer
        healthTimer = new Timer(COOLDOWN_DURATION, null);
        healthTimer.setRepeats(false); // Set to false to execute only once

        // ActionListener for health timer
        healthTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Decrement player's health
                player.playerHealth(-HEALTH_DECREMENT);
                System.out.println("Player health: " + player.playerHealth());
                // Set cooldown and stop the timer
                isCooldown = true;
                healthTimer.stop();
                // Start the cooldown timer
                new Timer(COOLDOWN_DURATION, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Reset the cooldown flag
                        isCooldown = false;
                        // Restart the health timer if the player is still in contact with the plant
                        if (characterBox.intersects(plantBox)) {
                            healthTimer.start();
                        }
                    }
                }).start();
                // Repaint the frame to update the health display
                mFrame.repaint();
            }
        });
    }

    private void loadRunFrames(String prefix) {
        this.runFrames = new Image[4];

        for (int i = 0; i < 4; ++i) {
            this.runFrames[i] = this.loadImage("resources/images/" + prefix + i + ".png");
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
        this.drawText(50.0, 100.0, "Welcome to our game!", 50);
        this.drawText(50.0, 300.0, "Press 'D' to move right and 'A' to move left.", 20);
        this.drawText(50.0, 330.0, "Hold 'Q' to attack with your sword.", 20);
        this.drawText(50.0, 360.0, "Press 'Space' to jump!", 20);
        this.drawText(50.0, 400.0, "Grab key to unlock door to proceed to next level!", 20);
        this.drawText(530.0, 670.0, "Press 'E' on door to enter!", 20);
    }

    public void paintComponent() {
        this.drawImage(this.bg, 0.0, 0.0);
        this.drawImage(this.door, lvlManager.DEMO.getDoorLocation().getX(), lvlManager.DEMO.getDoorLocation().getY(), 105.0, 115.0);

        //this.drawImage(this.dummy, 500.0, 483.0, 100.0, 100.0);
        int keyHitboxWidth = 50;
        int keyHitboxHeight = 50;
        int dHitboxW = 100;
        int dHitboxH = 60;
        Rectangle characterBox = new Rectangle((int) this.player.getLocation().getX() - 10, (int) this.player.getLocation().getY(), this.idle.getWidth((ImageObserver) null), this.idle.getHeight((ImageObserver) null));
        Rectangle keyBox = new Rectangle((int) lvlManager.DEMO.getKeyLocation().getX(), (int) lvlManager.DEMO.getKeyLocation().getY(), keyHitboxWidth, keyHitboxHeight);
        Rectangle dummyBox = new Rectangle(500, 483, dHitboxH, dHitboxW);
        Rectangle plantBox = new Rectangle((int) lvlManager.DEMO.getPlantLoc().getX(), (int) lvlManager.DEMO.getPlantLoc().getY(), keyHitboxWidth, keyHitboxHeight);
        if (characterBox.intersects(keyBox)) {
            this.player.setKeyObtained(true);
        }

        // Define a boolean flag to indicate if the plant monster is present
        boolean isPlantMonsterPresent = true;

        // Inside the plant attack section
        if (characterBox.intersects(plantBox)) {
            // Start the health timer if it's not already running and if not on cooldown
            if (!healthTimer.isRunning() && !isCooldown) {
                healthTimer.start();
            }
            // Draw the plantAttack image
            this.drawImage(this.plantAttack, lvlManager.DEMO.getPlantLoc().getX(), lvlManager.DEMO.getPlantLoc().getY(), 80.0, 80.0);

            // Set other necessary variables
            isAttacking = true;
        } else {
            // Stop the health timer if it's running
            if (healthTimer.isRunning()) {
                healthTimer.stop();
            }
            // Draw the plantMonster image
            if (isPlantMonsterPresent) {
                this.drawImage(this.idleMonster, lvlManager.DEMO.getPlantLoc().getX(), lvlManager.DEMO.getPlantLoc().getY(), 80.0, 80.0);
            } else {
                // Draw some default image or handle the absence of plantMonster
                // For example, draw a placeholder image or leave it blank
                this.drawImage(this.plantMonsterDef, lvlManager.DEMO.getPlantLoc().getX(), lvlManager.DEMO.getPlantLoc().getY(), 40.0, 55.0);
            }
            isAttacking = false;
        }

        if (this.player.hasObtainedKey()) {
            this.keyImage = null;
            this.drawText(700.0, 50.0, "Key: ",20);
            this.drawImage(this.gifImage2, 550, -10.0, 100.0, 100.0);
        }

        Rectangle doorBox = new Rectangle((int)lvlManager.DEMO.getDoorLocation().getX(), (int)lvlManager.DEMO.getDoorLocation().getY(), 100, 100);
        if (characterBox.intersects(doorBox) && this.player.hasObtainedKey()) {
            this.player.setTouchingDoor(true);
        }

        this.drawText(700.0, 50.0, "Key: ",20);
        this.welcome();
        int rectangleHeight = 50;
        int windowHeight = this.height();
        int yRectangle = windowHeight - rectangleHeight;
        //this.drawSolidRectangle(0.0, (double)yRectangle, 1500.0, (double)rectangleHeight);
        Rectangle characterAttackBox;

        int blockSize = WIDTH / lvlManager.DEMO.getSize();
        for (Block b : lvlManager.DEMO.getBlocks()) {
            if (b instanceof BlockGround) {
                String imagePath = b.getString();
                Image blockImage = loadImage(imagePath);

                this.drawImage(blockImage, (int) b.getLocation().getX(), (int) b.getLocation().getY(), blockSize, blockSize);
            }
        }
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
            this.drawImage(this.gifImage, lvlManager.DEMO.getKeyLocation().getX(), lvlManager.DEMO.getKeyLocation().getY(), 80.0, 70.0);
        }

        if (this.LEVEL == 1) {
            this.changeBackgroundColor(Color.BLACK);
            this.clearBackground(mWidth, mHeight);
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

            if (this.player.hasObtainedKey()) {
                this.keyImage = null;
            }

            if (this.keyImage != null) {
                this.drawImage(this.gifImage, 1100.0, 450.0, 50.0, 50.0);
            }
        }

    }

    private Image flipImageHorizontal(Image img) {
        BufferedImage bImg = (BufferedImage)img;
        AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
        tx.translate(-bImg.getWidth(null), 0.0);
        AffineTransformOp op = new AffineTransformOp(tx, 1);
        return op.filter(bImg, (BufferedImage)null);
    }
}

