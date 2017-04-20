package org.usfirst.frc.team6695.robot;

public class CustomTuple {
	private int milliseconds;
	private double leftMotor;
	private double rightMotor;
	
	public CustomTuple(int i, double dl, double dr) {
		this.milliseconds = i;
		this.leftMotor = dl;
		this.rightMotor = dr;
	}
	
	public Object get(int i) {
		switch (i) {
		case 0: return this.milliseconds;
		case 1: return this.leftMotor;
		case 2: return this.rightMotor;
		}
		System.out.println("good job you broke your own class idiot");
		return null;
	}
}