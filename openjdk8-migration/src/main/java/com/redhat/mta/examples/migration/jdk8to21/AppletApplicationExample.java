package com.redhat.mta.examples.migration.jdk8to21;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import javax.swing.JApplet;

/**
 * JDK 8 Applet application using deprecated Applet APIs.
 * Applet API is deprecated for removal in JDK 17+ (Konveyor rule: applet-api-deprecation-00000)
 */
public class AppletApplicationExample extends Applet implements MouseListener {
    
    private String message = "Click me!";
    private Color backgroundColor = Color.WHITE;
    private Font messageFont;
    private AudioClip clickSound;
    private int clickCount = 0;
    
    /**
     * Applet initialization - called when applet is loaded
     */
    @Override
    public void init() {
        // Set applet properties
        setBackground(backgroundColor);
        messageFont = new Font("Arial", Font.BOLD, 16);
        
        // Add mouse listener
        addMouseListener(this);
        
        // Get applet parameters from HTML
        String bgColor = getParameter("bgcolor");
        if (bgColor != null) {
            try {
                backgroundColor = Color.decode(bgColor);
                setBackground(backgroundColor);
            } catch (NumberFormatException e) {
                // Use default color
            }
        }
        
        String initialMessage = getParameter("message");
        if (initialMessage != null) {
            message = initialMessage;
        }
        
        // Load audio clip
        try {
            URL soundURL = getClass().getResource("/click.wav");
            if (soundURL != null) {
                clickSound = getAudioClip(soundURL);
            }
        } catch (Exception e) {
            // Audio not available
        }
        
        // Get applet context for browser interaction
        AppletContext context = getAppletContext();
        if (context != null) {
            context.showStatus("Applet initialized successfully");
        }
    }
    
    /**
     * Called when applet starts or becomes visible
     */
    @Override
    public void start() {
        AppletContext context = getAppletContext();
        if (context != null) {
            context.showStatus("Applet started - ready for interaction");
        }
    }
    
    /**
     * Paint the applet content
     */
    @Override
    public void paint(Graphics g) {
        // Set font and color
        g.setFont(messageFont);
        g.setColor(Color.BLACK);
        
        // Draw the message
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2;
        g.drawString(message, x, y);
        
        // Draw click count
        String countMessage = "Clicks: " + clickCount;
        g.drawString(countMessage, 10, 20);
        
        // Draw a border
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
    
    /**
     * Called when applet stops or becomes hidden
     */
    @Override
    public void stop() {
        AppletContext context = getAppletContext();
        if (context != null) {
            context.showStatus("Applet stopped");
        }
    }
    
    /**
     * Called when applet is being destroyed
     */
    @Override
    public void destroy() {
        removeMouseListener(this);
        if (clickSound != null) {
            clickSound.stop();
        }
    }
    
    // Mouse event handlers
    @Override
    public void mouseClicked(MouseEvent e) {
        clickCount++;
        
        // Change background color on click
        backgroundColor = new Color(
            (int) (Math.random() * 255),
            (int) (Math.random() * 255),
            (int) (Math.random() * 255)
        );
        setBackground(backgroundColor);
        
        // Play sound
        if (clickSound != null) {
            clickSound.play();
        }
        
        // Update message
        message = "Clicked " + clickCount + " times!";
        
        // Repaint the applet
        repaint();
        
        // Update browser status
        AppletContext context = getAppletContext();
        if (context != null) {
            context.showStatus("Click count: " + clickCount);
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // Visual feedback on press
        Graphics g = getGraphics();
        if (g != null) {
            g.setColor(Color.RED);
            g.fillOval(e.getX() - 5, e.getY() - 5, 10, 10);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        repaint();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        AppletContext context = getAppletContext();
        if (context != null) {
            context.showStatus("Mouse entered applet area");
        }
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        AppletContext context = getAppletContext();
        if (context != null) {
            context.showStatus("Mouse left applet area");
        }
    }
    
    /**
     * Get applet information
     */
    @Override
    public String getAppletInfo() {
        return "Interactive Applet Example v1.0 - Demonstrates deprecated Applet API usage";
    }
    
    /**
     * Get parameter information for HTML embedding
     */
    @Override
    public String[][] getParameterInfo() {
        return new String[][] {
            {"bgcolor", "color", "Background color (hex format, e.g., #FF0000)"},
            {"message", "string", "Initial message to display"}
        };
    }
}

/**
 * Swing-based applet example - also deprecated in JDK 17+
 */
class SwingAppletExample extends JApplet {
    
    @Override
    public void init() {
        // Use Swing components in applet
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }
    
    private void createGUI() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new java.awt.FlowLayout());
        
        javax.swing.JLabel label = new javax.swing.JLabel("Swing Applet Example");
        javax.swing.JButton button = new javax.swing.JButton("Click Me");
        javax.swing.JTextField textField = new javax.swing.JTextField("Type here", 20);
        
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String text = textField.getText();
                label.setText("You typed: " + text);
                
                // Show browser status
                AppletContext context = getAppletContext();
                if (context != null) {
                    context.showStatus("Button clicked with text: " + text);
                }
            }
        });
        
        panel.add(label);
        panel.add(textField);
        panel.add(button);
        
        getContentPane().add(panel);
    }
}
