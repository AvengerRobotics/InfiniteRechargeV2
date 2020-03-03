/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Add your docs here.
 */
public class ColorCode {
  private WPI_VictorSPX controlPanelMotor; // Creates the object for the WoF motors
  private ControlPad buttonPanel; // creates the button panel
  private ColorSensorV3 colorSensor; // creates the sensor
  private final ColorMatch colorMatcher = new ColorMatch();
  // the numbers used to match color sensor values to colors
  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
  private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
  private String previousColor;
  private int colorChanges;
  private String currentColor;
  private boolean isSpinActive;
  private boolean isColorActive;
  private ColorMatchResult match;
  private Color detectedColor;
  private String colorDetecting;
  private Map<String, String> colorMap;


  public ColorCode(WPI_VictorSPX motor, ControlPad buttonPanel, ColorSensorV3 sensor) {
    controlPanelMotor = motor;
    this.buttonPanel = buttonPanel; // the buttonpanel outside this is = the buttonpanel inside this
    colorSensor = sensor;
    // adds colors to the colormatcher
    colorMatcher.addColorMatch(kBlueTarget);
    colorMatcher.addColorMatch(kGreenTarget);
    colorMatcher.addColorMatch(kRedTarget);
    colorMatcher.addColorMatch(kYellowTarget);
    initColorMap();
  }

  public void teleOpRun() {
    detectedColor = colorSensor.getColor();
    match = colorMatcher.matchClosestColor(detectedColor);

    /**
     * Run the color match algorithm on our detected color
     */
    match = colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      currentColor = "Blue";
    } else if (match.color == kRedTarget) {
      currentColor = "Red";
    } else if (match.color == kGreenTarget) {
      currentColor = "Green";
    } else if (match.color == kYellowTarget) {
      currentColor = "Yellow";
    } else {
      currentColor = "Unknown";
    }

    /**
     * Open Smart Dashboard or Shuffleboard to see the color detected by the sensor.
     */
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", currentColor);
    // color code - current color is stored in currentColor
    if (buttonPanel.getControlPanelAuto()) { // when the X button is clicked, it turns on the WoFMotor, then resets color changes and previous color
      controlPanelMotor.set(1);
      colorChanges = 0;
      previousColor = "Unknown";
      isSpinActive = true;
    }
    if (isSpinActive) { // it checks if the WoFMotor is still spinning due to the X button
      if (!currentColor.equals(previousColor)) { // if the color changes, it increases the number of color changes by one
        colorChanges++; // increases color changes by 1
        previousColor = currentColor; // updates previous color to be current color
      }
      if (colorChanges >= 32) { // if the color has changed more than 32 times, it will stop the motor
        controlPanelMotor.set(0);
        isSpinActive = false;
      }
    }
    if (buttonPanel.getRed()) { // stops over blue
      controlPanelMotor.set(1);
      isColorActive = true;
      colorDetecting = "Red";
    }
    if (buttonPanel.getBlue()) { // stops over red
      controlPanelMotor.set(1);
      isColorActive = true;
      colorDetecting = "Blue";
    }
    if (buttonPanel.getGreen()) { // stops over yellow
      controlPanelMotor.set(1);
      isColorActive = true;
      colorDetecting = "Green";

    }
    if (buttonPanel.getYellow()) { // stops over green
      controlPanelMotor.set(1);
      isColorActive = true;
      colorDetecting = "Yellow";
    }
    if (isColorActive && currentColor.equals(colorMap.get(colorDetecting))) {
      controlPanelMotor.set(0);
      isColorActive = false;
    }

    if (buttonPanel.getControlPanelAuto() && controlPanelMotor.get() != 0){
      controlPanelMotor.set(0);
      isSpinActive = false;
      isColorActive = false;
    }
  }

  private void initColorMap() {
    colorMap = new HashMap<String, String>();
    colorMap.put("Blue", "Red");
    colorMap.put("Yellow", "Green");
    colorMap.put("Red", "Blue");
    colorMap.put("Green", "Yellow");
  }
}