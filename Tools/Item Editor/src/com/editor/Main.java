package com.editor;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class Main {

    public static Image iconImage;

    public static void main(String[] args) {
        URL url = ClassLoader.getSystemResource("com/editor/resources/ico32.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        iconImage = kit.createImage(url);
        Console.redirectSystemStreams();
        new Console().setVisible(true);
        new ToolSelection().setVisible(true);
    }

    public static void log(String className, String message) {
        System.out.println("[" + className + "]: " + message);
        printDebug(className, message);
    }

    private static void printDebug(String className, String message) {
        File f = new File("logs.txt");

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            String strFilePath = f.getAbsolutePath();

            try (FileOutputStream var9 = new FileOutputStream(strFilePath, true)) {
                String strContent = new Date() + ": [" + className + "]: " + message;
                String lineSep = System.getProperty("line.separator");
                var9.write(strContent.getBytes());
                var9.write(lineSep.getBytes());
            }
        } catch (IOException var8) {
            log("Main", "IOException: " + var8.getMessage());
        }
    }
}
