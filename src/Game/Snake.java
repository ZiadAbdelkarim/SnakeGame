package Game;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Snake {
	private int x; 
	private int y; 
	private int size;
	
	//constructor
	public Snake(int size) {
		this.size = size;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setPosition(int x, int y) {
		this.x = x; 
		this.y = y; 
	}
	
	public void move(int dx, int dy) {
		x += dx; 
		y += dy;
		
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, size, size);
	}
	
	//Returns true if colliding wth another snake objects
	public boolean isColliding(Snake s) {
		if(s == this) {
			return false;
		}else {
			return this.getBounds().intersects(s.getBounds());
		}
	}
	
	public void draw(Graphics g) {
		g.fillRect(x, y, size, size);
	}
}
