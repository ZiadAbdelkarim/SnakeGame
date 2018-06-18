package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	
	public static final int width = 500; 
	public static final int height = 500;

	
	private Graphics g; 
	private BufferedImage image;
	
	//Used for Game loop
	private Thread thread;
	private boolean running;
	private long targetTime;
	
	
	//Game variables
	private Snake head, food;
	private ArrayList<Snake> snakes;
	private int size = 10;
	private int score;
	private int level; 
	private boolean gameOver;
	private boolean start;
	
	//Movement variables
	private int dx; 
	private int dy; 
	private boolean up, right, down, left;
	
	//Constructor	
	public GamePanel() {
		this.setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	
	//Starts thread to begin changing frames
	public void addNotify() {
		super.addNotify();
		thread = new Thread(this);
		thread.start();
	}
	
	//Changes the framerate or rate of sleep on the thread
	private void setFPS(int fps) {
		targetTime = 1000 / fps;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(gameOver) {
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_RIGHT ||
					e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_D) {
				start = true;
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_UP) {
			up = true;
			down = false; 
			right = false;
			left = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			left = true;
			up = false; 
			right = false; 
			down = false; 
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			down = true;
			up = false; 
			right = false; 
			left = false;
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = true;
			left = false; 
			down = false; 
			up = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_W) {
			up = true;	
			down = false; 
			right = false;
			left = false;
		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			left = true;
			up = false; 
			right = false; 
			down = false;		
		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			down = true;
			up = false; 
			right = false; 
			left = false;
		}else if(e.getKeyCode() == KeyEvent.VK_D) {
			right = true;
			left = false; 
			down = false; 
			up = false;
		}
	}

	
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}

	
	@Override
	public void run() {
		if(running) return;
		init();
	
		long startTime; 
		long elapsed; 
		long wait;
		
		//Game loop
		while(running) {
			startTime = System.nanoTime(); 
			
			update();
			requestRender();
			
			elapsed = System.nanoTime() - startTime;
			wait = targetTime - elapsed / 1000000; 
			
			if(wait > 0) {
				try {
					thread.sleep(wait);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void requestRender() {
		render(g);
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0 , 0, null);
		g2.dispose();
		
	}
	
	//Updates the movement and checks for collision of snake
	private void update() {
		if(gameOver) {
			if(start) {
				setupLevel();
			}
			return;
		}
		
		//Checks if direction has changed and if not already moving down or up
		if(up && dy == 0) {
			dy = -size;
			dx = 0;
		}
		
		if(down && dy == 0) {
			dy = size; 
			dx = 0;
		}
		
		if(left && dx == 0) {
			dy = 0;
			dx = -size;
		}
		
		if(right && dx == 0 && dy != 0) {
			dy = 0; 
			dx = size;
		}
		
		
		//Previous snake parts must follow the part before them, this loop sets the location of each snake part to follow the one ahead of it
		if(dx != 0 || dy != 0) {
			for(int i = snakes.size()-1; i > 0; i--) {
				snakes.get(i).setPosition(snakes.get(i-1).getX(), snakes.get(i-1).getY());
			}
			head.move(dx, dy);			
		}		
		
		for(Snake s: snakes) {
			if(s.isColliding(head)) {
				gameOver = true;
				break;
			}
		}
		
		
		if(food.isColliding(head)) {
			score++;
			setFood();
			
			Snake e = new Snake(size);
			e.setPosition(-100, -100);
			snakes.add(e);
			//Every 10 points the amount of time the thread sleeps decreases
			if(score % 10 == 0) {
				level++;
				if(level > 10) level = 10;//Max level is 10
				setFPS(level * 10);
			}
		}
		
		//Running into walls causes the snake to appear on the opposite side
		if(head.getX() < 0) {
			head.setX(width);
		}if(head.getY() < 0) {
			head.setY(height);
		}if(head.getX() > width) {
			head.setX(0);
		}if(head.getY() > height) {
			head.setY(0);
		}
	}
	
	//Rendering image
	public void render(Graphics g) {
		g.clearRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		for(Snake e: snakes) {
			e.draw(g);
		}
		
		g.setColor(Color.RED);
		food.draw(g);
		
		if(gameOver) {
			g.drawString("GameOver! ", 200, 250);
		}
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimeRoman", Font.PLAIN, 20 ));
		g.drawString("Score : " + score + " Level : " + level, 15, 17);
		

		if(dx == 0 && dy == 0) {
			g.drawString("Ready!", 210, 240);
		}
	}
	
	private void init() {
		//Represents an image with 8-bit RGBA color components packed into integer pixels
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//Creates a Graphics2D, which can be used to draw into this BufferedImage.
		g = image.createGraphics();
		running = true;
		setupLevel();
		
	}
	
	//Initialize necessary variables for game to begin
	private void setupLevel() {
		snakes = new ArrayList<Snake>();
		head = new Snake(size);
		head.setPosition(width/2, height/2);
		snakes.add(head);
		
		//Place the first three Snake parts
		for(int i = 1; i < 3; i++) {
			Snake e = new Snake(size);
			e.setPosition(head.getX() + (i * size), head.getY());
			snakes.add(e);
		}
		food = new Snake(size);
		setFood();
		score = 0;

		gameOver = false; 
		start = false;
		level = 1;
		dx = dy = 0;
		setFPS(level * 10);
	}
	
	//Places the food on a random spot in the frame
	public void setFood() {
		
		int x = (int)(Math.random() * 465);
		int y = (int)(Math.random() * 465);		
		
		x -= (x % size);
		y -= (y % size);
		food.setPosition(x, y);
		 
		
	}

}
