package guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.Loader;

public class Font {

	private static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890,.!?:; /*%#@-+=[]<>$()^&    ";
	
	public static List<GuiTexture> draw(Loader loader, String msg, Vector2f position, Vector2f scale) {
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		msg = msg.toUpperCase();
		for(int i = 0; i < msg.length(); i++) {
			int ix = chars.indexOf(msg.charAt(i));
			if(ix >= 0) {
				guis.add(new GuiTexture(loader.loadTexture("text/" + String.valueOf(msg.charAt(i))),
						new Vector2f(position.x + i * 1.75f * scale.x, position.y), scale, 0, 0, 0));
			}
		}
		return guis;
	}
}
