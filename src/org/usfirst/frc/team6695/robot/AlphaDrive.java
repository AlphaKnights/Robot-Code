package org.usfirst.frc.team6695.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;

/**
 * Alpha Knights version of Robot Drive
 * 
 * @see RobotDrive
 * @author Alpha Knights
 */
public class AlphaDrive extends RobotDrive {
	/**
	 * This just calls the super class...
	 * 
	 * @param leftMotorChannel
	 *            left motor PWM port
	 * @param rightMotorChannel
	 *            right motor PWM port
	 */
	public AlphaDrive(int leftMotorChannel, int rightMotorChannel) {
		super(leftMotorChannel, rightMotorChannel);
	}

	/**
	 * Arcade Drive With Throttle
	 * 
	 * @param stick
	 *            the joystick
	 * @param squaredInputs
	 *            add more control
	 * @param throttle
	 *            multiplied by speed. 0 to 1
	 */
	public void arcadeDrive(GenericHID stick, boolean squaredInputs, double throttle) {
		// simply call the full-featured arcadeDrive with the appropriate values
		arcadeDrive(stick.getY(), stick.getX(), squaredInputs, throttle);
	}

	/**
	 * This is the arcade drive with code in it! Yay!
	 * 
	 * @param moveValue
	 *            stick y
	 * @param rotateValue
	 *            stick x
	 * @param squaredInputs
	 *            extra control
	 * @param throttle
	 *            multiplied by speed. 0 to 1
	 */
	public void arcadeDrive(double moveValue, double rotateValue, boolean squaredInputs, double throttle) {
		// local variables to hold the computed PWM values for the motors
		if (!kArcadeStandard_Reported) {
			HAL.report(tResourceType.kResourceType_RobotDrive, getNumMotors(), tInstances.kRobotDrive_ArcadeStandard);
			kArcadeStandard_Reported = true;
		}

		double leftMotorSpeed;
		double rightMotorSpeed;

		moveValue = limit(moveValue);
		rotateValue = limit(rotateValue);

		if (squaredInputs) {
			// square the inputs (while preserving the sign) to increase fine
			// control while permitting full power
			if (moveValue >= 0.0) {
				moveValue = moveValue * moveValue;
			} else {
				moveValue = -(moveValue * moveValue);
			}
			if (rotateValue >= 0.0) {
				rotateValue = rotateValue * rotateValue;
			} else {
				rotateValue = -(rotateValue * rotateValue);
			}
		}

		if (moveValue > 0.0) {
			if (rotateValue > 0.0) {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = Math.max(moveValue, rotateValue);
			} else {
				leftMotorSpeed = Math.max(moveValue, -rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			}
		} else {
			if (rotateValue > 0.0) {
				leftMotorSpeed = -Math.max(-moveValue, rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			} else {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
			}
		}
		throttle = 1 - throttle;
		setLeftRightMotorOutputs(leftMotorSpeed * throttle, rightMotorSpeed * throttle);
	}

}
