package core;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import core.Player.Direction;

public class Tron {

	public static final String TITLE = "Tron";
	public static final int TARGET_FPS = 700;

	private volatile boolean running;
	private volatile boolean won;
	private volatile boolean paused;

	private long lastFpsTime;

	private int fps;

	private Player playerOne;
	private Player playerTwo;

	public Tron() {
		try {
			Display.setDisplayMode(new DisplayMode((int) (Display.getDesktopDisplayMode()
					.getWidth() * 0.75), (int) (Display.getDesktopDisplayMode().getHeight() * 0.75)));
			Display.setTitle(TITLE);
			Display.setResizable(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		this.playerOne = new Player(1.0f, 0.0f, 0.0f, (int) (Display.getWidth() / 6),
				(int) (Display.getHeight() / 3), Direction.RIGHT);
		this.playerTwo = new Player(0.0f, 0.0f, 1.0f, (int) 5 * (Display.getWidth() / 6),
				(int) (Display.getHeight() / 3), Direction.LEFT);

		resize(Display.getWidth(), Display.getHeight());

		mainGameLoop();

	}

	public void mainGameLoop() {
		long lastLoopTime = System.nanoTime();
		final long OPTIMAL_TIME = 1000000000L / TARGET_FPS;

		this.running = true;
		while (this.running && !Display.isCloseRequested()) {
			long currentTime = System.nanoTime();
			long updateLength = currentTime - lastLoopTime;
			lastLoopTime = currentTime;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			this.lastFpsTime += updateLength;
			this.fps++;

			if (this.lastFpsTime >= 1000000000L) {
				Display.setTitle(TITLE + " (FPS: " + fps + ")");
				this.lastFpsTime = 0;
				fps = 0;
			}

			update(delta);
			render();

			if (Display.wasResized()) {
				resize(Display.getWidth(), Display.getHeight());
			}

			Display.update();
			Display.sync(TARGET_FPS);
		}

		Display.destroy();
	}

	private void update(double delta) {
		if (!this.won && !this.paused) {
			while (Keyboard.next()) {
				int keyCode = Keyboard.getEventKey();

				if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_END) {
					this.running = false;
				} else if (keyCode == Keyboard.KEY_P || keyCode == Keyboard.KEY_PAUSE) {
					this.paused = !this.paused;
				}

				if (Keyboard.getEventKeyState()) {
					if (keyCode == Keyboard.KEY_A) {
						this.playerOne.setDirection(Direction.LEFT);
					}
					if (keyCode == Keyboard.KEY_D) {
						this.playerOne.setDirection(Direction.RIGHT);
					}
					if (keyCode == Keyboard.KEY_W) {
						this.playerOne.setDirection(Direction.UP);
					}
					if (keyCode == Keyboard.KEY_S) {
						this.playerOne.setDirection(Direction.DOWN);
					}

					if (keyCode == Keyboard.KEY_LEFT) {
						this.playerTwo.setDirection(Direction.LEFT);
					}
					if (keyCode == Keyboard.KEY_RIGHT) {
						this.playerTwo.setDirection(Direction.RIGHT);
					}
					if (keyCode == Keyboard.KEY_UP) {
						this.playerTwo.setDirection(Direction.UP);
					}
					if (keyCode == Keyboard.KEY_DOWN) {
						this.playerTwo.setDirection(Direction.DOWN);
					}
				}
			}

			this.playerOne.update(delta);
			this.playerTwo.update(delta);

			if (Player.intersects(this.playerOne, this.playerTwo)) {
				this.won = true;
			} else if (Player.intersects(this.playerTwo, this.playerOne)) {
				this.won = true;
			}

			if (playerOne.isOut()) {
				this.won = true;
			} else if (playerTwo.isOut()) {
				this.won = true;
			}
		} else if (this.won) {
			while (Keyboard.next()) {
				int keyCode = Keyboard.getEventKey();

				if (Keyboard.getEventKeyState() && keyCode == Keyboard.KEY_SPACE) {
					this.playerOne = new Player(1.0f, 0.0f, 0.0f, (int) (Display.getWidth() / 6),
							(int) (Display.getHeight() / 3), Direction.RIGHT);
					this.playerTwo = new Player(0.0f, 0.0f, 1.0f, (int) 5
							* (Display.getWidth() / 6), (int) (Display.getHeight() / 3),
							Direction.LEFT);
					this.won = !this.won;
				}
			}
		} else if (this.paused) {
			while (Keyboard.next()) {
				int keyCode = Keyboard.getEventKey();

				if (!Keyboard.getEventKeyState() && keyCode == Keyboard.KEY_P) {
					this.paused = !this.paused;
				}
			}
		}
	}

	private void render() {
		if (!this.won && !this.paused) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();

			this.playerOne.render();
			this.playerTwo.render();
		}
	}

	public void resize(int width, int height) {
		GL11.glViewport(0, 0, width, height);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0f, Display.getWidth(), 0f, Display.getHeight(), 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glPointSize((float) Display.getWidth() / (float) Display.getHeight() * 100);
	}

	public static void main(String[] args) {
		new Tron();
	}
}
