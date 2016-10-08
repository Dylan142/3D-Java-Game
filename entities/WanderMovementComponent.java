package entities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import terrains.Terrain;
import toolbox.BoundingBox;

public class WanderMovementComponent extends EntityComponent {
	
	private Random random = new Random();
	
	public BoundingBox destination;
	public float radius;
	
	public WanderMovementComponent(float radius) {
		this.radius = radius;
	}
	
	public BoundingBox createDestinationAndBoundingBox(Terrain terrain) {
		float x = (random.nextFloat() -0.5f) * 2 * radius;
		float z = (random.nextFloat() -0.5f) * 2 * radius;
		float y = terrain.getHeightOfTerrain(x, z);
		destination = new BoundingBox(new Vector3f(x, y, z), new Vector3f(5, 5, 5));
		return destination;
	}
}
