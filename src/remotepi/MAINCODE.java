/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotepi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import net.java.games.input.*;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class MAINCODE {

    public static Process capture;
    public static Process mkdir;
    public static Process buildVideo;
    public static Process connect;
    public static String s;
    public static int count = 1;

    final static GpioController gpio = GpioFactory.getInstance();   //Create a controller object based on the current GPIO configuration
    final static GpioPinDigitalOutput pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "7 PIN DOWN ON THE LEFT", PinState.LOW);  //set up a digital pin based on GPIO pin 2
    final static GpioPinDigitalOutput pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "6 PIN DOWN ON THE RIGHT", PinState.LOW);
    private static final int PIN_NUMBER = 24;    //create an integer with value 24 - PWM1
    private static final int PIN_NUMBER_SERVO = 1;

    public static void main(String[] args) throws InterruptedException, IOException {
        File directory = CreateDirectory();
        int connected = ConnectController();

        STOP(); //call the stop function to set the pins initial state to low

        Gpio.wiringPiSetup();   //enable the wiringpi pin numbering system

        int scs = SoftPwm.softPwmCreate(PIN_NUMBER, 0, 100);    //create a soft pulse width modulation pin for the servo
        int scs2 = SoftPwm.softPwmCreate(PIN_NUMBER_SERVO, 0, 100);

        if (scs == 0 && connected == 1 && scs2 == 0) {
            System.out.println("PWM created successfully\n" + "Using PIN: " + PIN_NUMBER);  //if the PWM was created successfully
            System.out.println("Controller successfully paired");
        } else {
            System.exit(0); //if it wasn't, exit the program
        }

        Controller DS4 = null;  //controller object initialised to null
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();   //array of controllers connected to the pi

        if (ca.length == 0) {
            System.out.println("No controllers");   //if the array isn't populated we have no controllers
        } else {
            System.out.println("CONTROLLERS FOUND"); //else we do
        }

        for (Controller ca1 : ca) { //itterative loop to go through all of the controllers
            String name = ca1.getName();
            String type = ca1.getType().toString();

            if (type.equals("Stick")) {
                DS4 = ca1;  //if we find the PS4 controller set up the controller object to inherit its properties
                System.out.println("DUALSHOCK 4 FOUND AND SELECTED");
            }
        }

        while (true) {
            DS4.poll(); //constantly poll the controller for changes
            EventQueue queue = DS4.getEventQueue(); //create a queue of events (buttons pressed etc)
            Event event = new Event();  //event object

            while (queue.getNextEvent(event)) { //if we have a new event
                switch (event.getComponent().getName()) {   //get the name of the component the event is coming from
                    case "B":   //if its the x button on the controller
                        if (event.getValue() == 1.0) {
                            System.err.println("YOU PRESSED X");
                            setSpeedMotor(14);
                        } else {
                            setSpeedMotor(1); //when the button is released stop the motor
                        }
                        break;
                    case "A":   //if its the square button on the controller
                        if (event.getValue() == 1.0) {
                            System.err.println("YOU PRESSED SQUARE");
                            setSpeedMotor(10);
                        } else {
                            setSpeedMotor(1); //when the button is released stop the motor
                        }
                        break;
                    case "X":
                        if (event.getValue() == 1.0) {
                            System.err.println("YOU PRESSED TRIANGLE");
                            setSpeedMotor(20);
                            Thread.sleep(4000);
                            setSpeedMotor(30);
                            Thread.sleep(1000);
                            setSpeedMotor(40);
                            Thread.sleep(1000);
                            setSpeedMotor(30);
                            Thread.sleep(5000);
                        } else {
                            setSpeedMotor(1);
                        }
                        break;
                    case "Z":   //RB - Record 5 seconds of video
                        if (event.getValue() == 1.0) {
                            //run a UNIX process to execute the command to capture video for 5 seconds, set -t 0 for constant recording
                            capture = Runtime.getRuntime().exec("raspivid -o video.h264 -t 5000", null, directory);
                            BufferedReader br = new BufferedReader(new InputStreamReader(capture.getInputStream()));        //get any response messages from the command

                            System.out.println("5 SECONDS OF VIDEO CAPTURED");
                        }
                        break;
                    case "Y":   //LB - Take a picture
                        if (event.getValue() == 1.0) {
                            capture = Runtime.getRuntime().exec("raspistill -o " + count + ".jpg", null, directory);    //run a UNIX process to execute the command to capture a still image
                            BufferedReader br = new BufferedReader(new InputStreamReader(capture.getInputStream()));    //get any response messages from the command

                            System.out.println("STILL PICTURE TAKEN");
                        }
                        break;
                    case "pov": //if its one of the buttons on the directional pad
                        if (event.getValue() == 1.0) {  //value 1.0 corresponds to the left arrow
                            System.err.println("YOU PRESSED LEFT ON THE D-PAD");
                            setSpeedServo(7);    //set the PWM of the servo to 700 microseconds, aka turn left
                        } else if (event.getValue() == 0.5) {   //value 0.5 corresponds to the right arrow
                            System.err.println("YOU PRESSED RIGHT ON THE D-PAD");
                            setSpeedServo(22);   //set the PWM of the servo to 22000 microseconds, aka turn right
                        } else if (event.getValue() == 0) { //if the buttons have been released
                            setSpeedServo(15);   //set the servo back to neutral so the wheels face forward
                        }
                        break;
//                    case "y":
//                        if (event.getValue() > 0.3) {
//                            setSpeedServo(22);
//                        }
//                        break;
//                    case "x":
//                        if (event.getValue() > 0.7) {
//                            setSpeedServo(7);
//                        }
//                        break;
                    case "Right Thumb 2":   //if the start button has been pressed
                        if (event.getValue() == 1.0) {
                            buildVideo = Runtime.getRuntime().exec("sudo MP4Box -fps 30 -add video.h264 video.mp4", null, directory);   //command to decode the video and build it into a .mp4 file
                            BufferedReader br = new BufferedReader(new InputStreamReader(buildVideo.getInputStream()));        //get any response messages from the command

                            while ((s = br.readLine()) != null) {
                                System.out.println(s);  //print out the reponses if there are any
                            }

                            buildVideo.waitFor();  //wait for the command to finish execution
                            int cmdScs = buildVideo.exitValue();   //determine whether or not the command was a success

                            if (cmdScs == 0) {  //value of 0 means it executed successfully
                                System.out.println("VIDEO DECODED");
                                buildVideo.destroy();
                                capture.destroy();
                            }

                            System.err.println("PROGRAM TERMINATED");
                            System.exit(0); //terminate the code
                        }
                        break;
                }
                //System.out.println(event);  //print the event data
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }

    private static void setSpeedServo(int speed) throws InterruptedException {
        System.out.println("Speed is set to: " + speed + "%");

        SoftPwm.softPwmWrite(PIN_NUMBER_SERVO, speed);    //write the pulse width modulation to the pin based on the number entered

        if (speed == 0) {
            Thread.sleep(1000);
        } else {
            Thread.sleep(250);  //sleep for 250 milliseconds
        }
    }
    
    private static void setSpeedMotor(int speed) throws InterruptedException {
        System.out.println("Speed is set to: " + speed + "%");

        SoftPwm.softPwmWrite(PIN_NUMBER, speed);    //write the pulse width modulation to the pin based on the number entered

        if (speed == 0) {
            Thread.sleep(1000);
        } else {
            Thread.sleep(250);  //sleep for 250 milliseconds
        }
    }

    static void Clockwise() {
        pin2.high();    //set one pin to high and one to low allowing the motor to spin
        pin1.low();

        System.out.println("The motor is spinning clockwise");
    }

    static void CounterClockwise() {
        pin2.low();
        pin1.high();

        System.out.println("The motor is spinning counter-clockwise");
    }

    static void STOP() {
        pin2.low();     //set both pins to low to stop the motor rotating
        pin1.low();

        System.out.println("The motor is stopped");
    }

    static File CreateDirectory() throws InterruptedException, IOException {
        Date date = new Date();
        String stringDate = date.toString();
        String stringDateReplaced = stringDate.replaceAll("\\s", "_");
        File directory = new File("/home/pi/Camera/" + stringDateReplaced);

        mkdir = Runtime.getRuntime().exec("mkdir /home/pi/Camera/" + stringDateReplaced);
        mkdir.waitFor();
        mkdir.destroy();

        return directory;
    }

    static int ConnectController() throws InterruptedException, IOException {
        String b;
        int found = 0;

        connect = Runtime.getRuntime().exec("sudo ds4drv");
        BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));

        String controllerConnected = "[info][controller 1] Connected to Bluetooth Controller (30:0E:D5:AB:D8:29)";

        while ((b = br.readLine()) != null) {
            System.out.println(b);  //print out the reponses if there are any
            if (b.equals(controllerConnected)) {
                found = 1;
                break;
            }
        }

        return found;
    }

    static void Trial() throws InterruptedException {
        int speed;

        for (speed = 100; speed <= 200; speed++) {
            SoftPwm.softPwmWrite(PIN_NUMBER, speed);    //write the pulse width modulation to the pin based on the number entered
            System.out.println("Speed is set to: " + speed + "%");
            Thread.sleep(500);
        }
    }
}
