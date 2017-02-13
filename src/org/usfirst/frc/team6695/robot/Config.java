package org.usfirst.frc.team6695.robot;

public class Config {
	final static int driveMotorLeftChannel = 0;
	final static int driveMotorRightChannel = 1;
	final static int ballMotor1 = 1;
	final static int ballMotor2 = 2;
	final static int climbMotor = 3;
	final static boolean stir = false;
	final static double climbMaxCurrent = 1.0;
	final static int joystick = 0;
	final static int xbox = 1;
	final static int climbButtonSpeedUp = XboxPOVID.UP.value();
	final static int climbButtonSlowDown = XboxPOVID.DOWN.value();
	final static double climbInc = 0.1;
	final static double baseBallThrottle = 0.5;
	final static double deltaBallThrottle = 0.05;
	final static double beltSpeed = 0.2;
	final static int beltButton = XboxButtonID.Y.value();
	final static int ballShootButton = XboxButtonID.X.value();
	final static int ballLowerSpeedButton = XboxButtonID.LB.value();
	final static int ballFasterSpeedButton = XboxButtonID.RB.value();
}
