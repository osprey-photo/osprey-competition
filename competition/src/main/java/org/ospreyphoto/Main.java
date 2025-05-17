package org.ospreyphoto;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {


    public static void main(String... args) {
        System.out.println("Running main method");

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