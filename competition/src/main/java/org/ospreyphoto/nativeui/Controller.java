package org.ospreyphoto.nativeui;

import java.io.File;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Singleton;
import tv.wunderbox.nfd.FileDialogResult;
import tv.wunderbox.nfd.nfd.NfdFileDialog;

@Singleton
public class Controller {


    @ConsumeEvent("nativeui")
    @Blocking
    public String consume(String cmd) {

        System.out.println("natuve vui");
        switch (cmd) {
            case "selectdir":
                return "newdir";

        }
        return "Error";
    }
}
