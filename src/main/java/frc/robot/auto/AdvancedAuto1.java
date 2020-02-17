package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class AdvancedAuto1 {
  private DifferentialDrive driveTrain;
  private Timer timer;
  private WPI_VictorSPX conveyorMotor;

  public AdvancedAuto1(DifferentialDrive driveTrain, Timer timer, WPI_VictorSPX conveyorMotor) {
    this.driveTrain = driveTrain;
    this.timer = timer;
    this.conveyorMotor = conveyorMotor;
  }

  public void run() {
    if (timer.get() < 1.35) {
      driveTrain.tankDrive(0.78, 0.78);
    } else if (timer.get() > 1.35 && timer.get() < 3) {
      driveTrain.tankDrive(0, 0);
      conveyorMotor.set(1);
    } else if (timer.get() > 3 && timer.get() < 3.5) {
      driveTrain.tankDrive(-0.8, -0.8);
      conveyorMotor.set(0);
    } else if (timer.get() > 3.4 && timer.get() < 3.9) {
      driveTrain.tankDrive(0, -0.8);
      conveyorMotor.set(0);
    } else if (timer.get() > 3.9 && timer.get() < 5.3) {
      driveTrain.tankDrive(-0.8, -0.8);
      conveyorMotor.set(0);
    } else if (timer.get() > 5.3 && timer.get() < 7) {
      driveTrain.tankDrive(-0.8, 0);
      conveyorMotor.set(0);
    } else if (timer.get() >= 7 && timer.get() < 15) {
      driveTrain.tankDrive(0.6, 0.6);
      conveyorMotor.set(0);
    } else if (timer.get() >= 15) {
      driveTrain.tankDrive(0, 0);
      conveyorMotor.set(0);
    }
  }
}
