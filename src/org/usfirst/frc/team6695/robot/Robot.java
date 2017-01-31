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
	CANTalon ballMotor1 = new CANTalon(1);
	CANTalon ballMotor2 = new CANTalon(2);
	RobotDrive ballDrive = new RobotDrive(ballMotor1, ballMotor2);

	RobotDrive myRobot = new RobotDrive(0, 1, 2, 3);
	Joystick stick = new Joystick(0);
	//XboxController stick = new XboxController(0);
	Timer timer = new Timer();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);

			CvSink cvSink = CameraServer.getInstance().getVideo();
			CvSource os = CameraServer.getInstance().putVideo("os", 640, 480);

			Mat source = new Mat();
			Mat output = new Mat();
			Mat output2 = new Mat();

			while (!Thread.interrupted()) {
				cvSink.grabFrame(source);

				ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				Imgproc.findContours(output2.clone(), contours, new Mat(), Imgproc.RETR_LIST,
						Imgproc.CHAIN_APPROX_SIMPLE);
				MatOfPoint2f approxCurve = new MatOfPoint2f();
				int x = 1;

				for (int i = 0; i < contours.size(); i++) {
					MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());

					double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
					Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

					MatOfPoint points = new MatOfPoint(approxCurve.toArray());

					Rect rect = Imgproc.boundingRect(points);
					if (rect.height > 50 && rect.height < 100) {
						Rect roi = new Rect(rect.x, rect.y, rect.width, rect.height);
						Mat cropped = new Mat(output2, roi);
						Imgcodecs.imwrite("letter" + x + ".jpg", cropped);
						x++;
					}
				}

				os.putFrame(output);
			}
		}).start();
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
		// Drive for 2 seconds
		System.out.print("a");
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
		myRobot.arcadeDrive(stick);
		boolean buttonA = stick.getRawButton(1);
		boolean buttonB = stick.getRawButton(2);
		boolean buttonX = stick.getRawButton(3);
		boolean buttonY = stick.getRawButton(4);
		if (buttonX) {
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