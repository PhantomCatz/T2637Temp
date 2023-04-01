// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.Mechanisms.CatzArm;
import frc.Mechanisms.CatzElevator;
import frc.Mechanisms.CatzIntake;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot 
{
    private XboxController xboxDrv;
    private XboxController xboxAux;

    private final int XBOX_DRV_PORT = 0;
    private final int XBOX_AUX_PORT = 1;

    public static final int DPAD_UP = 0;
    public static final int DPAD_DN = 180;
    public static final int DPAD_LT = 270;
    public static final int DPAD_RT = 90;


    private CatzElevator elevator;
    private CatzArm arm;
    private CatzIntake intake;

    public double gamePiece = 0.0;
    public boolean stowPos  = false;
    public boolean pickUpPos = false;
    public boolean lowNode = false;
    public boolean midNode = false;
    public boolean highNode = false;
    public final double CONE = 1.0;

    public final boolean GPCUBE = false;
    public final boolean GPCONE = true;
    public boolean selectedGamePiece = GPCUBE;
    


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() 
  {
    xboxDrv = new XboxController(XBOX_DRV_PORT);
    xboxAux = new XboxController(XBOX_AUX_PORT);


    elevator = new CatzElevator();
    arm      = new CatzArm();
    intake    = new CatzIntake();



  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic()
  {
    elevator.shuffleboardElevator();
    elevator.shuffleboardElevator_DEBUG();

    elevator.checkLimitSwitches();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() 
  {
    
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic()
  {
    
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit()
  {
    
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic()
  {
    gamePiece = xboxAux.getLeftTriggerAxis(); //pressed is cone, released is cube
    
    highNode = xboxAux.getYButton();
    midNode = xboxAux.getBButton();
    lowNode = xboxAux.getAButton();
    stowPos = xboxDrv.getRightStickButton();
    pickUpPos = xboxDrv.getLeftStickButton();
 
    elevator.cmdProcElevator(-xboxAux.getRightY(),             // Manual Elevator Power
                              xboxAux.getRightStickButton(),     // Manual Soft Limit Override
                              gamePiece,
                              lowNode,
                              midNode,
                              highNode,
                              stowPos,
                              pickUpPos);  

    arm.cmdProcArm(xboxAux.getPOV() == DPAD_UP,  //Manual Extend Arm
                  xboxAux.getPOV() == DPAD_DN,   //Manual Retract Arm 
                  gamePiece,
                  lowNode,
                  midNode,
                  highNode,
                  stowPos,
                  pickUpPos);   

    intake.cmdProcIntake(-xboxAux.getLeftY(),                      // Wrist Power manual
                        xboxAux.getLeftStickButton(),            // Soft Limit Override
                        (xboxAux.getRightTriggerAxis() >= 0.2),  // Rollers In
                        (xboxAux.getLeftTriggerAxis() >= 0.2), // Rollers Out
                        gamePiece,
                        lowNode,
                        midNode,
                        highNode,
                        stowPos,
                        pickUpPos
                        );  

    

   
      
    
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit()
  {

  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic()
  {

  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
