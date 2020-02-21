
package frc.robot;
 
import edu.wpi.first.wpilibj.Joystick;
 
public class ControlPad{
    Joystick station;
 
    public ControlPad(Joystick gamepad){
        station = gamepad;
    }
 
    public boolean getUp(){
        return station.getRawButton(3);
    }
 
//    public boolean getDown(){
//        return station.getRawButton(1);
//    }
 
    public boolean getDown(){
        return station.getRawButton(2);
    }
 
    // public boolean getLeft(){
    //     return station.getRawButton(10);
    // }
 
    public boolean getConveyor(){
        return station.getRawButton(6);
    }
 
    public boolean getIntake(){
        return station.getRawButton(5);
    }
 
    public boolean getReverseConveyor(){
        return station.getRawButton(8);
    }
   
    public boolean getReverseIntake(){
        return station.getRawButton(7);
    }
 
    public boolean getControlPanel(){
        return station.getRawButton(9);
    }
 
     public boolean getSpeed(){
         return station.getRawButton(10);
     }
 
    public boolean getPneumaticsUp(){
        return station.getRawButton(11);
    }
 
    public boolean getPneumaticsDown(){
        return station.getRawButton(12);
    }

    public boolean getRed(){
        return station.getRawAxis(1) > 0;
    }

    public boolean getGreen(){
        return station.getRawAxis(1) < 0;
    }

    public boolean getYellow(){
        return station.getRawAxis(0) < 0;
    }

    public boolean getBlue(){
        return station.getRawAxis(0) > 0;
    }
}