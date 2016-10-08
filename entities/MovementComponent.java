package entities;



public class MovementComponent extends EntityComponent {

	private float runSpeed;
	private float turnSpeed;
	private float sprintSpeed;
	
	private float currentTurnSpeed = 0;
	private float lastTurnSpeed = 0;
	private float currentSpeed = 0;
	private float lastSpeed = 0;
	
	public MovementComponent(float runSpeed, float sprintSpeed, float turnSpeed) {
		this.runSpeed = runSpeed;
		this.sprintSpeed = sprintSpeed;
		this.turnSpeed = turnSpeed;
	}
	
	public float getSprintSpeed() {
		return sprintSpeed;
	}
	
	public void setSprintSpeed(float sprintSpeed) {
		this.sprintSpeed = sprintSpeed;
	}
	
	public float getRunSpeed() {
		return runSpeed;
	}

	public void setRunSpeed(float runSpeed) {
		this.runSpeed = runSpeed;
	}

	public float getTurnSpeed() {
		return turnSpeed;
	}

	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}

	public float getCurrentTurnSpeed() {
		return currentTurnSpeed;
	}

	public void setCurrentTurnSpeed(float currentTurnSpeed) {
		this.currentTurnSpeed = currentTurnSpeed;
	}
	
	public float increaseCurrentTurnSpeed(float increase) {
		this.currentTurnSpeed += increase;
		return this.currentTurnSpeed;
	}
	
	public float getLastTurnSpeed() {
		return lastTurnSpeed;
	}

	public void setLastTurnSpeed(float lastTurnSpeed) {
		this.lastTurnSpeed = lastTurnSpeed;
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	
	public void increaseCurrentSpeed(float increase) {
		this.currentSpeed += increase;
	}

	public float getLastSpeed() {
		return lastSpeed;
	}

	public void setLastSpeed(float lastSpeed) {
		this.lastSpeed = lastSpeed;
	}
	
	public boolean isMoving() {
		return currentSpeed != 0;
	}
	
	public boolean isTurning() {
		return currentTurnSpeed != 0;
	}
}
