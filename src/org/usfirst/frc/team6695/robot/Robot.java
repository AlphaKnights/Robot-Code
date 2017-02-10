package org.usfirst.frc.team6695.robot;

import java.io.FileInputStream;
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
	// motors
	CANTalon climbMotor1 = null;

	// Drives
	RobotDrive frontDrive;
	RobotDrive backDrive;
	RobotDrive ballDrive;

	// states
	boolean isClimbing = false;
	boolean bPreviouslyHeld = false;

	// Control Input
	Joystick joystick = new Joystick(0);
	XboxController xbox = new XboxController(1);

	// timers
	Timer timer = new Timer();

	// config
	Properties prop = new Properties();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		try {
			InputStream input = Robot.class.getResourceAsStream("config.properties");
			prop.load(input);
		} catch (IOException e) {
			System.err.println("Could Not Read config");
			e.printStackTrace();
		}
		frontDrive = new RobotDrive(Integer.parseInt(prop.getProperty("driveMotorFrontLeft")),
				Integer.parseInt(prop.getProperty("driveMotorFrontRight")));
		backDrive = new RobotDrive(Integer.parseInt(prop.getProperty("driveMotorBackLeft")),
				Integer.parseInt(prop.getProperty("driveMotorBackRight")));
		ballDrive = new RobotDrive(new CANTalon(Integer.parseInt(prop.getProperty("ballMotor1"))),
				new CANTalon(Integer.parseInt(prop.getProperty("ballMotor1"))));
		climbMotor1 = new CANTalon(Integer.parseInt(prop.getProperty("climbMotor1")));
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
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
		System.out.println("hilo woild");
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if (!isClimbing) {// If we are not climbing, we are driving
			frontDrive.arcadeDrive(joystick);
			backDrive.arcadeDrive(joystick);
		} else {// we must be climbing
			if (climbMotor1.getOutputCurrent() < 1.0)
				climbMotor1.set(joystick.getY() * joystick.getThrottle());
			// TODO Motor Must stop before we break the button
		}

		boolean buttonA = xbox.getRawButton(1);
		boolean buttonB = xbox.getRawButton(2);
		boolean buttonX = xbox.getRawButton(3);
		boolean buttonY = xbox.getRawButton(4);

		if (buttonB && !bPreviouslyHeld) {// if b is clicked, we are in climbing
											// mode
			isClimbing = !isClimbing;
			System.out.println("Current Climbing State: " + isClimbing);
		}
		// So holding down the button does not rapidly switch between climbing
		// and driving
		if (buttonB)
			bPreviouslyHeld = true;
		else
			bPreviouslyHeld = false;

		if (buttonX) {// if we hold x, ball shooter should shoot.
			System.out.println("pressedX");
			ballDrive.drive(1.0, 0.0);
		} else {
			ballDrive.drive(0.0, 0.0);
		}

		if (buttonY) {
			System.out.println("pressedY");
		}
		if (buttonA) {
			System.out.println("pressedA");
		}
		if (buttonB) {
			System.out.println("pressedB");
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}