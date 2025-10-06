package com.redhat.mta.examples.migration.jdk8to21;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

/**
 * Interactive web component using Java Applet API.
 * Applet API deprecated in JDK 17+
 */
public class DeprecatedAppletAPIExample extends Applet implements MouseListener {
    
    private String message = "Click to interact";
    private Color backgroundColor = Color.LIGHT_GRAY;
    private AudioClip soundClip;
    
    @Override
    public void init() {
        setBackground(backgroundColor);
        addMouseListener(this);
        
        // Load audio resource
        try {
            URL soundUrl = getClass().getResource("/sounds/click.wav");
            if (soundUrl != null) {
                soundClip = getAudioClip(soundUrl);
            }
        } catch (Exception e) {
            // Audio loading failed
        }
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        
        g.drawString(message, x, y);
        
        // Draw interactive elements
        g.setColor(Color.BLUE);
        g.fillRect(10, 10, 50, 30);
        g.setColor(Color.WHITE);
        g.drawString("Button", 15, 30);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        message = "Clicked at: " + e.getX() + ", " + e.getY();
        backgroundColor = new Color((int)(Math.random() * 255), 
                                  (int)(Math.random() * 255), 
                                  (int)(Math.random() * 255));
        setBackground(backgroundColor);
        
        if (soundClip != null) {
            soundClip.play();
        }
        
        repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {
        message = "Mouse entered applet";
        repaint();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        message = "Mouse left applet";
        repaint();
    }
    
    public void showDocument(String url) {
        try {
            AppletContext context = getAppletContext();
            if (context != null) {
                context.showDocument(new URL(url));
            }
        } catch (Exception e) {
            // URL navigation failed
        }
    }
    
    public String getParameter(String name, String defaultValue) {
        String value = getParameter(name);
        return value != null ? value : defaultValue;
    }
}
