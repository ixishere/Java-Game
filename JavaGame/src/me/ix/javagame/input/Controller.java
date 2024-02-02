package me.ix.javagame.input;

public class Controller {

	public double x, y, z, rotation, xa, za, rotationa;
	
	public static boolean turnLeft = false;
	public static boolean turnRight = false;
	
	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean crouch, boolean sprint) {
		
		double walkSpeed = 0.3;
		double sprintSpeed = 0.6;
		double movementSpeed = 0.3;
		double rotationSpeed = 0.05;
		
		double xMove = 0;
		double zMove = 0;
		
		double jumpHeight = 1;
		double crouchHeight = -0.4;
		
		if(forward) {
			zMove++;
		}
		
		if(back) {
			zMove--;
		}
		
		if(left) {
			xMove--;
		}
		
		if(right) {
			xMove++;
		}
		
		if(turnLeft) {
			rotationa -= rotationSpeed;
		}
		
		if(turnRight) {
			rotationa += rotationSpeed;
		}
		
		if(jump) {
			y += jumpHeight;
		}
		
		if(crouch) {
			y += crouchHeight;
		}
		
		if(sprint) {
			movementSpeed = sprintSpeed;
		}else {
			movementSpeed = walkSpeed;
		}
		
		xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation)) * movementSpeed;
		za += (zMove * Math.cos(rotation) - xMove * Math.sin(rotation)) * movementSpeed;
		
		x += xa;
		y *= 0.9;
		z += za;
		xa *= 0.1;
		za *= 0.1;
		rotation += rotationa;
		rotationa *= 0.5;
	}
	
}
