package org.usfirst.frc.team6695.robot;
/**
 * Human Readable Version of xbox keys
 * @author Pasha
 * @see XboxPOVID 
 */
public enum XboxButtonID {
	A(0),
	B(1),
	X(3),
	Y(4),
	LB(5),
	RB(6),
	Back(7),
	Start(8),
	LS(9),
	RS(10);
	
	private final int val;
	
	XboxButtonID(final int i) {
		val = i;
	}
	
	public int value() { return val; }
}