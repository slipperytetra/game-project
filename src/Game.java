import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;
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
    private int characterX;
    private int characterY;
    private boolean isMoving;
    private boolean isFlipped;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean keyObtained;
    private boolean doorTouched;
    private boolean attackRegistered = false;

    private Random mRandom;
    private final double GRAVITY = 0.98;
    private final double JUMP_VELOCITY = -15;
    private double velY;
    private Timer animationTimer;
    private Timer startAnimationTimer;

    public static void main(String args[]) {
        createGame(new Game());
    }

    public void update(double dt) {
        if (isJumping) {
            // Apply gravity when jumping
            velY += GRAVITY;
            characterY += velY;

            // Check if character hits the ground
            if (characterY >= yRectangle - idle.getHeight(null)) {
                characterY = yRectangle - idle.getHeight(null); // Snap character to ground
                isJumping = false; // End the jump
                velY = 0; // Reset vertical velocity
            }
        } else {
            // If not jumping, update character position normally
            characterY += velY;

            // Check collision with the ground
            if (characterY > yRectangle - idle.getHeight(null)) {
                characterY = yRectangle - idle.getHeight(null); // Snap character to ground
                velY = 0; // Reset vertical velocity
            }
        }


    }






    public void init() {
        setWindowSize(1500, 600);
        bg = loadImage("resources/background.jpg");
        mRandom = new Random();
        loadRunFrames("run");
        idle = loadImage("resources/idle.png");
        key = loadImage("resources/key.png");
        keyImage = loadImage("resources/keyy.gif");
        floor = loadImage("resources/floor.png");
        door = loadImage("resources/door.png");
        dummy = loadImage("resources/dummy.png");
        int rectangleHeight = -50;
        int windowHeight = height();
        yRectangle = windowHeight - rectangleHeight;
        characterX = 0;
        characterY = idle.getHeight(null) + 480; // Adjusted starting Y position
        System.out.println("Starting X position: " + characterX); // Log the starting X position
        System.out.println("Starting Y position: " + characterY); // Log the starting Y position
        animationTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentFrameIndex = (currentFrameIndex + 1) % runFrames.length;
                mFrame.repaint();
            }
        });
        startAnimationTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                animationTimer.start();
            }
        });

        // If LEVEL is 1, reset character's position to (0, idle.getHeight(null) + 480)
        if (LEVEL == 1) {
            characterX = 0;
            characterY = idle.getHeight(null) + 480;

        }
    }




    private void loadRunFrames(String prefix) {
        runFrames = new Image[4];
        for (int i = 0; i < 4; i++) {
            runFrames[i] = loadImage("resources/" + prefix + i + ".png");
        }
    }

    private void jumpAnimation() {
        loadRunFrames("jump");
        animationTimer.start();
        animationTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFrameIndex == runFrames.length - 1) {
                    animationTimer.stop();
                }
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent event) {
        super.keyPressed(event);
        if (event.getKeyCode() == KeyEvent.VK_D) {
            isMoving = true;
            isAttacking = false;
            characterX += 10;
            if (isJumping) {
                characterX += 5;
            }
            mFrame.repaint();
            isFlipped = false;
            loadRunFrames("run");
            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        } else if (event.getKeyCode() == KeyEvent.VK_A) {
            isMoving = true;
            isAttacking = false;
            characterX -= 10;
            mFrame.repaint();
            isFlipped = true;
            loadRunFrames("run");
            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        } else if (event.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isJumping) {
                isJumping = true;
                jumpAnimation();
                velY = JUMP_VELOCITY;
            }
        } else if (event.getKeyCode() == KeyEvent.VK_Q && !attackRegistered) {
            isAttacking = true;
            animationTimer.stop();
            currentFrameIndex = 0;
            loadRunFrames("attack");
            animationTimer.start();
            attackRegistered = true;
        }
        if (event.getKeyCode() == KeyEvent.VK_E) {
            if (doorTouched) {
                System.out.println("Entering door...");
                LEVEL = 1;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        super.keyReleased(event);
        if (event.getKeyCode() == KeyEvent.VK_D || event.getKeyCode() == KeyEvent.VK_A) {
            isMoving = false;
            animationTimer.stop();
            currentFrameIndex = 0;
            startAnimationTimer.stop();
            mFrame.repaint();
        } else if (event.getKeyCode() == KeyEvent.VK_Q) {
            System.out.println("Q key released");
            isAttacking = false;
            animationTimer.stop();
            currentFrameIndex = 0;
            mFrame.repaint();
            attackRegistered = false;
        }
    }

    private void welcome() {
        drawText(100, 100, "Welcome to our game!");
        drawText(100, 300, "Press 'D' to move right and 'A' to move left.");
        drawText(100, 330, "Hold 'Q' to attack with your sword.");
        drawText(100, 360, "Press 'Space' to jump!");
        drawText(700, 400, "Grab key to unlock door to proceed to next level!");
        drawText(1250, 450, "Press 'E' on door to enter!");
    }
    Image gifImage = Toolkit.getDefaultToolkit().createImage("resources/keyy.gif");
    Image gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/keyy.gif");
    Image level1 = Toolkit.getDefaultToolkit().createImage("resources/level1.gif");



    @Override
    public void paintComponent() {
        drawImage(bg, 0, 0);
        drawImage(door, 1400, 483, 100, 100);
        drawImage(dummy, 500, 483, 100, 100);
        int keyHitboxWidth = 50;
        int keyHitboxHeight = 50;
        int dHitboxW = 100;
        int dHitboxH = 60;
        Rectangle characterBox = new Rectangle(characterX - 40, characterY, idle.getWidth(null), idle.getHeight(null));
        Rectangle keyBox = new Rectangle(900, 475, keyHitboxWidth, keyHitboxHeight);
        Rectangle dummyBox = new Rectangle(500, 483, dHitboxH, dHitboxW);
        if (characterBox.intersects(keyBox)) {
            keyObtained = true;
        }
        if (keyObtained) {
            keyImage = null;
            drawText(1400, 50, "Key: ");
            drawImage(gifImage2, 1420, -10, 100, 100);
        }
        Rectangle doorBox = new Rectangle(1400, 483, 100, 100);
        if (characterBox.intersects(doorBox) && keyObtained) {
            doorTouched = true;
        }
        drawText(1400, 50, "Key: ");
        welcome();
        int rectangleHeight = 50;
        int windowHeight = height();
        int yRectangle = windowHeight - rectangleHeight;
        drawSolidRectangle(0, yRectangle, 1500, rectangleHeight);
        if (isAttacking && runFrames != null && runFrames.length > 0) {
            Rectangle characterAttackBox = new Rectangle(characterX, characterY, runFrames[currentFrameIndex].getWidth(null), runFrames[currentFrameIndex].getHeight(null));
            if (characterAttackBox.intersects(dummyBox) && !attackRegistered) {
                System.out.println("Hit");
                attackRegistered = true;
            }
            if (isFlipped) {
                drawImage(flipImageHorizontal(runFrames[currentFrameIndex]), characterX, characterY);
            } else {
                drawImage(runFrames[currentFrameIndex], characterX, characterY);
            }
        } else {
            if (isMoving || isJumping) {
                if (isFlipped) {
                    drawImage(flipImageHorizontal(runFrames[currentFrameIndex]), characterX, characterY);
                } else {
                    drawImage(runFrames[currentFrameIndex], characterX, characterY);
                }
            } else {
                drawImage(idle, characterX, characterY);
            }
        }
        if (keyImage != null) {
            drawImage(gifImage, 900, 475, 100, 100);
        }
        if (LEVEL == 1) {
            changeBackgroundColor(Color.BLACK);
            clearBackground(1500, 800);

            if (characterX == 0 && characterY == idle.getHeight(null) + 480) {
                // Reset character's position only once when entering level 1
                characterX = 0;
                characterY = idle.getHeight(null) + 480;}
            drawImage(bg, 0, 0);
            drawImage(gifImage, 900, 250, 100, 100);


            drawSolidRectangle(0, yRectangle, 1500, rectangleHeight);
            drawSolidRectangle(100, 450, 100, 25);



            if (isAttacking && runFrames != null && runFrames.length > 0) {
                Rectangle characterAttackBox = new Rectangle(characterX, characterY, runFrames[currentFrameIndex].getWidth(null), runFrames[currentFrameIndex].getHeight(null));
                if (characterAttackBox.intersects(dummyBox) && !attackRegistered) {
                    System.out.println("Hit");
                    attackRegistered = true;
                }
                if (isFlipped) {
                    drawImage(flipImageHorizontal(runFrames[currentFrameIndex]), characterX, characterY);
                } else {
                    drawImage(runFrames[currentFrameIndex], characterX, characterY);
                }
            } else {
                if (isMoving || isJumping) {
                    if (isFlipped) {
                        drawImage(flipImageHorizontal(runFrames[currentFrameIndex]), characterX, characterY);
                    } else {
                        drawImage(runFrames[currentFrameIndex], characterX, characterY);
                    }
                } else {
                    drawImage(idle, characterX, characterY);
                }
            }
        }
    }

    private Image flipImageHorizontal(Image image) {
        BufferedImage bufferedImage = (BufferedImage) image;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-bufferedImage.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(bufferedImage, null);
    }
}
