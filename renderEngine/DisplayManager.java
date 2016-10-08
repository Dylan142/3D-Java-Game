package renderEngine;

import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int WIDTH = 1080;//1280
	private static final int HEIGHT = 600;//720
	private static final String TITLE = "Pokemon";
	private static final int FPS_CAP = 120;
	
	private static float delta;
	private static long lastFrameTime;
	
	private static float maxTime = 24000;
	private static float dawnTime = maxTime / 8;
	private static float dayTime = dawnTime * 4;
	private static float duskTime = dayTime + dawnTime;
	private static float nightTime = duskTime * 4;
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3, 3, ContextAttribs.CONTEXT_CORE_PROFILE_BIT_ARB).withForwardCompatible(true).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle(TITLE);
			Display.create(new PixelFormat().withSamples(8), attribs);//0, 8, 0, 4
			Display.setVSyncEnabled(true);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		}catch(Exception e) {
			System.err.println("Error with Display Creation in DisplayManager!");
			Display.destroy();
		}
		lastFrameTime = getTime();
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		System.out.println("OpenGL version " + GL11.glGetString(GL11.GL_VERSION));
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static float getDelta() {
		return delta;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	public static float getDuskTime() {
		return duskTime;
	}

	public static float getMaxTime() {
		return maxTime;
	}

	public static float getDawnTime() {
		return dawnTime;
	}

	public static float getDayTime() {
		return dayTime;
	}

	public static float getNightTime() {
		return nightTime;
	}

	public static void closeDisplay() {
		Display.destroy();
	}
}
