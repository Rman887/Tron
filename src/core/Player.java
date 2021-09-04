package core;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class Player {
	public static enum Direction {
		STATIONARY(0, "STATIONARY"), UP(1, "UP"), DOWN(-1, "DOWN"), LEFT(2, "LEFT"), RIGHT(-2, "RIGHT");
		
		private int id;
		private String name;
		
		private Direction(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public boolean equals(Direction otherDirection) {
			if (this.getId() == otherDirection.getId()) {
				return this.name.equals(otherDirection.toString());
			}
			return false;
		}
		
		public int getId() {
			return this.id;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	public static boolean intersects(Player p1, Player p2) {
		List<Point> points = p1.getPoints();
		for (Point p : p2.getPoints()) {
			for (int i = 0; i < points.size() - 1; i++) {
				if ((points.get(i).getY() == points.get(i + 1).getY() && p.getY() == points.get(i).getY()) && ((points.get(i).getX() > p.getX() && p.getX() > points.get(i + 1).getX()) || (points.get(i).getX() < p.getX() && p.getX() < points.get(i + 1).getX()))) {
					return true;
				} else if ((points.get(i).getX() == points.get(i + 1).getX() && p.getX() == points.get(i).getX()) && ((points.get(i).getY() > p.getY() && p.getY() > points.get(i + 1).getY()) || (points.get(i).getY() < p.getY() && p.getY() < points.get(i + 1).getY()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public final int STEP_SIZE = 1;
	
	private List<Point> points;
	
	private Direction currentDirection;
	
	private float[] color;

	private boolean out;
	
	public Player(float r, float g, float b, int x, int y, Direction startingDirection) {
		this.points = new LinkedList<Point>();
		this.points.add(new Point(x, y));
		this.points.add(new Point(x, y));
		
		this.color = new float[] {r, g, b};
		
		this.currentDirection = startingDirection;
	}
	
	public Direction getDirection() {
		return this.currentDirection;
	}
	
	public boolean isOut() {
		return this.out;
	}
	
	public void setOut(boolean out) {
		this.out = out;
	}
	
	public List<Point> getPoints() {
		return this.points;
	}
	
	public void update(double delta) {
		Point lastPoint = this.points.get(this.points.size() - 1);
		
		if (this.currentDirection == Direction.UP) {
			this.points.set(this.points.size() - 1, new Point(lastPoint.getX(), lastPoint.getY() + STEP_SIZE));
		} else if (this.currentDirection == Direction.DOWN) {
			this.points.set(this.points.size() - 1, new Point(lastPoint.getX(), lastPoint.getY() - STEP_SIZE));
		} else if (this.currentDirection == Direction.RIGHT) {
			this.points.set(this.points.size() - 1, new Point(lastPoint.getX() + STEP_SIZE, lastPoint.getY()));
		} else if (this.currentDirection == Direction.LEFT) {
			this.points.set(this.points.size() - 1, new Point(lastPoint.getX() - STEP_SIZE, lastPoint.getY()));
		}
		
		/* Checks for wall collision
		if (lastPoint.getX() > Display.getWidth()) {
			this.points.set(this.points.size() - 1, new Point(Display.getWidth() - 1, lastPoint.getY()));
		} else if (lastPoint.getX() < 0) {
			this.points.set(this.points.size() - 1, new Point(1, lastPoint.getY()));
		}
		
		if (lastPoint.getY() > Display.getHeight()) {
			this.points.set(this.points.size() - 1, new Point(lastPoint.getX(), Display.getHeight() - 1));
		} else if (lastPoint.getY() < 0) {
			this.points.set(this.points.size() - 1, new Point(lastPoint.getX(), 1));
		}*/
		
		if ((lastPoint.getX() > Display.getWidth()) || (lastPoint.getX() < 0) || (lastPoint.getY() > Display.getHeight()) || (lastPoint.getY() < 0)) {
			this.out = true;
		}
		
		if (this.points.size() > 3) {
			for (int i = 0; i < this.points.size() - 1; i++) {
				if (this.currentDirection == Direction.UP || this.currentDirection == Direction.DOWN) {
					if ((this.points.get(i).getY() == this.points.get(i + 1).getY() && lastPoint.getY() == this.points.get(i).getY()) && ((this.points.get(i).getX() > lastPoint.getX() && lastPoint.getX() > this.points.get(i + 1).getX()) || (this.points.get(i).getX() < lastPoint.getX() && lastPoint.getX() < this.points.get(i + 1).getX()))) {
						this.out = true;
					}
				} else if (this.currentDirection == Direction.LEFT || this.currentDirection == Direction.RIGHT) {
					if ((this.points.get(i).getX() == this.points.get(i + 1).getX() && lastPoint.getX() == this.points.get(i).getX()) && ((this.points.get(i).getY() > lastPoint.getY() && lastPoint.getY() > this.points.get(i + 1).getY()) || (this.points.get(i).getY() < lastPoint.getY() && lastPoint.getY() < this.points.get(i + 1).getY()))) {
						this.out = true;
					}
				}
			}
		}
	}
	
	public synchronized void setDirection(Direction direction) {
		if (!(this.currentDirection.equals(direction) || this.currentDirection.getId() == -direction.getId())) {
			this.currentDirection = direction;
			this.points.add(this.points.get(this.points.size() - 1));
		}
	}

	public void render() {
		GL11.glColor3f(this.color[0], this.color[1], this.color[2]);
		GL11.glLineWidth(3.0f);
		for (int i = 0; i < this.points.size() - 1; i++) {
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2i(this.points.get(i).getX(), this.points.get(i).getY());
			GL11.glVertex2i(this.points.get(i + 1).getX(), this.points.get(i + 1).getY());
			GL11.glEnd();
		}
		
		/*
		for (Point p : this.points) {
			System.out.print("(" + p.getX() + " " + p.getY() + "), ");
		}
		System.out.println();*/
	}
}
