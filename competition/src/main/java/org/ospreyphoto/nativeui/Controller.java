package org.ospreyphoto.nativeui;

import java.io.File;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Singleton;

@Singleton
public class Controller {
    public String loadFromDir() {
        System.out.println("Startng file dialog");
        

        // jnafilechooser.api.JnaFileChooser fc = new jnafilechooser.api.JnaFileChooser();
        // fc.addFilter("All Files", "*");
        // fc.addFilter("Pictures", "jpg", "jpeg", "png", "gif", "bmp");
        // if (fc.showDialog(0)) {
        //     File f = fc.getSelectedFile();
        //     // do something with f
        // }

        // fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Set to allow directory selection only
        // System.out.println("a");
        // int result = fileChooser.showOpenDialog(null); // Use showOpenDialog or showSaveDialog as appropriate

        // System.out.println("v");

        // if (result == JFileChooser.APPROVE_OPTION) {
        //     File selectedDirectory = fileChooser.getSelectedFile();
        //     // Use the selected directory:
        //     System.out.println("Selected Directory: " + selectedDirectory.getAbsolutePath());
        //     return selectedDirectory.getAbsolutePath();
        // }

        return null;
        // var nfd = new NfdFileDialog();
        // FileDialogResult<File> r = nfd.pickDirectory(System.getProperty("user.home"));
        // var s = r.toString();
        // if (s.startsWith("Success")) {
        //     var name = s.substring(s.indexOf('=') + 1, s.length() - 1);
        //     return name;
        // } else {
        //     throw new RuntimeException(s);
        // }

    }

    @ConsumeEvent("nativeui")
    @Blocking
    public String consume(String cmd) {

        switch (cmd) {
            case "selectdir":
                var dir = loadFromDir();
                return dir;

        }
        return "Error";
    }
}
