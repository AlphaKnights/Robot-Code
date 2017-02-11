package org.usfirst.frc.team6695.robot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	/**
	 * Talon SRX motor controller for climbing rope
	 */
	CANTalon climbMotor;

	/**
	 * Drive configurations for front robot drivetrain
	 */
	AlphaDrive frontDrive;
	/**
	 * Drive configurations for back robot drivetrain
	 */
	AlphaDrive backDrive;

	/**
	 * Drive configuration for ball motors
	 */
	RobotDrive ballDrive;

	/**
	 * Controls climbing state
	 */
	boolean isClimbing = false;
	/**
	 * Modifies climbing power
	 */
	double climbMaxCurrent;

	/**
	 * Package for joystick control scheme
	 */
	Joystick logitechJoy;
	/**
	 * Package for xbox controller control scheme
	 */
	XboxController xbox;

	/**
	 * Controls ball hopper stirring state
	 */
	boolean isStirring = false;
	
	// TODO Implement timers
	//     Timer myTimer = new Timer();
	//	   myTimer.start();
	
	/**
	 * Configures property file
	 * @see "config.properties"
	 */
	Properties config = new Properties();
  double climbSpeed = 0.0;
	int climbButtonSpeedUp;
	int climbButtonSlowDown;
	double climbInc;
  /**
	 * Initialize instance variables from property file
	 * @see "config.properties"
	 */
	public void configSetup() {
		try {
			InputStream input = Robot.class.getResourceAsStream("config.properties");
			config.load(input);
		} catch (IOException e) {
			System.err.println("Could Not Read config");
			e.printStackTrace();
		}
		/**
		 * Initialize joystick and xbox controller input
		 */
		logitechJoy = new Joystick(Integer.parseInt(config.getProperty("joystick")));
		xbox = new XboxController(Integer.parseInt(config.getProperty("xbox")));
		
		/**
		 * Initialize robot drivetrain configuration
		 */
		frontDrive = new AlphaDrive(Integer.parseInt(config.getProperty("driveMotorFrontLeft")),
				Integer.parseInt(config.getProperty("driveMotorFrontRight")));
		backDrive = new AlphaDrive(Integer.parseInt(config.getProperty("driveMotorBackLeft")),
				Integer.parseInt(config.getProperty("driveMotorBackRight")));
		/**
		 * Initialize ball launcher motor configuration
		 */
		ballDrive = new RobotDrive(new CANTalon(Integer.parseInt(config.getProperty("ballMotor1"))),
				new CANTalon(Integer.parseInt(config.getProperty("ballMotor2"))));
		
		/**
		 * Initialize climbing mechanism motor configuration
		 */
		climbMotor = new CANTalon(Integer.parseInt(config.getProperty("climbMotor")));
		/**
		 * Configure climbing mechanism maximum power draw
		 */
		climbMaxCurrent = Double.parseDouble(config.getProperty("climbMaxCurrent"));
		climbButtonSpeedUp = Integer.parseInt(config.getProperty("climbButtonSpeedUp"));
		climbButtonSlowDown = Integer.parseInt(config.getProperty("climbButtonSlowDown"));
		climbInc = Double.parseDouble(config.getProperty("climbInc"));
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		configSetup();
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
		// TODO Implement Timer
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		// TODO Make Autonomous work...
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		System.out.println("Hello World");
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		climb();
		drive();
		shoot();

		/**
		 * Implement xbox controller main button usage
		 */
		boolean buttonA = xbox.getRawButton(1);
		boolean buttonB = xbox.getRawButton(2);
		boolean buttonY = xbox.getRawButton(4);

		if (buttonY) System.out.println("pressedY");
		if (buttonA) System.out.println("pressedA");
		if (buttonB) System.out.println("pressedB");
	}
	
	/**
	 * Ball Shooter Code
	 */
	public void shoot() {
		boolean buttonX = xbox.getRawButton(3);
		if (buttonX) ballDrive.drive(1.0, 0.0);
		else ballDrive.drive(0.0, 0.0);
	}
	/**
	 * Implement robot drive
	 */
	public void drive() {
		frontDrive.arcadeDrive(logitechJoy, false, logitechJoy.getThrottle());
		backDrive.arcadeDrive(logitechJoy, false, logitechJoy.getThrottle());
	}

	/**
	 * Climber
	 */
	public void climb() {
		if ((xbox.getPOV() == climbButtonSpeedUp) && (climbSpeed <= 1))
			climbSpeed = climbSpeed + climbInc;
		if ((xbox.getPOV() == climbButtonSlowDown) && (climbSpeed <= -1))
			climbSpeed = climbSpeed - climbInc;
		if (climbMotor.getOutputCurrent() >= climbMaxCurrent)
			climbMotor.set(climbSpeed);
	}
	/**
	 * This function is called periodically during test mode
	 */
	
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}