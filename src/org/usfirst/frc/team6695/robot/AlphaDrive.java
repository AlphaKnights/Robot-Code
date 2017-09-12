package org.usfirst.frc.team6695.robot;

import static java.util.Objects.requireNonNull;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;

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
	
	public AlphaDrive(final int frontLeftMotor, final int rearLeftMotor, final int frontRightMotor,
            final int rearRightMotor) {
		super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
//		m_sensitivity = kDefaultSensitivity;
//	    m_maxOutput = kDefaultMaxOutput;
//	    m_rearLeftMotor = new Talon(rearLeftMotor);
//	    m_rearRightMotor = new Talon(rearRightMotor);
//	    m_frontLeftMotor = new Talon(frontLeftMotor);
//	    m_frontRightMotor = new Talon(frontRightMotor);
//	    m_allocatedSpeedControllers = true;
//	    drive(0, 0);
	}
	  /**
	   * Constructor for RobotDrive with 4 motors specified as SpeedController objects. Speed controller
	   * input version of RobotDrive (see previous comments).
	   *
	   * @param frontLeftMotor  The front left SpeedController object used to drive the robot
	   * @param rearLeftMotor   The back left SpeedController object used to drive the robot.
	   * @param frontRightMotor The front right SpeedController object used to drive the robot.
	   * @param rearRightMotor  The back right SpeedController object used to drive the robot.
	   */
	  public AlphaDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor,
	                    SpeedController frontRightMotor, SpeedController rearRightMotor) {
		  super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	    m_frontLeftMotor = requireNonNull(frontLeftMotor, "frontLeftMotor cannot be null");
	    m_rearLeftMotor = requireNonNull(rearLeftMotor, "rearLeftMotor cannot be null");
	    m_frontRightMotor = requireNonNull(frontRightMotor, "frontRightMotor cannot be null");
	    m_rearRightMotor = requireNonNull(rearRightMotor, "rearRightMotor cannot be null");
	    m_sensitivity = kDefaultSensitivity;
	    m_maxOutput = kDefaultMaxOutput;
	    m_allocatedSpeedControllers = false;
	    //setupMotorSafety();
	    drive(0, 0);
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
			if (moveValue >= 0.0) moveValue = moveValue * moveValue;
			else moveValue = -(moveValue * moveValue);

			if (rotateValue >= 0.0) rotateValue = rotateValue * rotateValue;
			else rotateValue = -(rotateValue * rotateValue);

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
		setLeftRightMotorOutputs(rightMotorSpeed * throttle, leftMotorSpeed * throttle);
	}
	
	public double getLeft()  { return m_rearLeftMotor.get(); }
	public double getRight() { return m_rearRightMotor.get(); }

}
