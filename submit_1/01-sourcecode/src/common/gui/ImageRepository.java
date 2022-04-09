package common.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.Hashtable;

/**
 * utilities
 * Created by NhatLinh - 19127652
 * Date 1/13/2022 - 2:30 PM
 * Description: The pool of images in the program (store images in buffer in order to quickly load it instead of loading from disk everytime)
 */
public class ImageRepository {

    private ImageRepository() {}

    /**
     * Instance object
     */
    private static ImageRepository instance = null;

    /**
     * The small size of an image in the program
     */
    public static final int SMALL = 16;
    /**
     * The medium size of an image in the program
     */
    public static final int MEDIUM = 25;
    /**
     * The big size of an image in the program
     */
    public static final int BIG = 40;

    /**
     * Get the instance of this singleton class
     * @return the singleton instance
     */
    public static ImageRepository getInstance()
    {
        if (instance == null)
            instance = new ImageRepository();
        return instance;
    }

    /**
     * Store images in buffer (accessed by its name)
     */
    private final Hashtable<String, Image> images = new Hashtable<>();

    /**
     * Get an image by its name
     * @param name the name of the image
     * @return the instance of the image
     */
    public Image getImage(String name)
    {
        //Load the image if it was not loaded before
        if (!images.contains(name))
            images.put(name, load(name));
        return images.get(name);
    }

    /**
     * Get an image scaled by the given width and height by its name
     * @param name the name of the image
     * @param width the width of the image
     * @param height the height of the image
     * @return the instance of the image
     */
    public Image getImage(String name, int width, int height)
    {
        return getImage(name).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     * Get an image scaled by the given size (width = height) by its name
     * @param name the name of the image
     * @param size the size for both width and height
     * @return the instance of the image
     */
    public Image getImage(String name, int size)
    {
        return getImage(name, size, size);
    }

    private Image load(String path)
    {
        try
        {
            URL imgURl = this.getClass().getClassLoader().getResource(path);
            return ImageIO.read(imgURl);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
