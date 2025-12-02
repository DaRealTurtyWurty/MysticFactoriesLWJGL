package dev.turtywurty.mysticfactories.client.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class ClipboardUtils {
    public static Clipboard getClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public static void copyStringToClipboard(String text) {
        var data = new StringSelection(text);
        getClipboard().setContents(data, null);
    }

    public static String copyStringFromClipboard() throws UnsupportedFlavorException, IOException {
        Transferable transferable = getClipboard().getContents(null);

        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            return (String) transferable.getTransferData(DataFlavor.stringFlavor);

        System.out.println("Couldn't get data from the clipboard");
        return null;
    }

    public static boolean isStringInClipboard() {
        Transferable transferable = getClipboard().getContents(null);
        return transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    public static void copyImageToClipboard(Image image) {
        var data = new ImageSelection(image);
        getClipboard().setContents(data, null);
    }

    public static Image getImageFromClipboard() throws UnsupportedFlavorException, IOException {
        Transferable transferable = getClipboard().getContents(null);

        if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
            return (Image) transferable.getTransferData(DataFlavor.imageFlavor);

        System.out.println("Couldn't get image from the clipboard");
        return null;
    }

    public static boolean isImageInClipboard() {
        Transferable transferable = getClipboard().getContents(null);
        return transferable.isDataFlavorSupported(DataFlavor.imageFlavor);
    }
}
