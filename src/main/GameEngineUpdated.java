package main;


import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class GameEngineUpdated {

    protected JFrame mFrame;
    protected GamePanel mPanel;

    protected int wWidth;
    protected int wHeight;

    protected Graphics2D mGraphics;

    private Timer gameTimer;
    private long lastUpdateTime;

    private static final int TARGET_UPS = 60;
    private static final int TIMER_DELAY_MS = 1000 / TARGET_UPS;

    private boolean running = false;

    public GameEngineUpdated() {
        this(500, 500);
    }

    public GameEngineUpdated(int width, int height) {
        wWidth = width;
        wHeight = height;
    }

    public static void createGame(GameEngineUpdated engine) {
        createGame(engine, TARGET_UPS);
    }

    public static void createGame(GameEngineUpdated engine, int updatesPerSecond) {
        if (updatesPerSecond < 1) {
            throw new IllegalArgumentException(
                    "Updates per second must exceed 0."
            );
        }

        Runnable startup = () -> {
            if (engine.running) {
                return;
            }

            engine.setupWindow();
            engine.init();
            engine.startLoop(updatesPerSecond);
        };

        if (SwingUtilities.isEventDispatchThread()) {
            startup.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(startup);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(
                        "Game startup was interrupted.", e
                );
            } catch (InvocationTargetException e) {
                throw new RuntimeException(
                        "Game startup failed.", e.getCause()
                );
            }
        }

    }

    private void setupWindow() {
        mFrame = new JFrame("Untitled Java Game");
        mPanel = new GamePanel();

        mPanel.setPreferredSize(new Dimension(wWidth, wHeight));
        mPanel.setFocusable(true);
        mPanel.setDoubleBuffered(true);

        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setResizable(false);
        mFrame.setContentPane(mPanel);
        mFrame.pack();
        mFrame.setLocationRelativeTo(null);
        mFrame.setVisible(true);

        //Add key listeners
        mPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                GameEngineUpdated.this.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                GameEngineUpdated.this.keyReleased(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                GameEngineUpdated.this.keyTyped(e);
            }
        });

        mPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameEngineUpdated.this.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                GameEngineUpdated.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                GameEngineUpdated.this.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                GameEngineUpdated.this.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                GameEngineUpdated.this.mouseExited(e);
            }
        });

        mPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                GameEngineUpdated.this.mouseMoved(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                GameEngineUpdated.this.mouseDragged(e);
            }
        });

        mPanel.requestFocusInWindow();
    }

    //Start update timer

    private void startLoop(int updatesPerSecond) {
        lastUpdateTime = System.nanoTime();
        running = true;

        int delay = Math.max(1, 1000 / updatesPerSecond);

        gameTimer = new Timer(delay, e -> {
            long now = System.nanoTime();

            double dt = (now - lastUpdateTime) / 1_000_000_000.0;
            lastUpdateTime = now;

            dt = Math.min(dt, 0.25);

            update(dt);
            mPanel.repaint();
        });

        gameTimer.setCoalesce(true);
        gameTimer.start();
    }

    public void stopLoop() {
        Runnable stop = () -> {
            running = false;

            if (gameTimer != null) {
                gameTimer.stop();
                gameTimer = null;
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            stop.run();
        } else {
            SwingUtilities.invokeLater(stop);
        }
    }

    public void setWindowSize(int width, int height) {
        if (mFrame == null) {
            wWidth = width;
            wHeight = height;
            return;
        }

        SwingUtilities.invokeLater(() -> {
            wWidth = width;
            wHeight = height;

            mPanel.setPreferredSize(new Dimension(width, height));

            mFrame.pack();
            mFrame.setLocationRelativeTo(null);
        });
    }

    public int getWidth() {
        return wWidth;
    }

    public int getHeight() {
        return wHeight;
    }

    public Graphics2D getGraphics() {
        return mGraphics;
    }

    //Game lifestyle hooks
    public void init() {};
    public abstract void update(double dt);
    public abstract void paintComponent();

    //Keyboard hooks
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    //Mouse hooks
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    // Changes the background Color to the color c
    public void changeBackgroundColor(Color c) {
        // Set background colour
        mGraphics.setBackground(c);
    }

    // Changes the background Color to the color (red,green,blue)
    public void changeBackgroundColor(int red, int green, int blue) {
        // Clamp values
        if(red < 0)   {red = 0;}
        if(red > 255) {red = 255;}

        if(green < 0)   {green = 0;}
        if(green > 255) {green = 255;}

        if(blue < 0)   {blue = 0;}
        if(blue > 255) {blue = 255;}

        // Set background colour
        mGraphics.setBackground(new Color(red,green,blue));
    }

    // Clears the background, makes the whole window whatever the background color is
    public void clearBackground(int width, int height) {
        // Clear background
        mGraphics.clearRect(0, 0, width, height);
    }

    // Changes the drawing Color to the color c
    public void changeColor(Color c) {
        // Set colour
        mGraphics.setColor(c);
    }

    // Changes the drawing Color to the color (red,green,blue)
    public void changeColor(int red, int green, int blue) {
        // Clamp values
        if(red < 0)   {red = 0;}
        if(red > 255) {red = 255;}

        if(green < 0)   {green = 0;}
        if(green > 255) {green = 255;}

        if(blue < 0)   {blue = 0;}
        if(blue > 255) {blue = 255;}

        // Set colour
        mGraphics.setColor(new Color(red,green,blue));
    }

    // Draws a line from (x1,y2) to (x2,y2)
    public void drawLine(double x1, double y1, double x2, double y2) {
        // Draw a Line
        mGraphics.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    // Draws a line from (x1,y2) to (x2,y2) with width l
    void drawLine(double x1, double y1, double x2, double y2, double l) {
        // Set the stroke
        mGraphics.setStroke(new BasicStroke((float)l));

        // Draw a Line
        mGraphics.draw(new Line2D.Double(x1, y1, x2, y2));

        // Reset the stroke
        mGraphics.setStroke(new BasicStroke(1.0f));
    }

    // This function draws a rectangle at (x,y) with width and height (w,h)
    public void drawRectangle(double x, double y, double w, double h) {
        // Draw a Rectangle
        mGraphics.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    // This function draws a rectangle at (x,y) with width and height (w,h)
    // with a line of width l
    void drawRectangle(double x, double y, double w, double h, double l) {
        // Set the stroke
        mGraphics.setStroke(new BasicStroke((float)l));

        // Draw a Rectangle
        mGraphics.draw(new Rectangle2D.Double(x, y, w, h));

        // Reset the stroke
        mGraphics.setStroke(new BasicStroke(1.0f));
    }

    // This function fills in a rectangle at (x,y) with width and height (w,h)
    public void drawSolidRectangle(double x, double y, double w, double h) {
        // Fill a Rectangle
        mGraphics.fill(new Rectangle2D.Double(x, y, w, h));

    }

    // This function draws a circle at (x,y) with radius
    void drawCircle(double x, double y, double radius) {
        // Draw a Circle
        mGraphics.draw(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));
    }

    // This function draws a circle at (x,y) with radius
    // with a line of width l
    void drawCircle(double x, double y, double radius, double l) {
        // Set the stroke
        mGraphics.setStroke(new BasicStroke((float)l));

        // Draw a Circle
        mGraphics.draw(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));

        // Reset the stroke
        mGraphics.setStroke(new BasicStroke(1.0f));
    }

    // This function draws a circle at (x,y) with radius
    void drawSolidCircle(double x, double y, double radius) {
        // Fill a Circle
        mGraphics.fill(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));
    }

    // This function draws text on the screen at (x,y)
    public void drawText(double x, double y, String s, int size) {
        // Draw text on the screen
        mGraphics.setFont(new Font("Arial", Font.PLAIN, size));
        mGraphics.drawString(s, (int)x, (int)y);
    }




    // This function draws bold text on the screen at (x,y)
    public void drawBoldText(double x, double y, String s) {
        // Draw text on the screen
        mGraphics.setFont(new Font("Arial", Font.BOLD, 40));
        mGraphics.drawString(s, (int)x, (int)y);
    }


    // This function draws bold text on the screen at (x,y)
    public void drawBoldText(double x, double y, String s, int size) {
        // Draw text on the screen
        mGraphics.setFont(new Font("Arial", Font.BOLD, size));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    // This function draws text on the screen at (x,y)
    // with Font (font,size)
    public void drawText(double x, double y, String s, String font, int size) {
        // Draw text on the screen
        mGraphics.setFont(new Font(font, Font.PLAIN, size));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    // This function draws bold text on the screen at (x,y)
    // with Font (font,size)
    public void drawBoldText(double x, double y, String s, String font, int size) {
        // Draw text on the screen
        mGraphics.setFont(new Font(font, Font.BOLD, size));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y) {
        // Check if image is null
        if(image == null) {
            // Print Error message
            System.out.println("Error: cannot draw null image.\n");
            return;
        }

        // Draw image on screen at (x,y)
        mGraphics.drawImage(image, (int)x, (int)y, null);
    }

    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y, double w, double h) {
        // Check if image is null
        if(image == null) {
            // Print Error message
            System.out.println("Error: cannot draw null image.\n");
            return;
        }
        // Draw image on screen at (x,y) with size (w,h)
        mGraphics.drawImage(image, (int) Math.ceil(x), (int)Math.ceil(y), (int)Math.ceil(w), (int)Math.ceil(h), null);
    }

    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y, double w, double h, float opacity) {
        // Check if image is null
        if(image == null) {
            // Print Error message
            System.out.println("Error: cannot draw null image.\n");
            return;
        }

        if (opacity < 0) {
            opacity = 0.0f;
        }
        // Draw image on screen at (x,y) with size (w,h)
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity);
        mGraphics.setComposite(ac);
        mGraphics.drawImage(image, (int)x, (int)y, (int)w, (int)h, null);

        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);
        mGraphics.setComposite(ac);
    }
    // Loads an image from file
    public Image loadImage(String filename) {
        try {
            // Load Image
            Image image = ImageIO.read(new File(filename));

            // Return Image
            return image;
        } catch (IOException e) {
            // Show Error Message
            System.out.println("Error: could not load image " + filename);
            System.exit(1);
        }

        // Return null
        return null;
    }

    //-------------------------------------------------------
    // Sound Functions
    //-------------------------------------------------------

    // Class used to store an audio clip
    public class AudioClip {
        // Format
        AudioFormat mFormat;

        // Audio Data
        byte[] mData;

        // Buffer Length
        long mLength;

        // Loop Clip
        Clip mLoopClip;

        public Clip getLoopClip() {
            // return mLoopClip
            return mLoopClip;
        }

        public void setLoopClip(Clip clip) {
            // Set mLoopClip to clip
            mLoopClip = clip;
        }

        public AudioFormat getAudioFormat() {
            // Return mFormat
            return mFormat;
        }

        public byte[] getData() {
            // Return mData
            return mData;
        }

        public long getBufferSize() {
            // Return mLength
            return mLength;
        }

        public AudioClip(AudioInputStream stream) {
            // Get Format
            mFormat = stream.getFormat();

            // Get length (in Frames)
            mLength = stream.getFrameLength() * mFormat.getFrameSize();

            // Allocate Buffer Data
            mData = new byte[(int)mLength];

            try {
                // Read data
                stream.read(mData);
            } catch(Exception exception) {
                // Print Error
                System.out.println("Error reading Audio File\n");

                // Exit
                System.exit(1);
            }

            // Set LoopClip to null
            mLoopClip = null;
        }
    }

    // Loads the AudioClip stored in the file specified by filename
    public AudioClip loadAudio(String filename) {
        try {
            // Open File
            File file = new File(filename);

            // Open Audio Input Stream
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            // Create Audio Clip
            AudioClip clip = new AudioClip(audio);

            // Fix pauses and mixer issues when clip is first played
            playAudio(clip,-60);

            // Return Audio Clip
            return clip;
        } catch(Exception e) {
            // Catch Exception
            System.out.println("Error: cannot open Audio File " + filename + "\n");
        }

        // Return Null
        return null;
    }

    // Plays an AudioClip
    public void playAudio(AudioClip audioClip) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        try {
            // Create a Clip
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if(event.getType().equals(LineEvent.Type.STOP)) {
                        clip.close();
                    }
                }
            });
            // Load data
            clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

            // Play Clip
            clip.start();
        } catch(Exception exception) {
            // Display Error Message
            System.out.println("Error playing Audio Clip\n");
        }
    }

    // Plays an AudioClip with a volume in decibels
    public void playAudio(AudioClip audioClip, float volume) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        try {
            // Create a Clip
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if(event.getType().equals(LineEvent.Type.STOP)) {
                        clip.close();
                    }
                }
            });
            // Load data
            clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

            // Create Controls
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Set Volume
            control.setValue(volume);

            // Play Clip
            clip.start();
        } catch(Exception exception) {
            // Display Error Message
            System.out.println("Error: could not play Audio Clip\n");
        }
    }


    // Starts playing an AudioClip on loop
    public void startAudioLoop(AudioClip audioClip) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        // Get Loop Clip
        Clip clip = audioClip.getLoopClip();

        // Create Loop Clip if necessary
        if(clip == null) {
            try {
                // Create a Clip
                clip = AudioSystem.getClip();

                // Load data
                clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

                // Set Clip to Loop
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                // Set Loop Clip
                audioClip.setLoopClip(clip);
            } catch(Exception exception) {
                // Display Error Message
                System.out.println("Error: could not play Audio Clip\n");
            }
        }

        // Set Frame Position to 0
        clip.setFramePosition(0);

        // Start Audio Clip playing
        clip.start();
    }

    // Starts playing an AudioClip on loop with a volume in decibels
    public void startAudioLoop(AudioClip audioClip, float volume) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        // Get Loop Clip
        Clip clip = audioClip.getLoopClip();

        // Create Loop Clip if necessary
        if(clip == null) {
            try {
                // Create a Clip
                clip = AudioSystem.getClip();

                // Load data
                clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

                // Create Controls
                FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Set Volume
                control.setValue(volume);

                // Set Clip to Loop
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                // Set Loop Clip
                audioClip.setLoopClip(clip);
            } catch(Exception exception) {
                // Display Error Message
                System.out.println("Error: could not play Audio Clip\n");
            }
        }

        // Set Frame Position to 0
        clip.setFramePosition(0);

        // Start Audio Clip playing
        clip.start();
    }

    // Stops an AudioClip playing
    public void stopAudioLoop(AudioClip audioClip) {
        // Get Loop Clip
        Clip clip = audioClip.getLoopClip();

        // Check clip is not null
        if(clip != null){
            // Stop Clip playing
            clip.stop();
        }
    }


    protected class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (!running) {
                return;
            }

            Graphics2D g = (Graphics2D) graphics.create();

            try {
                mGraphics = g;

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                GameEngineUpdated.this.paintComponent();
            } finally {
                mGraphics = null;
                g.dispose();
            }
        }
    }
}

