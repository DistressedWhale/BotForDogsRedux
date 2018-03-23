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

class ImageSelection implements Transferable {
    private Image image;

    public ImageSelection(Image image) {
        this.image = image;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return image;
    }
}