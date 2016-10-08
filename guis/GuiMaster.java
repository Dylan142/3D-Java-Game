package guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.Loader;

public class GuiMaster {

	private static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private static Loader loader;
	private static GuiRenderer renderer;
	
	public static void init(Loader loader) {
		GuiMaster.loader = loader;
		GuiMaster.renderer = new GuiRenderer(loader);
	}
	
	public static void createGui(String image, Vector2f position, Vector2f scale, float rX, float rY, float rZ) {
		GuiTexture gui = new GuiTexture(loader.loadTexture(image), position, scale, rX, rY, rZ);
		guis.add(gui);
	}
	
	public static void removeGui(GuiTexture gui) {
		guis.remove(gui);
	}
	
	public static void render() {
		renderer.render(guis);
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
		guis.clear();
	}
}
