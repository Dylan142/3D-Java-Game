package guis;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.DisplayManager;

public class GuiTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	private float rotX, rotY, rotZ;
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		this(texture, position, scale, 0, 0, 0);
	}
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale, float rotX, float rotY, float rotZ) {
		super();
		this.texture = texture;
		this.position = position;
		this.scale = scale;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
	
	public void increaseRotation(float rotX, float rotY, float rotZ) {
		this.rotX += rotX;
		this.rotY += rotY;
		this.rotZ += rotZ;
	}
	
	public void increasePosition(float dx, float dy) {
		position.x += dx * DisplayManager.getDelta();
		position.y += dy * DisplayManager.getDelta();
	}
	
	public void setScale(float x, float y) {
		scale.x = x;
		scale.y = y;
	}
	
	public void setRotation(float rotX, float rotY, float rotZ) {
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}	

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

	public float getRotX() {
		return rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public float getRotZ() {
		return rotZ;
	}
}
