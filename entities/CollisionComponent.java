package entities;

import org.lwjgl.util.vector.Vector3f;

import toolbox.BoundingBox;

public class CollisionComponent extends EntityComponent {
	
	public BoundingBox boundingBox;
	
	public CollisionComponent(Vector3f position, Vector3f size) {
		boundingBox = new BoundingBox(position, size);
	}
	
	public CollisionComponent(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public void recalculate() {
		boundingBox.recalculate();
	}
}
