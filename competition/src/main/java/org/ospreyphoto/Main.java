package org.ospreyphoto;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {


    public static void main(String... args) {
        System.out.println("Running main method");


        var s = ConfigProvider.getConfig().getConfigValue("quarkus.log.file.path");
        
        System.out.println(s);

        // Thread t1 = new Thread(new Runnable() {
        //     @Override
        //     public void run() {
        //         UIMain.uimain();
        //     }

        // });
        // t1.start();

        Quarkus.run(args);
    }

    
}