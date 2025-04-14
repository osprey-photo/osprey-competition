package org.ospreyphoto.nativeui;

import java.io.File;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Singleton;
import tv.wunderbox.nfd.FileDialogResult;
import tv.wunderbox.nfd.nfd.NfdFileDialog;

@Singleton
public class Controller {
    private String loadFromDir() {

        var nfd = new NfdFileDialog();
        FileDialogResult<File> r = nfd.pickDirectory(System.getProperty("user.home"));
        var s = r.toString();
        if (s.startsWith("Success")) {
            var name = s.substring(s.indexOf('=') + 1, s.length() - 1);
            return name;
        } else {
            throw new RuntimeException(s);
        }

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
