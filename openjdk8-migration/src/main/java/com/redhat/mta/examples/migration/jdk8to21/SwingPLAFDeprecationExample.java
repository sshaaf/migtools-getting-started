package com.redhat.mta.examples.migration.jdk8to21;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.io.File;

/**
 * UI framework using deprecated Swing PLAF internal classes.
 * Basic PLAF internal classes deprecated in JDK 19+
 */
public class SwingPLAFDeprecationExample {
    
    private CustomDirectoryModel directoryModel;
    private CustomToolBarUI toolBarUI;
    
    public void initializeUI() {
        JFileChooser fileChooser = new JFileChooser();
        directoryModel = new CustomDirectoryModel(fileChooser);
        toolBarUI = new CustomToolBarUI();
    }
    
    public JScrollPane createCustomScrollPane(Component view) {
        return new CustomScrollPane(view);
    }
    
    public JToolBar createCustomToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setUI(toolBarUI);
        return toolBar;
    }
    
    /**
     * Custom directory model using deprecated PLAF methods
     */
    private static class CustomDirectoryModel extends BasicDirectoryModel {
        
        public CustomDirectoryModel(JFileChooser fileChooser) {
            super(fileChooser);
        }
        
        public void processIntervalChanges() {
            // These methods are deprecated in JDK 19+:
            // intervalAdded(), intervalRemoved(), lt()
        }
        
        public boolean compareFiles(File file1, File file2) {
            // Custom comparison instead of deprecated lt() method
            return file1.getName().compareTo(file2.getName()) < 0;
        }
    }
    
    /**
     * Custom scroll pane using deprecated PLAF internals
     */
    private static class CustomScrollPane extends JScrollPane {
        
        public CustomScrollPane(Component view) {
            super(view);
            setupScrollBehavior();
        }
        
        private void setupScrollBehavior() {
            // Access deprecated internal listeners:
            // BasicScrollPaneUI.HSBChangeListener
            // BasicScrollPaneUI.VSBChangeListener  
            // BasicScrollPaneUI.ViewportChangeHandler
            // BasicScrollPaneUI.PropertyChangeHandler
            
            getHorizontalScrollBar().addAdjustmentListener(e -> {
                // Custom horizontal scroll handling
            });
            
            getVerticalScrollBar().addAdjustmentListener(e -> {
                // Custom vertical scroll handling
            });
        }
    }
    
    /**
     * Custom toolbar UI using deprecated PLAF methods
     */
    private static class CustomToolBarUI extends BasicToolBarUI {
        
        private JFrame floatingFrame;
        
        public void createCustomFloatingFrame() {
            // BasicToolBarUI.createFloatingFrame() is deprecated in JDK 19+
            floatingFrame = new JFrame("Custom Floating Toolbar");
            floatingFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        
        public void showFloatingFrame() {
            if (floatingFrame != null) {
                floatingFrame.setVisible(true);
            }
        }
    }
}
