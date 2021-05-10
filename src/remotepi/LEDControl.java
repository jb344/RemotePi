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
 * @author JB
 */
public class LEDControl {
    
    final static GpioController gpio = GpioFactory.getInstance();
    final static GpioPinDigitalOutput pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "6 PIN DOWN ON THE RIGHT", PinState.LOW);

    public static void main(String[] args) throws InterruptedException {
        on();
        
        Thread.sleep(10000);
        
        off();
        
        gpio.shutdown();
    }
    
    public static void on() {
        System.out.println("Pin going high");
        pin1.setState(PinState.HIGH);
    }
    
    public static void off() {
        pin1.setState(PinState.LOW);
    }
}
