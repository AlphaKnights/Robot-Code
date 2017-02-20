package org.usfirst.frc.team6695.robot;

/**
 * Class that holds all the unchanging config values
 * 
 * @author Alpha Knights
 *
 */
public final class Config {

	// Motors
	/** Left side PWM Port */
	static int driveMotorLeftChannel = 0;
	/** Right Side PWM port */
	static int driveMotorRightChannel = 1;
	/** Ball shooter motor 1 */
	static int ballMotor1 = 2;
	/** Ball Shooter motor 2 */
	static int ballMotor2 = 3;
	/** Climb Motor */
	static int climbMotor = 1;
	/** Ball Stir Motor */
	static int ballStirMotor = 4;

	// Buttons
	static int ClimbHoldButton = XboxButtonID.Back.value();
	/** Button To press to speedup climb motor */
	static int climbButtonSpeedUp = XboxPOVID.UP.value();
	/** Button to press to slow down climb motor */
	static int climbButtonSlowDown = XboxPOVID.DOWN.value();
	/** This button turns on or off the belt */
	static int beltButton = XboxButtonID.Y.value();
	/** Button to press to shoot */
	static int ballShootButton = XboxButtonID.X.value();
	/** Button to lower the speed of ball shooter */
	static int ballLowerSpeedButton = XboxButtonID.LB.value();
	/** Button to speed up the ball shooter */
	static int ballFasterSpeedButton = XboxButtonID.RB.value();

	// INPUTS
	/** Joystick Port */
	static int joystick = 0;// TODO DEPENDS ON SIDE
	/** xbox port */
	static int xbox = 1;// TODO DEPENDS ON SIDE

	// DIO PORTS
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

	// DELTAs
	/** How much to increment climb speed */
	static double climbInc = 0.05;
	/** Start speed of ball launcher */
	static double baseBallThrottle = 0.5;
	/** How much to change ball speed by per click */
	static double deltaBallThrottle = 0.01;

	// Speeds
	/** speed of belt */
	static double beltSpeed = 0.2;
	/** Speed To go when holding */
	static double holdSpeed = 0.0;// TODO FIND SPEED

	// Maxumums
	/** Max Current of climb motor */
	static double climbMaxCurrent = 100000.0; // TODO FIND MAX CURRENT

	// Other....
	/** Should we allow estop? Only for testing IMO **/
	static boolean allowEStop = true;
	/** Drivetrain encoder unit definition (counts / meter) */
	static int encUnit = 100; // TODO requires testing, means nothing atm
	/** True/False for logging */
	static boolean logging = true;
}
