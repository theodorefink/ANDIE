package cosc202.andie;

import java.awt.*;
import java.util.ResourceBundle;

import javax.swing.*;

/**
 * <p>
 * UI display element for {@link EditableImage}s.
 * </p>
 * 
 * <p>
 * This class extends {@link JPanel} to allow for rendering of an image, as well
 * as zooming in and out.
 * 
 * Some code taken from:
 * https://stackoverflow.com/questions/3680221/how-can-i-get-screen-resolution-in-java
 * </p>
 * 
 * <p>
 * <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/">CC BY-NC-SA
 * 4.0</a>
 * </p>
 * 
 * @author Steven Mills
 * @version 1.0
 */
public class ImagePanel extends JPanel {

    /**
     *  Language Bundle for Multilingual Support
     */  
    ResourceBundle bundle = ResourceBundle.getBundle("LanguageBundle");

    /**
     * The image to display in the ImagePanel.
     */
    private EditableImage image;

    /**
     * <p>
     * The zoom-level of the current view.
     * A scale of 1.0 represents actual size; 0.5 is zoomed out to half size; 1.5 is
     * zoomed in to one-and-a-half size; and so forth.
     * </p>
     * 
     * <p>
     * Note that the scale is internally represented as a multiplier, but externally
     * as a percentage.
     * </p>
     */
    private double scale;

    /**
     * <p>
     * The mouse listener used on the image panel.
     * </p>
     * 
     * Allows for selecting areas, cropping and drawing.
     */
    public MyMouseListener mouseListener;

    /**
     * <p>
     * Create a new ImagePanel.
     * </p>
     * 
     * <p>
     * Newly created ImagePanels have a default zoom level of 100%
     * </p>
     */
    public ImagePanel() {
        image = new EditableImage();
        scale = 1.0;
        mouseListener = new MyMouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    /**
     * <p>
     * Get the currently displayed image
     * </p>
     *
     * @return the image currently displayed.
     */
    public EditableImage getImage() {
        try {
            return image;
        } catch (Exception e) {
            
            Object[] options = {bundle.getString("okOption"), bundle.getString("cancelOption")};
            JOptionPane.showOptionDialog(null, bundle.getString("errorRetrievingImage"), bundle.getString("error"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, null);
        }
        return null;
    }

    /**
     * <p>
     * Set the currently displayed image
     * </p>
     *
     * @param newImage The image to replace the current image.
     */
    public void setImage(EditableImage newImage) {
        image = newImage;
    }

    /**
     * <p>
     * Get the current zoom level as a percentage.
     * </p>
     * 
     * <p>
     * The percentage zoom is used for the external interface, where 100% is the
     * original size, 50% is half-size, etc.
     * </p>
     * 
     * @return The current zoom level as a percentage.
     */
    public double getZoom() {
        return 100 * scale;
    }

    /**
     * <p>
     * Set the current zoom level as a percentage.
     * </p>
     * 
     * <p>
     * The percentage zoom is used for the external interface, where 100% is the
     * original size, 50% is half-size, etc.
     * The zoom level is restricted to the range [50, 200].
     * </p>
     * 
     * @param zoomPercent The new zoom level as a percentage.
     */
    public void setZoom(double zoomPercent) {
        if (zoomPercent < 50) {
            zoomPercent = 50;
        }
        if (zoomPercent > 200) {
            zoomPercent = 200;
        }
        scale = zoomPercent / 100;
    }

    /**
     * <p>
     * Gets the preferred size of this component for UI layout.
     * </p>
     * 
     * <p>
     * The preferred size is the size of the image (scaled by zoom level), or a
     * default size if no image is present.
     * </p>
     * 
     * @return The preferred size of this component.
     */
    @Override
    public Dimension getPreferredSize() {

        if (image.hasImage()) {
            return new Dimension((int) Math.round(image.getCurrentImage().getWidth() * scale),
                    (int) Math.round(image.getCurrentImage().getHeight() * scale));
        } else {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            // Get the screen size
            Dimension screenSize = toolkit.getScreenSize();

            // Get the insets of the screen, things like the taskbar
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

            // Get the actual screen size by subtracting the space taken by insets
            int screenWidth = (screenSize.width - insets.left - insets.right);
            // with a bit of extra space because of windows taskbar
            int screenHeight = (screenSize.height - insets.top - insets.bottom) - 60;

            return new Dimension(screenWidth, screenHeight);
        }
    }

    /**
     * <p>
     * (Re)draw the component in the GUI.
     * </p>
     * 
     * @param g The Graphics component to draw the image on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image.hasImage()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.scale(scale, scale);
            
            g2.drawImage(image.getCurrentImage(), null, 0, 0);
            
            // Call drawSelectedRegion() to draw the selected region 
            mouseListener.drawSelectedRegion(g2);
        g2.dispose();
        }  
    }
}
