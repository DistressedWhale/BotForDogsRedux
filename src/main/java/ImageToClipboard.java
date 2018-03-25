import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import javax.swing.ImageIcon;
import java.net.*;
import javax.imageio.*;

public class ImageToClipboard {
    public static void setImageToClipboard(String fileName) throws Exception {

        ImageSelection imgSel = new ImageSelection(new ImageIcon(fileName).getImage());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);

        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image img = (Image) t.getTransferData(DataFlavor.imageFlavor);
        }

    }

    public static void setImageLinkToClipboard(String imageURL) throws Exception {
        Image image;
        URL url = new URL(imageURL);
        image = ImageIO.read(url);

        ImageSelection imgSel = new ImageSelection(new ImageIcon(image).getImage());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);

        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image img = (Image) t.getTransferData(DataFlavor.imageFlavor);
        }
    }
}

