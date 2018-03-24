import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.ImageIcon;

public class ImageToClipboard {
    public static void setImageToClipboard(String fileName) throws Exception {

        ImageSelection imgSel = new ImageSelection(new ImageIcon(fileName).getImage());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);

        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image img = (Image) t.getTransferData(DataFlavor.imageFlavor);
        }

    }
}

