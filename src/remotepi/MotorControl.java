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

/**
 *
 * @author Jack
 */
public class MotorControl {
    final static GpioController gpio = GpioFactory.getInstance();
        
    final static GpioPinDigitalOutput pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "7 PIN DOWN ON THE LEFT", PinState.LOW);
    final static GpioPinDigitalOutput pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "6 PIN DOWN ON THE RIGHT", PinState.LOW);
        
    public static void main(String[] args) throws InterruptedException {
        Clockwise();
        
        Thread.sleep(5000);
        
        CounterClockwise();
        
        Thread.sleep(5000);
        
        STOP();
        
        Thread.sleep(1000);
        
        gpio.shutdown();
    }
    
    static void Clockwise() {
        pin2.high();
        pin1.low();
        
        System.out.println("The motor is spinning clockwise");
    }
    
    static void CounterClockwise() {
        pin2.low();
        pin1.high();
        
        System.out.println("The motor is spinning counter-clockwise");
    }
    
    static void STOP() {
        pin2.low();
        pin1.low();
        
        System.out.println("The motor is stopped");
    }
}
