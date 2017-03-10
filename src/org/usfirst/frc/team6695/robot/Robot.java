package org.usfirst.frc.team6695.robot;

import org.usfirst.frc.team6695.robot.ModeSelector.Mode;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	/** Container and modifiers for climbing mechanism speed */
	double climbSpeed = 0.0;
	/** Container and modifiers for ball launching mechanism speed */
	double ballThrottle;
	/** Get Button Info for auto */
	ModeSelector ms = new ModeSelector(Config.leftSwitchDIO, Config.rightSwitchDIO);

	/** Autonomous Kill */
	boolean teleOpCalled = false;
	Timer autotime = new Timer();
	

	Boolean climbHolding = false;
	Boolean climbPrevHolding = false;

	/**
	 * Initialize instance variables from property file
	 *
	 * @see Config
	 */
	public void configSetup() {
		System.out.println("Starting UP");
		logitechJoy = new Joystick(Config.joystick);
		xbox = new XboxController(Config.xbox);
		ballThrottle = Config.baseBallThrottle;
		drivetrain = new AlphaDrive(Config.driveMotorLeftChannel, Config.driveMotorRightChannel);
		ballDrive = new RobotDrive(new CANTalon(Config.ballMotor1), new CANTalon(Config.ballMotor2));
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
		autotime.reset();
		autotime.start();
		autonomous();
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
		drivetrainEncLeft.reset();
		drivetrainEncRight.reset();
		teleOpCalled = true;
		drivetrain.drive(0, 0);
		drivetrainEncLeft.reset();
		drivetrainEncRight.reset();
	}

	/** This function is called periodically during operator control */
	@Override
	public void teleopPeriodic() {
		climb();
		shoot();
		getSpeeds();
		drivetrain.arcadeDrive(logitechJoy, logitechJoy.getTrigger(), logitechJoy.getThrottle());
		System.out.println("In Loop " + Math.abs(drivetrainEncLeft.get()) + " " + Math.abs(drivetrainEncRight.get()));
		
	}

	/**
	 * Ball Shooter Code
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
	@Deprecated
	public void drive() {
		drivetrain.arcadeDrive(logitechJoy, logitechJoy.getTrigger(), logitechJoy.getThrottle());
	}

	/** Climber */
	public void climb() {
		boolean button = xbox.getRawButton(Config.ClimbHoldButton);

		if (button && !climbPrevHolding) climbHolding = !climbHolding;
		climbPrevHolding = button;
		if (Config.logging) System.out.println("Motor Current: " + climbMotor.getOutputCurrent());

		if (!climbHolding) {
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

	public void DriveDistance(double speed, double feet) {
		drivetrainEncLeft.reset();
		drivetrainEncRight.reset();
		while (Math.abs(drivetrainEncLeft.get()) < Math.abs(feet * Config.encUnit) && Math.abs(drivetrainEncRight.get()) < Math.abs(feet * Config.encUnit) && !teleOpCalled
				&& autotime.get() < 15) {
			drivetrain.drive(-speed, 0);
			System.out.println("In Loop " + Math.abs(drivetrainEncLeft.get()) + " " + Math.abs(drivetrainEncRight.get()));
		}
	}

	public void turn(double deg, double speed) {
		drivetrainEncLeft.reset();
		drivetrainEncRight.reset();
		if (deg > 0) while (Math.abs(drivetrainEncRight.get()) < Math.abs(deg * Config.degUnit) && Math.abs(drivetrainEncLeft.get()) < Math.abs(deg * Config.degUnit) && !teleOpCalled
				&& autotime.get() < 15)
			drivetrain.drive(-speed, 1);
		else if (deg < 0) while (Math.abs(drivetrainEncLeft.get()) < Math.abs(deg * Config.degUnit) && Math.abs(drivetrainEncRight.get()) < Math.abs(deg * Config.degUnit) && !teleOpCalled
				&& autotime.get() < 15)
			drivetrain.drive(-speed, 1);

	}

	public void autonomous() {
		/** linear distance from starting position A / C to baseline */
		double distToBaseline = 187.5 / 12;
		/** linear distance from baseline to halfmark, used by all positions */
		double distToHalfmark = (distToBaseline - 94.5 / 12) / 12;
		/**
		 * linear distance from position B to halfmark, merging with C position
		 * autonomous
		 */
		double diagDistToHalfmark = 131 / 12;
		/** linear distance from halfmark to gear loader, used by all */
		double distToLoader = 68 / 12;
		/**
		 * radial distance in degrees between baseline and gear loader from
		 * halfmark
		 */
		double degToLoader = 60;

		if (ms.getMode() == Mode.OnOff) {// A
			System.out.println("POS A");
			// Drive to baseline
			DriveDistance(0.6, distToBaseline);
			// Drive back to halfmark
			DriveDistance(-0.6, distToHalfmark);
			// Turn towards gear drop-off
			turn(degToLoader, 0.6);
			// Drive into gear loader
			DriveDistance(0.6, distToLoader);
		} else if (ms.getMode() == Mode.OffOn) { // C
			System.out.println("POS C");
			// Drive to baseline
			DriveDistance(0.6, distToBaseline);
			// Drive back to halfmark
			DriveDistance(-0.6, distToHalfmark);
			// Turn towards gear drop-off
			turn(-degToLoader, 0.6);
			// Drive into gear loader
			DriveDistance(0.6, distToLoader);
		} else if (ms.getMode() == Mode.OnOn) { // B
			System.out.println("POS B NOMOBILITY");
			DriveDistance(0.6, distToBaseline - distToHalfmark);
		} else if (ms.getMode() == Mode.OffOff) {
			System.out.println("POS A/B/C NOGEAR NOMOBILITY");
		}
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

	// Beware ye who goes beyond this point

	/** Ball Conveyor Belt */
	@Deprecated
	public void ballConveyorBelt() {
		boolean button = xbox.getRawButton(Config.beltButton);
		if (button && !prevBeltButton) isBelting = !isBelting;
		if (button) prevBeltButton = true;
		else prevBeltButton = false;
		if (isBelting) beltMotor.set(Config.beltSpeed);
		else beltMotor.set(0.0);
	}

	/**
	 * If stop button is clicked, stop robot functions <br>
	 * <b>THE DRIVE STATION HAS THIS! Just Press Space To ESTOP</b> <br>
	 * Note: This will E-Stop the robot regardless of if the Driver Station
	 * window has focus or not (THIS IS AN FRC THING)
	 * 
	 * @see <a href=
	 *      "http://wpilib.screenstepslive.com/s/4485/m/24192/l/144976-frc-driver-station-powered-by-ni-labview">Drive
	 *      Station Info</a>
	 **/
	@Deprecated
	public void alphaStop() {
		if (Config.allowEStop) {
			System.err.println("DONT USE THIS ESTOP");
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
}