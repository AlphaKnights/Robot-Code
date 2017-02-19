package org.usfirst.frc.team6695.robot;

/**
 * Class that holds all the unchanging config values
 * 
 * @author Alpha Knights
 *
 */
final public class Config {
	/** Should we allow estop? Only for testing IMO **/
	static boolean allowEStop = true;
	/** Left side PWM Port */
	static int driveMotorLeftChannel = 0;
	/** Right Side PWM port */
	static int driveMotorRightChannel = 1;
	/** Ball shooter motor 1 */
	static int ballMotor1 = 1;
	/** Ball Shooter motor 2 */
	static int ballMotor2 = 2;
	/** Climb Motor */
	static int climbMotor = 3;
	/** Max Current of climb motor */
	static double climbMaxCurrent = 1.0;
	/** Joystick Port */
	static int joystick = 0;
	/** xbox port */
	static int xbox = 1;
	/** Button To press to speedup climb motor */
	static int climbButtonSpeedUp = XboxPOVID.UP.value();
	/** Button to press to slow down climb motor */
	static int climbButtonSlowDown = XboxPOVID.DOWN.value();
	/** How much to increment climb speed */
	static double climbInc = 0.1;
	/** Start speed of ball launcher */
	static double baseBallThrottle = 0.5;
	/** How much to change ball speed by per click */
	static double deltaBallThrottle = 0.05;
	/** speed of belt */
	static double beltSpeed = 0.2;
	/** This button turns on or off the belt */
	static int beltButton = XboxButtonID.Y.value();
	/** Button to press to shoot */
	static int ballShootButton = XboxButtonID.X.value();
	/** Button to lower the speed of ball shooter */
	static int ballLowerSpeedButton = XboxButtonID.LB.value();
	/** Button to speed up the ball shooter */
	static int ballFasterSpeedButton = XboxButtonID.RB.value();
	/** DIO port for ultrasonic **/
	static int ultrasonicPort = 4;
	/** DIO Port #1 For Right Encoder */
	static int encoderRightPortA = 0;
	/** DIO Port #2 For Right Encoder */
	static int encoderRightPortB = 1;
	/** DIO Port #1 For Left Encoder */
	static int encoderLeftPortA = 2;
	/** DIO Port #2 For Left Encoder */
	static int encoderLeftPortB = 3;
	/** Drivetrain encoder unit definition (counts / meter) */
	static int encUnit = 100; // TODO requires testing, means nothing atm
}
