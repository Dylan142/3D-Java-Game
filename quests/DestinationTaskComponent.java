package quests;

import org.lwjgl.util.vector.Vector3f;

import toolbox.BoundingBox;
import entities.RenderComponent;

public class DestinationTaskComponent extends TaskComponent {

	private BoundingBox destination;
	private RenderComponent position;
	
	/**
	 * 
	 * @param destination - the center of the destination bounding box
	 * @param sizeOfDestination - half is added to each side of destination to create a bounding box
	 */
	public DestinationTaskComponent(RenderComponent position, Vector3f destination, Vector3f sizeOfDestination, String taskString) {
		super(taskString);
		this.position = position;
		this.destination = new BoundingBox(destination, sizeOfDestination);
	}
	
	/**
	 * Checks if player is within destination bounds
	 */
	@Override
	public void update() {
		if(destination.contains(position.getPosition())) super.setCompleted(true);
	}

}
