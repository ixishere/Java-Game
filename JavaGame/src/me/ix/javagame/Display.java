package me.ix.javagame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import me.ix.javagame.graphics.Screen;
import me.ix.javagame.input.Controller;
import me.ix.javagame.input.InputHandler;

public class Display extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1280, HEIGHT = 720;
	
	public static final String TITLE = "JGame Pre-Alpha 0.01";

	private Thread thread;
	private Screen screen;
	private Game game;
	private BufferedImage img;
	private boolean running = false;
	private int[] pixels;
	private InputHandler input;
	
	private int newX = 0;
	private int oldX = 0;
	
	public int fps = 0;
	
	public Display() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		screen = new Screen(WIDTH, HEIGHT);
		game = new Game();
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		input = new InputHandler();
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
	}
	
	private void start() {
		System.out.println("Game thread starting");
		
		if(running)
			return;

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	private void stop() {
		System.out.println("Game thread stopping");
		
		if(!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void run() {
		int frames = 0;
		double unprocessedSeconds = 0;
		long previousTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		
		while(running) {
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			previousTime = currentTime;
			unprocessedSeconds += passedTime / 1000000000.0;
			
			while(unprocessedSeconds > secondsPerTick) {
				tick();
				unprocessedSeconds -= secondsPerTick;
				tickCount++;
				if(tickCount % 60 == 0) {
					fps = frames;
					previousTime += 1000;
					frames = 0;
				}
			}
			/* Commented this out because I dont get the point considering its done either way.
			if(ticked) {
				render();
				frames++;
			}
			*/
			render();
			frames++;
			
			newX = InputHandler.MouseX;
			if(newX < oldX) {
				Controller.turnLeft = true;
			}
			if(newX > oldX) {
				Controller.turnRight = true;
			}
			if(newX == oldX) {
				Controller.turnLeft = false;
				Controller.turnRight = false;
			}
			oldX = newX;
		}
	}
	
	private void tick() {
		game.tick(input.key);
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		screen.render(game);
		
		for(int i = 0; i < WIDTH * HEIGHT; i++) {
			pixels[i] = screen.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
		
		g.setColor(Color.yellow);
		g.setFont(new Font("Verdana", 2, 25));
		g.drawString(fps + " FPS", 15, 35);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		BufferedImage cursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0,0), "blank");
		
		Display game = new Display();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.getContentPane().setCursor(blank);
		frame.setTitle(TITLE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		
		game.start();
	}
}
