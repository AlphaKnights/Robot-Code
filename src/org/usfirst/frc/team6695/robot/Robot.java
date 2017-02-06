package org.usfirst.frc.team6695.robot;

import java.awt.List;
import java.util.ArrayList;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.ctre.CANTalon;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
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
	CANTalon ballMotor1 = new CANTalon(1);
	CANTalon ballMotor2 = new CANTalon(2);
	CANTalon climbMotor1 = new CANTalon(3);

	final int driveMoterRight1 = 1;// values are subject to change
	final int driveMoterRight2 = 2;
	final int driveMoterLeft1 = 3;
	final int driveMoterLeft2 = 4;

	// Drives
	RobotDrive drive1 = new RobotDrive(driveMoterLeft1, driveMoterRight1);
	RobotDrive drive2 = new RobotDrive(driveMoterLeft2, driveMoterRight2);
	RobotDrive ballDrive = new RobotDrive(ballMotor1, ballMotor2);

	// states
	boolean isClimbing = false;
	boolean bHasBeenHeld = false;

	Joystick stick = new Joystick(0);
	// XboxController stick = new XboxController(0);
	Timer timer = new Timer();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
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
	//TODO Make Autonomous work...
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
			drive1.arcadeDrive(stick);
			drive2.arcadeDrive(stick);
		} else {// we must be climbing
			climbMotor1.set(stick.getY() * stick.getThrottle());
			//TODO Motor Must stop before we break the button
		}

		boolean buttonA = stick.getRawButton(1);
		boolean buttonB = stick.getRawButton(2);
		boolean buttonX = stick.getRawButton(3);
		boolean buttonY = stick.getRawButton(4);

		if (buttonB && !bHasBeenHeld) {// if b is clicked, we are in climbing
										// mode
			isClimbing = !isClimbing;
			System.out.println("Current Climbing State: " + isClimbing);
		}
		// So holding down the button does not rapidly switch between climbing
		// and driving
		if (buttonB)
			bHasBeenHeld = true;
		else
			bHasBeenHeld = false;

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