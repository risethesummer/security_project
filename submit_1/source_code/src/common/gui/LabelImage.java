package common.gui;

import javax.swing.*;

/**
 * clientSide.GUI.utilities
 * Created by NhatLinh - 19127652
 * Date 1/13/2022 - 3:42 PM
 * Description: A label with image icon
 */
public class LabelImage extends JLabel {

    /**
     * Create a label with displayed text and icon
     * @param imgPath the path of the image
     * @param text the text displayed on the label
     */
    public LabelImage(String imgPath, String text)
    {
        super(text);
        int height = this.getPreferredSize().height;
        setIcon(new ImageIcon(ImageRepository.getInstance().getImage(imgPath, height)));
    }

    /**
     * Create a label with displayed icon
     * @param imgPath the path of the image
     * @param size the size of the image
     */
    public LabelImage(String imgPath, int size)
    {
        super();
        setIcon(new ImageIcon(ImageRepository.getInstance().getImage(imgPath, size)));
    }
}
