/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot; //The robot

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick; //The controller
import edu.wpi.first.wpilibj.SpeedControllerGroup; //Groups two speed controllers
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX; //The VictorSPX motor controllers
import edu.wpi.first.wpilibj.TimedRobot; //The class that a user program is based on -- not much other info is given
import edu.wpi.first.wpilibj.Timer; //timer for auton
import edu.wpi.first.wpilibj.drive.DifferentialDrive; //used for driving differential drive/skid-steer drive platforms
import frc.robot.auto.SimpleAuto;
import edu.wpi.first.wpilibj.Compressor; //compressor


public class Robot extends TimedRobot { 
  private Gamepad controller; //Creates the object for the contoller
  private ControlPad buttonPanel;
  private SpeedControllerGroup leftMotors; //Creates the object for the left motors
  private SpeedControllerGroup rightMotors; //Creates the object for the right motors
  private SpeedControllerGroup winchMotors;
  private WPI_VictorSPX intakeMotor;
  private WPI_VictorSPX conveyorMotor;
  private DifferentialDrive driveTrain; //Creates the object for the drivetrain
  private DoubleSolenoid intakeSolenoid;
  private Compressor compressor;// class to control the solenoid
  private WPI_VictorSPX controlPanelMotor;//creates the wof Motor as an object

  private String autoChoice = "SimpleAuto";
  private SimpleAuto simpleAuto;
  private Timer timer; //creates timer

  @Override
  public void robotInit() {
    controller = new Gamepad(new Joystick(0)); // Creates the controller on USB 0
    buttonPanel = new ControlPad(new Joystick(1)); // Creates the button panel on USB 1

    /*
    Assigns the motor controllers to speed controller groups
    Argument(value in parenthises) is the CAN bus address
    */
    leftMotors = new SpeedControllerGroup(new WPI_VictorSPX(1), new WPI_VictorSPX(4)); // assigns the left motors on CAN 1 and CAN 4
    rightMotors = new SpeedControllerGroup(new WPI_VictorSPX(2), new WPI_VictorSPX(3)); // assigns the right motors on CAN 2 and CAN 3
    driveTrain = new DifferentialDrive(leftMotors, rightMotors); // makes the drivetrain a differential drive made of the left and right motors
    winchMotors = new SpeedControllerGroup(new WPI_VictorSPX(8), new WPI_VictorSPX(9));
    intakeMotor = new  WPI_VictorSPX(7);
    conveyorMotor = new  WPI_VictorSPX(6);
    controlPanelMotor = new WPI_VictorSPX(5);
    compressor = new Compressor();//creates the compressor as an object

    intakeSolenoid = new DoubleSolenoid(0, 1);//creates the cylinder for the intake as an object
    compressor.start(); //starts compressor in initialization

    timer = new Timer(); //timer method for autonomous
    simpleAuto = new SimpleAuto(timer, driveTrain);
  }

  public void teleopInit() {
    compressor.start();//turns on compressor when the robot is initialized in teleop
  }

  @Override
  public void autonomousInit() {
    compressor.start();//turns on compressor when the robot is initialized in autonomous
    timer.reset();
    timer.start();
  }

  @Override
  public void disabledInit() {
    compressor.stop(); //stops compressor when robot is disabled
  }

  @Override
  public void teleopPeriodic() {
    //Sets tankDrive to the inverse of the values from the joysticks, leftStick value is 1 and rightStick value is 5
    driveTrain.tankDrive((controller.getLJoystickY()), (controller.getRJoystickY()));
    intakeButton();
    intakePneumatics();
    controlPanelMotor();
    winchMotorControls();
  }

  @Override
  public void autonomousPeriodic() {
    switch (autoChoice) { //iterates through a set of choices and runs the one that matches the variable
      case "SimpleAuto":
        simpleAuto.run(); //runs simple auto code
        break; //breaks out of the switch statement
      case "Advanced1":
        // run advanced auto 1 here
        break;
      case "Advanced2":
        // run advanced auto 2 here
        break;
    }
  }

  private void intakeButton() {
    if (buttonPanel.getIntake()) {
      intakeMotor.set(-1);
    } else if (buttonPanel.getDump()) {
      conveyorMotor.set(-1);
    } else if (buttonPanel.getReverse()) {
      conveyorMotor.set(1);
      intakeMotor.set(1);
    } else {
      conveyorMotor.set(0);
      intakeMotor.set(0);
    }
  }

  private void intakePneumatics() {
    if (buttonPanel.getPneumaticsDown()) {
      intakeSolenoid.set(DoubleSolenoid.Value.kForward);
    } else if (buttonPanel.getPneumaticsUp()) {
      intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
    } else {
      intakeSolenoid.set(DoubleSolenoid.Value.kOff);
    }
  }

  private void controlPanelMotor() {
    if (buttonPanel.getControlPanel()) {
      controlPanelMotor.set(1);
    } else {
      controlPanelMotor.set(0);
    }
  }

  private void winchMotorControls() {
    if (buttonPanel.getUp()) {
      winchMotors.set(1);
    } else if (buttonPanel.getDown()) {
      winchMotors.set(-1);
    } else {
      winchMotors.set(0);
    }
  }
}