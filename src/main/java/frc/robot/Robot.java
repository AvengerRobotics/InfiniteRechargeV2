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
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX; //The VictorSPX motor controllers
// import com.revrobotics.ColorSensorV3;
// import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot; //The class that a user program is based on -- not much other info is given
import edu.wpi.first.wpilibj.Timer; //timer for auton
import edu.wpi.first.wpilibj.drive.DifferentialDrive; //used for driving differential drive/skid-steer drive platforms
import frc.robot.auto.AdvancedAuto1;
import frc.robot.auto.SimpleAuto;
import edu.wpi.first.wpilibj.Compressor; //compressor
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

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
  private UsbCamera usbCamera; // Creates the object for the camera

  //private ColorCode colorCode;// class for color sensor code


  private String autoChoice = "Advanced1";
  private SimpleAuto simpleAuto;
  private AdvancedAuto1 advancedAuto1;
  private Timer timer; // creates timer

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
    //colorCode = new ColorCode(new WPI_VictorSPX(5), buttonPanel, new ColorSensorV3(I2C.Port.kOnboard));//Assigns the WoF motor to mprt 5 and the I2C port on the roborio to the color sensor 
    intakeSolenoid = new DoubleSolenoid(0, 1);// creates the cylinder for the intake as an object
    compressor = new Compressor();// creates the compressor as an object
    

    usbCamera = CameraServer.getInstance().startAutomaticCapture("camera_serve_0", 0); // adds a source to the cameraserver from the camera on port 1
    usbCamera.setResolution(320, 240);

    timer = new Timer(); // timer method for autonomous
    simpleAuto = new SimpleAuto(timer, driveTrain);


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
  if (controller.getRB())
    driveTrain.tankDrive((controller.getLJoystickY()*.8),(controller.getRJoystickY()*.8)); // When RB is held, drivetrain runs at 80% of max power
  else
    driveTrain.tankDrive((controller.getLJoystickY()),(controller.getRJoystickY())); // Drivetrain normally runs at 100% power

    
    intakeButton(); //method for intake and conveyor controls
    intakePneumatics(); //method for pneumatics to bring intake up and down
    controlPanelMotor(); //method for the  manual controls of the Wheel of Fortune
    winchMotorControls(); //method for winch motor controls
    //colorCode.teleOpRun(); // runs class for color sensor
  }

  @Override
  public void autonomousPeriodic() {
    switch (autoChoice) { // iterates through a set of choices and runs the one that matches the variable
    case "SimpleAuto":
      simpleAuto.run(); // runs simple auto code
      break; // breaks out of the switch statement
    case "Advanced1":
      advancedAuto1.run();
      break;
    case "Advanced2":
      // run advanced auto 2 here
      break;
    }
  }
  
  // private void AdvancedAuto() {
  //   if (timer.get() < 1.35){
  //     driveTrain.tankDrive(0.78, 0.78);
  //   } else if (timer.get() > 1.35 && timer.get() < 3){
  //     driveTrain.tankDrive(0, 0);
  //     conveyorMotor.set(1);
  //   } else if (timer.get() > 3 && timer.get() < 3.5){
  //     driveTrain.tankDrive(-0.8, -0.8);
  //     conveyorMotor.set(0);
  //   }else if (timer.get() > 3.4 && timer.get() < 3.9){
  //     driveTrain.tankDrive(0, -0.8);
  //     conveyorMotor.set(0);
  //   }else if (timer.get() > 3.9 && timer.get() < 5.3){
  //       driveTrain.tankDrive(-0.8, -0.8);
  //       conveyorMotor.set(0);
  //   }else if (timer.get() > 5.3 && timer.get() < 7){
  //     driveTrain.tankDrive(-0.8, 0);
  //     conveyorMotor.set(0);
  //   }else if (timer.get() >= 7 && timer.get() < 15){
  //     driveTrain.tankDrive(0.6, 0.6);
  //     conveyorMotor.set(0);
  //   }else if (timer.get() >= 15){
  //     driveTrain.tankDrive(0, 0);
  //     conveyorMotor.set(0);
  //   }
  // }

  
  private void intakeButton() {
    //if the intake button is pressed on the control panel, the intake will run
    if (buttonPanel.getIntake()) {
      intakeMotor.set(-1);
    } //if the conveyor button is pressed on the button panel, the conveyor motor will run
    else if (buttonPanel.getConveyor()) {
      conveyorMotor.set(-0.5);
    } //if the ReverseIntake button is pressed on the button panel, the intake motor will run backwards
    else if (buttonPanel.getReverseIntake()) {
      intakeMotor.set(1);
    } //if the ReverseConveyor button is pressed on the button panel, the conveyor motor will run backwards
    else if (buttonPanel.getReverseConveyor()) {
      conveyorMotor.set(0.5);
    }  //if no button is pressed on the button panel, nothing will run
    else {
      conveyorMotor.set(0);
      intakeMotor.set(0);
    }
  }

  private void intakePneumatics() {
    //if the PneumaticsDown button is pressed on the control panel, the intake will drop down
    if (buttonPanel.getPneumaticsDown()) {
      intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
    }//if the PneumaticsUp button is pressed on the control panel, the intake will be brought up
    else if (buttonPanel.getPneumaticsUp() ) {
      intakeSolenoid.set(DoubleSolenoid.Value.kForward);
    } //If neither is pressed, no airflow will happen
    else {
      intakeSolenoid.set(DoubleSolenoid.Value.kOff);
    }
  }

  private void controlPanelMotor() {
    //if the ControlPanel button is pressed on the button panel, the WoF motor will run
    if (buttonPanel.getControlPanel()) {
      controlPanelMotor.set(1);
    }//if the ControlPanel button is not pressed, the motor will not run
    else {
      controlPanelMotor.set(0);
    }
  }

  private void winchMotorControls() {
    //if the LiftUp button is pressed on the button panel, the motors will run to bring the lift up
    if (buttonPanel.getUp()) {
      winchMotors.set(1);
    } //if the LiftDown button is pressed on the button panel, the motors will run to bring the lift down
    else if (buttonPanel.getDown()) {
      winchMotors.set(-1);
    } //if neither button is pressed, the motors will not run
    else {
      winchMotors.set(0);
    }
  }
}