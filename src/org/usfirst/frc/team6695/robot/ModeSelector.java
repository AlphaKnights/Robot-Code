package org.usfirst.frc.team6695.robot;

import edu.wpi.first.wpilibj.DigitalInput;

public class ModeSelector {
	/** Input for left switch */
	DigitalInput switch1;
	/** Input for right switch */
	DigitalInput switch2;

	public enum Mode {
		OffOff,
		OffOn,
		OnOff,
		OnOn,
		none;
	}

	public ModeSelector(int left, int right) {
		switch1 = new DigitalInput(right);
		switch2 = new DigitalInput(left);
	}

	public Mode getMode() {
		if (switch1.get() && switch2.get()) return Mode.OffOff;
		if (!switch1.get() && switch2.get()) return Mode.OffOn;
		if (switch1.get() && !switch2.get()) return Mode.OnOff;
		if (!switch1.get() && !switch2.get()) return Mode.OnOn;
		return Mode.none;
	}

}
