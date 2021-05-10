/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remotepi;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

/**
 *
 * @author Jack
 */
public class ServoControl {
    
    private static final int PIN_NUMBER = 1;
    
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        System.out.println("Started\n");
        
        Gpio.wiringPiSetup();
        
        int scs = SoftPwm.softPwmCreate(PIN_NUMBER, 0, 100);
        
        if (scs == 0) {
            System.out.println("PWM created successfully\n" + "Using PIN: " + PIN_NUMBER);
            //FUTABA SERVO
            setSpeed(15);
            setSpeed(7);
            setSpeed(15);
            setSpeed(22);
            setSpeed(15);
                    
            
//            setSpeed(1);
//            setSpeed(30);
//            setSpeed(1);
//            setSpeed(30);
//            setSpeed(13);
        } else {
            System.out.println("Failed to create PWM pin");
        }
        
        System.out.println("\nFinished");
    }
    
    private static void setSpeed(int speed) throws InterruptedException {
        System.out.println("Speed is set to: " + speed + "%");
        
        SoftPwm.softPwmWrite(PIN_NUMBER, speed);
        
        if (speed == 0) {
            Thread.sleep(1000);
        } else {
            Thread.sleep(8000);
        }
    }
    
}
