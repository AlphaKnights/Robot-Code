package org.usfirst.frc.team6695.robot;
/**
 * Human Readable xbox pov values.
 * @author Alpha Knights
 * @see XboxButtonID
 */
public enum XboxPOVID {
	UP(0),
	UPRIGHT(45),
	RIGHT(90),
	DOWNRIGHT(135),
	DOWN(180),
	DOWNLEFT(225),
	LEFT(270),
	UPLEFT(315),
	CENTER(-1);
	
	private final int val;
	
	XboxPOVID(final int i) {
		val = i;
	}
	
	public int value() { return val; }
}
