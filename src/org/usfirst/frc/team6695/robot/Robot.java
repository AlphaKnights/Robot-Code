package org.usfirst.frc.team6695.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Ultrasonic;
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
	int avgCount;
	double avgLinearDistance;
	double avgSpeed;
	/** Drivetrain left distance traveled measurement */
	Encoder drivetrainEncLeft = new Encoder(Config.encoderLeftPortA, Config.encoderLeftPortB, false, EncodingType.k2X);
	/** Drivetrain right distance traveled measurement */
	Encoder drivetrainEncRight = new Encoder(Config.encoderRightPortA, Config.encoderRightPortB, false,
			EncodingType.k2X);
	
	/** Drive configuration for ball motors */
	RobotDrive ballDrive;
	/** Power Distribution Panel */
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	/** Controls climbing state */
	boolean isClimbing = false;

	/** Package for joystick control scheme */
	Joystick logitechJoy;
	/** Package for xbox controller control scheme */
	XboxController xbox;

	/** Controls ball hopper belt state */
	boolean isBelting = false;
	/** Belt Button Pushed Before this loop */
	boolean prevBeltButton = false;
	/** Ball Shoot button Pressed before */
	boolean prevBallShooter = false;

	/** Lower Speed pre (Ball shooter) */
	boolean lsp = false;
	/** inc speed pre (Ball shooter) */
	boolean isp = false;

	/** Ultrasonic Sensor */
	Ultrasonic ultrasonic = new Ultrasonic(Config.ultrasonicPort, Config.ultrasonicPort);
	/** Input for left switch */
	DigitalInput leftSwitch = new DigitalInput(Config.leftSwitchDIO);
	/** Input for right switch */
	DigitalInput rightSwitch = new DigitalInput(Config.rightSwitchDIO);

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

		beltMotor = new CANTalon(Config.ballStirMotor);
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture();
		configSetup();
	}

	/** This function is run once each time the robot enters autonomous mode */
	@Override
	public void autonomousInit() {
		// autonomous();
		DriveDistance(0.5, 10);
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
		drivetrain.drive(0, 0);
		drivetrainEncLeft.reset();
		drivetrainEncRight.reset();
	}

	/** This function is called periodically during operator control */
	@Override
	public void teleopPeriodic() {
		// System.out.println(drivetrainEncLeft.getDistance());
		//
		// System.out.println("loop");
		//
		// if (xbox.getAButton()) {
		// drivetrain.drive(-.3, 0);
		// } else {
		// drivetrain.drive(0, 0);
		// }

		// // System.out.println(uss.getRangeInches());
		// System.err.println(drivetrainEncLeft.getDistance());
		// if (Config.logging) System.out.println("Total Current: " +
		// pdp.getTotalCurrent());
		//
		climb();
		drive();
		shoot();
		// ballConveyorBelt();
		eStop();
		getSpeeds();
		testUltrasonic();
	}

	public void testUltrasonic() {
		ultrasonic.setEnabled(true);
		System.out.println(ultrasonic.getRangeInches());
	}

	/** Approxomate the ball speed when button is clicked **/
	public void APS() {
		ultrasonic.setEnabled(true);
		if (logitechJoy.getRawButton(Config.getAppxBallButton)) {
			ballThrottle = (ultrasonic.getRangeInches() * Config.speedDistanceRatio);
			System.out.println("Set Speed: " + ballThrottle);
		}
	}

	/**
	 * Ball Shooter Code
	 * 
	 * @param useUltrasonic
	 *            True if ultrasonic should be used to calculate shoot speed.
	 *            DOES NOT WORK. Keep False
	 **/
	public void shoot() {
		boolean ballShoot = xbox.getRawButton(Config.ballShootButton);

		if (ballShoot && !prevBallShooter) prevBallShooter = !prevBallShooter;
		prevBallShooter = ballShoot;
		boolean lowerSpeed = xbox.getRawButton(Config.ballLowerSpeedButton);
		boolean fasterSpeed = xbox.getRawButton(Config.ballFasterSpeedButton);
		if (ballShoot && !prevBallShooter) {
			System.out.println("Ball backward");
			ballDrive.drive(-1, 0);
		} else if (ballShoot && prevBallShooter) {
			System.out.print("Ball forward");
			if (lowerSpeed && !lsp) ballThrottle -= Config.deltaBallThrottle;
			if (fasterSpeed && !isp) ballThrottle += Config.deltaBallThrottle;
			if (ballShoot) ballDrive.drive(-ballThrottle, 0.0);
			else ballDrive.drive(0.0, 0.0);
		}
		lsp = lowerSpeed;
		isp = fasterSpeed;
	}

	/**
	 * Implement robot drive pressing trigger button will square inputs i.e. 0.5
	 * speed turns to .25 (For momentary precise control)
	 */
	public void drive() {
		drivetrain.arcadeDrive(logitechJoy, logitechJoy.getTrigger(), logitechJoy.getThrottle());
	}

	/** Climber */
	Boolean holding = false;
	Boolean PrevHolding = false;

	public void climb() {
		boolean button = xbox.getRawButton(Config.ClimbHoldButton);

		if (button && !PrevHolding) holding = !holding;
		PrevHolding = button;
		if (Config.logging) System.out.println("Motor Current: " + climbMotor.getOutputCurrent());

		if (!holding) {
			if (Config.logging) System.out.println("Climb Motor Current:" + climbMotor.getOutputCurrent());

			if ((xbox.getPOV() == Config.climbButtonSpeedUp) && (climbSpeed <= 1))
				climbSpeed = climbSpeed + Config.climbInc;

			if ((xbox.getPOV() == Config.climbButtonSlowDown) && (climbSpeed >= -1))
				climbSpeed = climbSpeed - Config.climbInc;

			if (climbMotor.getOutputCurrent() <= Config.climbMaxCurrent) climbMotor.set(climbSpeed);
			else climbMotor.set(0);

		} else {
			climbMotor.set(Config.holdSpeed);
		}

	}

	/** Ball Conveyer Belt */
	@Deprecated
	public void ballConveyorBelt() {
		// check for toggle of belt button
		boolean button = xbox.getRawButton(Config.beltButton);
		if (button && !prevBeltButton) isBelting = !isBelting;
		if (button) prevBeltButton = true;
		else prevBeltButton = false;
		// turn on and off belt motor
		if (isBelting) beltMotor.set(Config.beltSpeed);
		else beltMotor.set(0.0);
	}

	/** If stop button is clicked, stop robot functions **/
	public void eStop() {
		if (Config.allowEStop) {
			if (xbox.getRawButton(Config.eStopButton)) {
				System.err.println("ESTOP");
				drivetrain.setMaxOutput(0);
				ballDrive.setMaxOutput(0);
				beltMotor.set(0);
				beltMotor.disable();
				climbMotor.set(0);
				climbMotor.disable();
			}
			if (xbox.getRawButton(Config.eStartButton)) {
				System.err.println("ESTART");
				drivetrain.setMaxOutput(1);
				ballDrive.setMaxOutput(1);
				beltMotor.enable();
				climbMotor.enable();
			}

		}
	}

	public void DriveDistance(double speed, double feet) {
		drivetrainEncLeft.reset();
		while (Math.abs(drivetrainEncLeft.get()) < Math.abs(feet * Config.encUnit))
			drivetrain.drive(speed, 0);
		;
	}

	public void autonomous() {
		// STAGE 1a
		// TODO implement way to differentiate between start locations
		// if position == A / C (far sides of start area)
		// move in straight line until baseline crossed
		DriveDistance(0.5, 10);

		// STAGE 1b
		// else
		// turn 45 degrees
		// move forward until intersecting the path of position A / C
		// turn -45 degrees
		// move in straight line until baseline crossed

		// STAGE 2
		// reverse some distance to reach airship
		// DriveDistance(-0.6, -0.6, 3);

		// turn some amount towards airship
		// DriveDistance(-0.6, 0.6, 3);

		// STAGE 3
		// drive into airship
		// DriveDistance(0.6, 0.6, 3);

	}

	public void getSpeeds() {
		if (xbox.getRawButton(Config.getSpeed)) {
			System.out.println("Climb Speed: " + climbSpeed);
			System.out.println("Ball Speed: " + ballThrottle);
		}
	}

	/** This function is called periodically during test mode */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
