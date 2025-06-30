package org.ospreyphoto.nativeui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.dustinredmond.fxtrayicon.FXTrayIcon;

import jakarta.inject.Singleton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

@Singleton
public class UIMain {

    private Stage stage;
    private DirectoryChooser directoryChooser;

    public String fileDialog() {
        System.out.println("UIMAIN");
        File selectedDirectory = directoryChooser.showDialog(stage);
        System.out.println(selectedDirectory);
        return selectedDirectory.toString();
    }

    public void start() {
   
        stage = new Stage();

        directoryChooser = new DirectoryChooser();
        String startingDir = "";//StateStore.getStateStore().getDataDir();
        if (startingDir == null || startingDir.trim().length() == 0) {
            startingDir = System.getProperty("user.home");
        }
        directoryChooser.setInitialDirectory(new File(startingDir));

        System.out.println(directoryChooser);

        var iconResource = getClass().getResource("ex2.gif");
        FXTrayIcon trayIcon = new FXTrayIcon.Builder(stage, iconResource)
                .toolTip("Osprey Competition")
                .menuItem("About", e -> {
                    File selectedDirectory = directoryChooser.showDialog(stage);
                    System.out.println(selectedDirectory);
                })
                .menuItem("Control...", e -> {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI("http://localhost:9000/#/summary"));
                        } catch (IOException | URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                })
                .menuItem("Display...", e -> {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI("http://localhost:9000/#/display"));
                        } catch (IOException | URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                })
                .separator()
                .menuItem("Exit", e -> {
                    System.exit(0);
                })
                .build();
        trayIcon.show();
        trayIcon.showInfoMessage("Osprey Competition Started");

    }


}
