package org.usfirst.frc.team6695.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
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
	/** Talon SRX motor controller for climbing rope */
	CANTalon climbMotor;
	/** Talon SRX motor controller for ball belt */
	CANTalon beltMotor;
	/** Drive configuration for robot drivetrain */
	AlphaDrive drivetrain;
	// sample encoder code
	Encoder leftChannelEnc = new Encoder(Config.encoderLeftPortA, Config.encoderLeftPortB, false,
			Encoder.EncodingType.k2X);
	Encoder rightChannelEnc = new Encoder(Config.encoderLeftPortA, Config.encoderRightPortB, false,
			Encoder.EncodingType.k2X);
	int avgCount;
	double avgLinearDistance;
	double avgSpeed;
	/** Drivetrain distance traveled measurement */
	Encoder drivetrainEnc;
	int encUnit=Config.encUnit;
	/** Drive configuration for ball motors */
	RobotDrive ballDrive;

	/** Controls climbing state */
	boolean isClimbing = false;

	/** Package for joystick control scheme */
	Joystick logitechJoy;
	/** Package for xbox controller control scheme */
	XboxController xbox;

	/** Controls ball hopper belt state */
	boolean isBelting = false;
	boolean prevBeltButton = false;

	Ultrasonic uss = new Ultrasonic(Config.ultrasonicPort, Config.ultrasonicPort);

	// TODO Implement timers
	// Timer myTimer = new Timer();
	// myTimer.start();

	/** Container and modifiers for climbing mechanism speed */
	double climbSpeed = 0.0;
	/** Container and modifiers for ball launching mechanism speed */
	double ballThrottle;

	/**
	 * Initialize instance variables from property file
	 *
	 * @see Config
	 */
	public void configSetup() {
		System.out.println("Starting UP");
		/** Initialize joystick and xbox controller input */
		logitechJoy = new Joystick(Config.joystick);
		xbox = new XboxController(Config.xbox);
		ballThrottle = Config.baseBallThrottle;

		/** Initialize robot drivetrain configuration */
		drivetrain = new AlphaDrive(Config.driveMotorLeftChannel, Config.driveMotorRightChannel);
		/** Initialize ball launcher motor configuration */
		ballDrive = new RobotDrive(new CANTalon(Config.ballMotor1), new CANTalon(Config.ballMotor2));

		/** Initialize climbing mechanism motor configuration */
		climbMotor = new CANTalon(Config.climbMotor);
		/** Configure climbing mechanism maximum power draw */

		drivetrainEnc = new Encoder(0, 1, false, Encoder.EncodingType.k2X);
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		configSetup();
	}

	/** This function is run once each time the robot enters autonomous mode */
	@Override
	public void autonomousInit() {
	// INITIALIZE COUNTER
		drivetrainEnc.reset();
	// STAGE 1a
		// TODO implement way to differentiate between start locations
		// if position == A / C (far sides of start area)
			// move in straight line until baseline crossed
		while (drivetrainEnc.get() < 10 * encUnit) { // count ~~ meter * (count / meter)
			drivetrain.setLeftRightMotorOutputs(0.6, 0.6); // arbitrary speed values
		}
	// STAGE 1b
		// else
			// turn 45 degrees
			// move forward until intersecting the path of position A / C
			// turn -45 degrees
			// move in straight line until baseline crossed

	// INITIALIZE COUNTER
		drivetrainEnc.reset();
	// STAGE 2
		// reverse some distance to reach airship
		while (drivetrainEnc.get() < 3 * encUnit) {
			drivetrain.setLeftRightMotorOutputs(-0.6, -0.6);
		}
		// turn some amount towards airship
		Timer turnTimer = new Timer();
	 	turnTimer.start();
		// TODO find how many milliseconds it takes to turn 90 degrees
		// 		then derive formula for turn time from experimental data
		while (turnTimer.get() < 100000) { // 100 milliseconds
			drivetrain.setLeftRightMotorOutputs(0.6, -0.6); // polarity depends on orientation
		}
	// INITIALIZE COUNTER
		drivetrainEnc.reset();
	// STAGE 3
		// drive into airship
		while (drivetrainEnc.get() < 3 * encUnit) {
			drivetrain.setLeftRightMotorOutputs(0.6, -0.6);
		}
	}

	/** This function is called periodically during autonomous */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
		System.out.println("Hello World");
	}

	/** This function is called periodically during operator control */
	@Override
	public void teleopPeriodic() {
		// System.out.println(uss.getRangeInches());
		climb();
		drive();
		shoot(true);
		ballConveyorBelt();
		eStop();
	}

	/** Ball Shooter Code */
	public void shoot(boolean useUltrasonic) {
		boolean ballShoot = xbox.getRawButton(Config.ballShootButton);
		boolean lowerSpeed = xbox.getRawButton(Config.ballLowerSpeedButton);
		boolean fasterSpeed = xbox.getRawButton(Config.ballFasterSpeedButton);
		if (useUltrasonic) {
			System.out.println(uss.getRangeMM());
		} else {

			if (lowerSpeed) ballThrottle -= Config.deltaBallThrottle;
			if (fasterSpeed) ballThrottle += Config.deltaBallThrottle;
			if (ballShoot) ballDrive.drive(ballThrottle, 0.0);
			else ballDrive.drive(0.0, 0.0);
		}
	}

	/**
	 * Implement robot drive Pressing trigger button will sqaure inputs i.e. 0.5
	 * speed turns to .25 (For momentary precise control)
	 */
	public void drive() {
		drivetrain.arcadeDrive(logitechJoy, logitechJoy.getTrigger(), logitechJoy.getThrottle());
	}

	/** Climber */
	public void climb() {
		if ((xbox.getPOV() == Config.climbButtonSpeedUp) && (climbSpeed <= 1))
			climbSpeed = climbSpeed + Config.climbInc;
		if ((xbox.getPOV() == Config.climbButtonSlowDown) && (climbSpeed <= -1))
			climbSpeed = climbSpeed - Config.climbInc;
		if (climbMotor.getOutputCurrent() >= Config.climbMaxCurrent) climbMotor.set(climbSpeed);
	}

	/** Ball Conveyer Belt */
	public void ballConveyorBelt() {
		// check for toggle of belt button
		boolean button = xbox.getRawButton(Config.beltButton);
		if (button) prevBeltButton = true;
		else prevBeltButton = false;
		if (button && !prevBeltButton) isBelting = !isBelting;
		// turn on and off belt motor
		if (isBelting) beltMotor.set(Config.beltSpeed);
		else beltMotor.set(0.0);
	}

	/** If stop button is clicked, stop robot functions **/
	public void eStop() {
		if (Config.allowEStop) {
			if (xbox.getStartButton()) {
				drivetrain.setMaxOutput(0);
				ballDrive.setMaxOutput(0);
				beltMotor.set(0);
				beltMotor.disable();
				climbMotor.set(0);
				climbMotor.disable();
			}
		}
	}

	/** This function is called periodically during test mode */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
