package me.ix.javagame.graphics;

import me.ix.javagame.Game;

public class Render3D extends Render {

	public double[] zBuffer;
	private double renderDistance = 4000;
	
	public Render3D(int width, int height) {
		super(width, height);
		zBuffer = new double[width * height];
	}

	public void floor(Game game) {
		double floorPosition = 8;
		double ceilingPosition = 55;
		
		double forward = game.controls.z; // FORWARD AND BACK
		double strafe = game.controls.x; // STRAFE LEFT RIGHT
		double up = game.controls.y; // JUMP
		double rotation = game.controls.rotation; // TURN ROTATION
		
		double cosine = Math.cos(rotation);
		double sine = Math.sin(rotation);
		
		for(int y = 0; y < height; y++) {
			double ceiling = (y - height / 2.0) / height;
			
			double z  = (floorPosition + up) / ceiling;

			if(ceiling < 0) {
				z = (ceilingPosition - up) / -ceiling;
			}
			
			for(int x = 0; x < width; x++) {
				double depth = (x - width / 2.0) / height;
				depth *= z;
				double xx = depth * cosine + z * sine + strafe;
				double yy = z * cosine - depth * sine + forward;
				int xPixels = (int) (xx + strafe);
				int yPixels = (int) (yy + forward);
				zBuffer[x + y * width] = z;
				pixels[x + y * width] = Texture.floor.pixels[(xPixels & 7) + (yPixels & 7) * 8];
			}
		}
	}
	
	public void renderDistanceLimiter() {
		for(int i = 0; i < width * height; i++) {
			
			int colour = pixels[i];
			int brightness = (int) (renderDistance / (zBuffer[i]));
			
			if(brightness < 0) {
				brightness = 0;
			}
			if(brightness > 255) {
				brightness = 255;
			}
			
			int r = (colour >> 16) & 0xff;
			int g = (colour >> 8) & 0xff;
			int b = (colour) & 0xff;
			
			r = r * brightness >>> 8;
			g = g * brightness >>> 8;
			b = b * brightness >>> 8;
			
			pixels[i] = r << 16 | g << 8 | b;
		}
	}
}
