package org.usfirst.frc.team6695.robot;
/**
 * Class that holds all the unchanging config values
 * @author Michael
 *
 */
public class Config {
	/** Left side DIO Port */
	final static int driveMotorLeftChannel = 0;
	/** Right Side DIO port */
	final static int driveMotorRightChannel = 1;
	/** Ball shooter motor 1 */
	final static int ballMotor1 = 1;
	/** Ball Shooter motor 2 */
	final static int ballMotor2 = 2;
	/** Climb Motor */
	final static int climbMotor = 3;
	/** Stir starting state */
	final static boolean stir = false;
	/** Max Current of climb motor */
	final static double climbMaxCurrent = 1.0;
	/** Joystick Port */
	final static int joystick = 0;
	/** xbox port */
	final static int xbox = 1;
	/** Button To press to speedup climb motor */
	final static int climbButtonSpeedUp = XboxPOVID.UP.value();
	/** Button to press to slow down climb motor */
	final static int climbButtonSlowDown = XboxPOVID.DOWN.value();
	/** How much to increment climb speed */
	final static double climbInc = 0.1;
	/** Start speed of ball launcher */
	final static double baseBallThrottle = 0.5;
	/** How much to change ball speed by per click */
	final static double deltaBallThrottle = 0.05;
	/** speed of belt */
	final static double beltSpeed = 0.2;
	/** This button turns on or off the belt */
	final static int beltButton = XboxButtonID.Y.value();
	/** Button to press to shoot */
	final static int ballShootButton = XboxButtonID.X.value();
	/** Button to lower the speed of ball shooter */
	final static int ballLowerSpeedButton = XboxButtonID.LB.value();
	/** Button to speed up the ball shooter */
	final static int ballFasterSpeedButton = XboxButtonID.RB.value();
}
