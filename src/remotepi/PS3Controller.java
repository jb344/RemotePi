/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotepi;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class PS3Controller {

    public static void main(String[] args) {
        // TODO code application logic here
        Controller DS4 = null;
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        
        if (ca.length == 0) {
            System.out.println("No controllers");
        } else {
            System.out.println("CONTROLLER FOUND");
        }

        for (Controller ca1 : ca) {
            String name = ca1.getName();
            String type = ca1.getType().toString();

            if (type.equals("Stick")) {
                DS4 = ca1;
            }
        }

        while (true) {
            DS4.poll();
            EventQueue queue = DS4.getEventQueue();
            Event event = new Event();

            while (queue.getNextEvent(event)) {
                switch (event.getComponent().getName()) {
                    case "B":
                        System.err.println("YOU PRESSED X");
                        break;
                    case "A":
                        System.err.println("YOU PRESSED SQUARE");
                        break;
                    case "pov":
                        if (event.getValue() == 1.0) {
                            System.err.println("YOU PRESSED LEFT ON THE D-PAD");
                        } else if (event.getValue() == 0.5) {
                            System.err.println("YOU PRESSED RIGHT ON THE D-PAD");
                        }
                }
                System.out.println(event);
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }
}
