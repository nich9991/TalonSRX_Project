/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import java.sql.Driver;
import java.sql.DriverAction;
import javax.annotation.meta.When;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.InvertType;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController; 
import edu.wpi.first.wpilibj.Joystick.ButtonType;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;


import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
//ColorTarget is all red,blue,yellow,green
private static final int ColorTarget =7;
private int redcount = 0;
private int bluecount = 0;
private int greencount = 0;
private int yellowcount = 0;
//Variable to store last color used in control panel stage 2 NDL
private String LastColorString;

 private double forward = 0.0;
 private double turn = 0.0;
 private double backward = 0.0;

 public CameraServer server;
 public VideoSource cam0;

 private final I2C.Port ColorPort = I2C.Port.kOnboard;
 private final ColorSensorV3 m_colorSensor = new ColorSensorV3(ColorPort);
 private final ColorMatch m_colorMatcher = new ColorMatch();

 private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
 private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
 private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
 private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

  WPI_TalonSRX leftController = new WPI_TalonSRX(11);
  WPI_TalonSRX rightController = new WPI_TalonSRX(12);
  WPI_TalonSRX controlPannelMotor = new WPI_TalonSRX(13);

  Joystick joy_silv = new Joystick(0);
 // Joystick joy_blac = new Joystick(1);

  XboxController Xbox = new XboxController(1);

  DigitalInput magnet = new DigitalInput(0);
  DigitalInput limit = new DigitalInput(1); 


  @Override
  public void robotInit(){
  /*
  This function is run when the robot is first started up and should be used
  for any initialization code.
  */
  CameraServer.getInstance().startAutomaticCapture();
 
  m_colorMatcher.addColorMatch(kBlueTarget);
  m_colorMatcher.addColorMatch(kGreenTarget);
  m_colorMatcher.addColorMatch(kRedTarget);
  m_colorMatcher.addColorMatch(kYellowTarget);    
}
DifferentialDrive drive = new DifferentialDrive(leftController, rightController);
  
  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
    
  }

  @Override
  public void teleopInit() {


    leftController.configFactoryDefault();
    rightController.configFactoryDefault();  

    leftController.setInverted(false);
    rightController.setInverted(true);
    
    drive.setRightSideInverted(false);
  
  }
  
@Override
  public void teleopPeriodic() {
  
    Color detectedColor = m_colorSensor.getColor();
    String colorString;
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
    if (match.color == kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }
               
    checkcolor(colorString); //---> diff from colorStrin at bottom
    System.out.println(redcount);
   if (redcount >= ColorTarget || bluecount >= ColorTarget 
            || yellowcount >= ColorTarget || greencount >= ColorTarget){
             // controlPannelMotor.set(0);
              System.out.println("done count");

   }
    magnet.get();
    limit.get();
    //double forward = +.8 * joy_blac.getY();
    //double turn = +.8 * joy_blac.getZ();
    //double backward = .8* joy_blac.getX();
     forward = +.8* Xbox.getY();  
     turn = +.8 * Xbox.getX();
     backward = +.8* Xbox.getY();

    if (Math.abs(forward) < 0.4) {
			forward = 0;
    }
    
    if (Math.abs(turn) < 0.4) {
			turn = 0;
    }

   if (joy_silv.getRawButton(1)==true) {
    controlPannelMotor.set(.30);
   }
   else if (joy_silv.getRawButton(1)==false){
   controlPannelMotor.set(0);
   }

  
   



    System.out.println("JoyY:" + forward + "  turn:" + turn + " joyX " + backward);
    System.out.println("magnet:" + !magnet.get());  
    System.out.println("limit:" + limit.get());  
    System.out.println("joystick_1:" + joy_silv.getTrigger());

    drive.arcadeDrive(forward, turn);
  
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);  

  }
  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

public void checkcolor (String funcolor){
 boolean read = false;
 // Added in Check to LastColorString and set Last color String when it does not match so that it will only count when color changes NDL
  if (read == false){
  if (funcolor.equals("Red") && !funcolor.equals(LastColorString)){  
   redcount++;
   read = true;
   LastColorString = "RED";
  }
  else if (funcolor.equals("Blue")&& !funcolor.equals(LastColorString)){
   bluecount++;
   read = true;
   LastColorString = "Blue";
  }
  else if (funcolor.equals("Yellow")&& !funcolor.equals(LastColorString)){
   yellowcount++;
   read = true;
   LastColorString = "Yellow";
  }
  else if (funcolor.equals("Green")&& !funcolor.equals(LastColorString)){
   greencount++;
   read = true;
   LastColorString = "Green";
  }
 } 
}


}
