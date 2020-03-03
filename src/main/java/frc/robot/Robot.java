/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot; //The robot

//DO NOT TOUCH THIS SECTION
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick; //The controller
import edu.wpi.first.wpilibj.SpeedControllerGroup; //Groups two speed controllers

import java.util.logging.Logger;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX; //The VictorSPX motor controllers
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot; //The class that a user program is based on -- not much other info is given
import edu.wpi.first.wpilibj.Timer; //timer for auton
import edu.wpi.first.wpilibj.drive.DifferentialDrive; //used for driving differential drive/skid-steer drive platforms
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer; //import to create the dial on the drivers station to select auton
import edu.wpi.first.wpilibj.Compressor; //compressor
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput; //Sensors
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.SimpleAuto;

public class Robot extends TimedRobot {
  // DO NOT TOUCH THIS SECTION
  private Gamepad controller; // Creates the object for the contoller
  private ControlPad buttonPanel; // Creates the object for the button panel
  private SpeedControllerGroup leftMotors; // Creates the object for the left motors
  private SpeedControllerGroup rightMotors; // Creates the object for the right motors
  private SpeedControllerGroup winchMotors; // Creates the object for the winch motors
  private WPI_VictorSPX intakeMotor; // Creates the object for the intake motor
  private WPI_VictorSPX conveyorMotor; // Creates the object for the conveyor motor
  private WPI_VictorSPX controlPanelMotor;// creates the wof Motor as an object
  private DifferentialDrive driveTrain; // Creates the object for the drivetrain
  private DoubleSolenoid intakeSolenoid; // Creates the object for the solenoid
  private Compressor compressor;// class to control the solenoid
  private UsbCamera usbCamera; // Creates the object for the camera facing the front
  private UsbCamera usbCamera2; // back Camera 

  private ColorCode colorCode;
  private I2C.Port i2cPort = I2C.Port.kOnboard; //port for 
  private ColorSensorV3 colorSensor;
  private AnalogPotentiometer pot;
  private Timer timer; // creates timer
  private Timer teleopTimer;
  private Boolean timerIsOn = false;
  private Logger logger;
  private SimpleAuto simpleAuto;

  private DigitalInput topLimit, bottomLimit;
  private DigitalInput prox1, prox2; //creates the proximity switches for conveyer and intake
  private boolean isIntakeDown = false;

  @Override
  public void robotInit() {
    // DO NOT TOUCH THIS SECTION
    controller = new Gamepad(new Joystick(0)); // Creates the controller on USB 0
    buttonPanel = new ControlPad(new Joystick(1)); // Creates the button panel on USB 1
    // DO NOT TOUCH THIS SECTION
    /*
     * Assigns the motor controllers to speed controller groups Argument(value in
     * parenthises) is the CAN bus address
     */
    leftMotors = new SpeedControllerGroup(new WPI_VictorSPX(1), new WPI_VictorSPX(4)); // assigns the left motors on CAN 1 and CAN 4
    rightMotors = new SpeedControllerGroup(new WPI_VictorSPX(2), new WPI_VictorSPX(3)); // assigns the right motors on CAN 2 and CAN 3
    driveTrain = new DifferentialDrive(leftMotors, rightMotors); // makes the drivetrain a differential drive made of the left and right motors
    winchMotors = new SpeedControllerGroup(new WPI_VictorSPX(8), new WPI_VictorSPX(9));
    intakeMotor = new WPI_VictorSPX(7);
    conveyorMotor = new WPI_VictorSPX(6);
    controlPanelMotor = new WPI_VictorSPX(5);
    colorSensor = new ColorSensorV3(i2cPort);
    colorCode = new ColorCode(controlPanelMotor, buttonPanel, colorSensor);//Assigns the WoF motor to mprt 5 and the I2C port on the roborio to the color sensor 
    intakeSolenoid = new DoubleSolenoid(0, 1);// creates the cylinder for the intake as an object
    compressor = new Compressor();// creates the compressor as an object
    
    usbCamera = CameraServer.getInstance().startAutomaticCapture("camera_serve_0", 0); // adds a source to the cameraserver from the camera on port 1
    usbCamera2 = CameraServer.getInstance().startAutomaticCapture("camera_serve_1", 1);
    usbCamera.setResolution(256, 192);
    usbCamera2.setResolution(256, 192);
    pot = new AnalogPotentiometer(new AnalogInput(0), 180, 0);
    timer = new Timer(); // timer method for autonomous
    teleopTimer = new Timer();
    simpleAuto = new SimpleAuto(timer, driveTrain);
    logger = Logger.getLogger(this.getClass().getName());

    prox1 = new DigitalInput(2); //bottom sensor
    prox2 = new DigitalInput(0); //top sensor
  
    topLimit = new DigitalInput(4);
    bottomLimit = new DigitalInput(5);

    compressor.start(); // starts compressor in initialization
  }

  public void teleopInit() {
    compressor.start();// turns on compressor when the robot is initialized in teleop
  }

  @Override
  public void autonomousInit() {
    compressor.start();// turns on compressor when the robot is initialized in autonomous
    timer.reset();
    timer.start();
  }
  @Override
  public void disabledInit() {
    compressor.stop(); // stops compressor when robot is disabled
  }

  @Override
  public void teleopPeriodic() {
    if (controller.getRB()) {
      driveTrain.tankDrive((controller.getLJoystickY()),(controller.getRJoystickY())); // Drivetrain normally runs at 100% power
    } else {
      driveTrain.tankDrive((controller.getLJoystickY()*.8),(controller.getRJoystickY()*.8)); 
    }
    teleopTimer.reset();


    intakeButton(); //method for intake and conveyor controls
    intakePneumatics(); //method for pneumatics to bring intake up and down
    controlPanelMotor(); //method for the  manual controls of the Wheel of Fortune
    winchMotorControls(); //method for winch motor controls
    //colorCode.teleOpRun(); // runs class for color sensor
  }

  @Override
  public void autonomousPeriodic() {
    /**
     * pot can range from 0 to the value of POT_FULL_RANGE. this value is set above to match the potentiometer's full range.
     * when divided by 1/3 of the potentiometer's full range, it returns a number between 0 and 3.
     * math.floor rounds down and subtracting by 1 makes sure it never returns 3
     */
    // try {
      switch ((int) Math.floor((pot.get() - 1) / (180 / 3))){
        case 0:
          advancedAuto(); // runs advanced auto code
          break; // breaks out of the switch statement
        case 1:
          simpleAuto.run();
          break;
        case 2:
          // run advanced auto 2 here
          break;
      }
    /*} catch (Exception e) {
      /**
       * if we are not using a potentiometer, it will automatically run this
       * because it will catch the exception in the above statement and run this instead
       */
      /*advancedAuto();
    }*/
  }

    private void advancedAuto() {
    if (timer.get() < 1.53) { //reverse into the goal
      driveTrain.tankDrive(-0.78, -0.78); 
    } else if (timer.get() > 1.53 && timer.get() < 3.4) { //dumps the cells into the lower goal
      driveTrain.tankDrive(0, 0);
      conveyorMotor.set(-1);
    } else if (timer.get() > 3.4 && timer.get() < 3.5) { //drives fowards away from the goal
      driveTrain.tankDrive(0.8, -0.8); 
    } else if (timer.get() >= 3.5 && timer.get() < 4.5) { //drives fowards away from the goal
      driveTrain.tankDrive(0.8, 0.8); 
      conveyorMotor.set(0);
    } else if (timer.get() >= 4.4 && timer.get() < 4.5) { //drives fowards away from the goal
      driveTrain.tankDrive(-0.8, 0.8);
    } else if (timer.get() >= 4.5 && timer.get() < 4.7) { //drives fowards away from the goal
      driveTrain.tankDrive(0.8, 0.8); 
    } else if (timer.get() >= 4.7) { //drives fowards away from the goal
      driveTrain.tankDrive(0, 0);  
    }
  }

  //   if (timer.get() < 1.53) { //reverse into the goal
  //     driveTrain.tankDrive(-0.78, -0.79); 
  //   } else if (timer.get() > 1.53 && timer.get() < 3.3) { //dumps the cells into the lower goal
  //     driveTrain.tankDrive(0, 0);
  //     conveyorMotor.set(-1);
  //   } else if (timer.get() > 3.3 && timer.get() < 3.8) { //drives fowards away from the goal
  //     driveTrain.tankDrive(0.8, 0.8); 
  //     conveyorMotor.set(0);
  //   } else if (timer.get() > 3.8 && timer.get() < 4.35) { //turns left
  //     driveTrain.tankDrive(0.8, -0.8); 
  //     conveyorMotor.set(0);
  //   } else if (timer.get() > 4.35 && timer.get() < 5) {
  //       driveTrain.tankDrive(-0.8, -0.8); //reverses to the center of the field
  //       conveyorMotor.set(0);
  //   } else if (timer.get() > 5 && timer.get() < 5.7) {
  //     driveTrain.tankDrive(-0.8, 0.8); //turns left to have the back of the robot facing the generator
  //     conveyorMotor.set(0);
  //   } else if (timer.get() >= 5.7 && timer.get() < 7) { //reverse to the opposite side of the field
  //     driveTrain.tankDrive(0.8, 0.8);
  //     conveyorMotor.set(0);
  //   } else if (timer.get() >= 7){ //stops the robot at 11 seconds
  //     driveTrain.tankDrive(0, 0);
  //     conveyorMotor.set(0);
  //   }
  // }
  
  private void intakeButton() {

    if (buttonPanel.getIntake() && isIntakeDown) { //will run the next if statements if the intake is down and the intake button is pressed
      if(prox1.get() && prox2.get()) { //If there is not a ball at either of the sensors, the intake motor will run, but the conveyor wont
        intakeMotor.set(-1);
        conveyorMotor.set(0);
      } else if(!prox1.get() && prox2.get()) { //If there is a ball at 1 but not at 2, the conveyor will run at 20% speed and the intake will run
        intakeMotor.set(-1);
        conveyorMotor.set(-0.3);
      } else if(!prox2.get()) { //If there is a ball at 2, neither the conveyor or intake motor will run
        intakeMotor.set(0);
        conveyorMotor.set(0);
      } 
    } else if (buttonPanel.getReverseIntake()) { // if the ReverseIntake button is pressed on the button panel, the intake motor will run backwards
         intakeMotor.set(1);
    } else if (buttonPanel.getConveyor()) { //the conveyor motor will run at full speed of the conveyor Button is pressed
      conveyorMotor.set(-1);
    } else if (buttonPanel.getReverseConveyor()) { // if the ReverseConveyor button is pressed on the button panel, the conveyor motor will run backwards
      conveyorMotor.set(0.5);
    } else { // if no button is pressed on the button panel, nothing will run
      conveyorMotor.set(0);
      intakeMotor.set(0);
    } 
    SmartDashboard.putBoolean("Proximity Switch 1", prox1.get());
    SmartDashboard.putBoolean("Proximity Switch 2", prox2.get()); 
  }
 
  private void intakePneumatics() {
    if (buttonPanel.getPneumaticsDown()) { // if the PneumaticsDown button is pressed on the control panel, the intake will drop down
      intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
      isIntakeDown = true;
    } else if (buttonPanel.getPneumaticsUp()) { // if the PneumaticsUp button is pressed on the control panel, the intake will be brought up
      intakeSolenoid.set(DoubleSolenoid.Value.kForward);
      isIntakeDown = false;
    } else { // If neither is pressed, no airflow will happen
      intakeSolenoid.set(DoubleSolenoid.Value.kOff);
    }
  }

  private void controlPanelMotor() {
  /*  
  if (buttonPanel.getControlPanel() && timerIsOn == false) {
      timerIsOn = true;

      if (timerIsOn = true){
        teleopTimer.reset();
        teleopTimer.start();
        if (teleopTimer.get() < 3) { 
          controlPanelMotor.set(0.75);
        } if (teleopTimer.get() >= 3) { 
          controlPanelMotor.set(0); 
        }
      }
    }
*/
    if (buttonPanel.getControlPanel()) { // if the ControlPanel button is pressed on the button panel, the WoF motor will run
      controlPanelMotor.set(0.3);
     } else if (buttonPanel.getControlPanelAuto()) { // if the LiftDown button is pressed on the button panel, the motors will run to bring the lift down
         controlPanelMotor.set(0.7);
      } 
     else {
       controlPanelMotor.set(0);// if the ControlPanel button is not pressed, the motor will not run
     }
  }

  private void winchMotorControls() {
    if (buttonPanel.getUp() && topLimit.get()) { // if the LiftUp button is pressed on the button panel, the motors will run to bring the lift up
      winchMotors.set(0.4);
    } else if (buttonPanel.getDown() && bottomLimit.get()) { // if the LiftDown button is pressed on the button panel, the motors will run to bring the lift down
      winchMotors.set(-0.5);
    } else { // if neither button is pressed, the motors will not run
      winchMotors.set(0);
    }
  }
}