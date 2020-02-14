
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
 
    public boolean getDown(){
        return station.getRawButton(1);
    }
 
    public boolean getRight(){
        return station.getRawButton(4);
    }
 
    public boolean getLeft(){
        return station.getRawButton(2);
    }
 
    public boolean getPneumaticsUp(){
        return station.getRawButton(6);
    }
 
    public boolean getPneumaticsDown(){
        return station.getRawButton(5);
    }
 
    public boolean getIntake(){
        return station.getRawButton(8);
    }
   
    public boolean getDump(){
        return station.getRawButton(7);
    }
 
    public boolean getControlPanel(){
        return station.getRawButton(10);
    }
 
    public boolean getReverse(){
        return station.getRawButton(9);
    }
 
    public boolean getAutoClimb(){
        return station.getRawButton(12);
    }
 
    public boolean getClimbStop(){
        return station.getRawButton(11);
    }
}