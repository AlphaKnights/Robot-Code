package org.usfirst.frc.team6695.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
// TODO import edu.wpi.first.wpilibj.Timer;
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
	 * Talon SRX motor controller for ball belt
	 */
	CANTalon beltMotor;
	/**
	 * Drive configuration for robot drivetrain
	 */
	AlphaDrive drivetrain;
	// sample encoder code
	Encoder leftChannelEnc = new Encoder(0, 1, false, Encoder.EncodingType.k2X);
	Encoder rightChannelEnc = new Encoder(2, 3, false, Encoder.EncodingType.k2X);
	int avgCount;
	double avgLinearDistance;
	double avgSpeed;
	
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
	 * Controls ball hopper belt state
	 */
	boolean isBelting = false;
	boolean prevBeltButton = false;
	double beltSpeed;
	
	int beltButton;
	int ballShootButton;
	int ballLowerSpeedButton;
	int ballFasterSpeedButton;
	
	// TODO Implement timers
	//     Timer myTimer = new Timer();
	//	   myTimer.start();
	
	/**
	 * Container and modifiers for climbing mechanism speed
	 */
	double climbSpeed = 0.0;
	int climbButtonSpeedUp;
	int climbButtonSlowDown;
	double climbInc;
	
	/**
	 * Container and modifiers for ball launching mechanism speed
	 */
	double baseBallThrottle;
	double deltaBallThrottle;
	double ballThrottle;
	
	/**
	 * Initialize instance variables from property file
	 * @see "config.properties"
	 */
	public void configSetup() {
		System.out.println("Starting UP");
		/**
		 * Initialize joystick and xbox controller input
		 */
		logitechJoy = new Joystick(Config.joystick);
		xbox = new XboxController(Config.xbox);
		
		/**
		 * Initialize robot drivetrain configuration
		 */
		drivetrain = new AlphaDrive(Config.driveMotorLeftChannel,Config.driveMotorRightChannel);
		/**
		 * Initialize ball launcher motor configuration
		 */
		ballDrive = new RobotDrive(new CANTalon(Config.ballMotor1),
				new CANTalon(Config.ballMotor2));
		
		/**
		 * Initialize climbing mechanism motor configuration
		 */
		climbMotor = new CANTalon(Config.climbMotor);
		/**
		 * Configure climbing mechanism maximum power draw
		 */
		climbMaxCurrent = Config.climbMaxCurrent;
		climbButtonSpeedUp = Config.climbButtonSpeedUp;
		climbButtonSlowDown = Config.climbButtonSlowDown;
		climbInc = Config.climbInc;
		baseBallThrottle = Config.baseBallThrottle;
		deltaBallThrottle = Config.deltaBallThrottle;
		beltSpeed = Config.beltSpeed;
		beltButton = Config.beltButton;
		ballShootButton = Config.ballShootButton;
		ballLowerSpeedButton = Config.ballLowerSpeedButton;
		ballFasterSpeedButton = Config.ballFasterSpeedButton;
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
		avgCount = (leftChannelEnc.get() + rightChannelEnc.get()) / 2;
		avgLinearDistance = (leftChannelEnc.getDistance() + rightChannelEnc.getDistance()) / 2;
		avgSpeed = (leftChannelEnc.getRate() + rightChannelEnc.getRate()) / 2;
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		System.out.println("Hello World");
		ballThrottle = baseBallThrottle;
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		climb();
		drive();
		shoot();
		ballConveyorBelt();

		/**
		 * Implement xbox controller main button usage
		 */
		boolean buttonA = xbox.getRawButton(1);
		boolean buttonB = xbox.getRawButton(2);

		if (buttonA) System.out.println("pressedA");
		if (buttonB) System.out.println("pressedB");
	}
	
	/**
	 * Ball Shooter Code
	 */
	public void shoot() {
		boolean ballShoot = xbox.getRawButton(ballShootButton);
		boolean lowerSpeed = xbox.getRawButton(ballLowerSpeedButton);
		boolean fasterSpeed = xbox.getRawButton(ballFasterSpeedButton);
		if (lowerSpeed) ballThrottle -= deltaBallThrottle;
		if (fasterSpeed) ballThrottle += deltaBallThrottle;
		if (ballShoot) ballDrive.drive(ballThrottle, 0.0);
		else ballDrive.drive(0.0, 0.0);
	}
	/**
	 * Implement robot drive
	 * Pressing trigger button will sqaure inputs
	 * i.e. 0.5 speed turns to .25 (For momentary precise control)
	 */
	public void drive() {
		drivetrain.arcadeDrive(logitechJoy, logitechJoy.getTrigger(), logitechJoy.getThrottle());
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
	 * Ball Conveyer Belt
	 */
	public void ballConveyorBelt() {
		//check for toggle of belt button
		boolean button = xbox.getRawButton(beltButton);
		if(button) prevBeltButton = true;
		else prevBeltButton = false;
		if(button && !prevBeltButton) isBelting = !isBelting;
		//turn on and off belt motor
		if(isBelting) beltMotor.set(beltSpeed);
		else beltMotor.set(0.0);
	}
	/**
	 * This function is called periodically during test mode
	 */
	
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}