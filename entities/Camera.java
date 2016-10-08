package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;
import engineTester.MainGameLoop;

public class Camera {
	
	private float distanceFromPlayer = 35;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 8, -4);
	private float pitch = 10;
	private float noHeightPitch = pitch;
	private float yaw = 0;
	private float roll;
	
	private RenderComponent render;
	private MovementComponent move;
	
	private static final float MAXIMUM_CAMERA_DISTANCE = 150f;
	private static final float MINIMUM_PITCH = -60f;
	private static final float MAXIMUM_PITCH = 90f;
	
	public Camera(RenderComponent render, MovementComponent move) {
		this.render = render;
		this.move = move;
	}
	
	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (render.getRoty() + angleAroundPlayer);
	}
	
	public void invertPitch() {
		this.pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = render.getRoty() + angleAroundPlayer;
		position.z = render.getPosition().z - (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = render.getPosition().x - (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float yPos = render.getPosition().y + 10 + verticalDistance;
		float terrainHeight = MainGameLoop.getTerrainHeight(position.x, position.z);
		if(terrainHeight > yPos) {
			float yDelta = terrainHeight - yPos;
			pitch = pitch + yDelta * 0.01f;
			yPos = terrainHeight + 1;
		} else if(pitch >= noHeightPitch) {
			pitch = Maths.clamp(pitch - noHeightPitch * 0.03f, noHeightPitch, pitch);
		}
		position.y = yPos;
	}
	
	private float calculateHorizontalDistance() {
		float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		float maximumHorizontalDistance = (float) (MAXIMUM_CAMERA_DISTANCE * Math.cos(Math.toRadians(pitch)));
		return horizontalDistance < maximumHorizontalDistance ? horizontalDistance : maximumHorizontalDistance;
	}
	
	private float calculateVerticalDistance() {
		float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		float maximumVerticalDistance = (float) (MAXIMUM_CAMERA_DISTANCE * Math.sin(Math.toRadians(pitch)));
		return verticalDistance < maximumVerticalDistance ? verticalDistance : maximumVerticalDistance;
	}
	
	private void calculateZoom() {
		distanceFromPlayer -= Mouse.getDWheel() * 0.002f;
	}
	
	private void calculatePitch() {
		if(Mouse.isButtonDown(1)) {
			pitch = Maths.clamp(pitch - Mouse.getDY() * 0.1f, MINIMUM_PITCH, MAXIMUM_PITCH);
			noHeightPitch = pitch;
		}
	}
	
	private void calculateAngleAroundPlayer() {
		if(!move.isMoving() && Mouse.isButtonDown(0)) angleAroundPlayer -= Mouse.getDX() * 0.3f;
		else if(move.isMoving() && (int) angleAroundPlayer % 360 != 0) angleAroundPlayer -= angleAroundPlayer % 360 * 0.1f;
	}
}
