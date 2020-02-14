package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class AdvancedAuto1{
  private DifferentialDrive driveTrain;
  private Timer timer;
  private WPI_VictorSPX conveyerMotor;
  
  public AdvancedAuto1(DifferentialDrive driveTrain, Timer timer, WPI_VictorSPX conveyerMotor){
    this.driveTrain = driveTrain;
    this.timer = timer;
    this.conveyerMotor = conveyerMotor;
  }

  public void run(){
    if (timer.get() < 2){
      driveTrain.tankDrive(-0.5, -0.5);
    } else if (timer.get() > 2 && timer.get() < 4){
      driveTrain.tankDrive(0, 0);
      conveyerMotor.set(1);
    } else if (timer.get() >= 6){
      driveTrain.tankDrive(0, 0);
      conveyerMotor.set(0);
    }
  }
}
