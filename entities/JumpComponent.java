package entities;

import renderEngine.DisplayManager;

public class JumpComponent extends EntityComponent {

	private float jumpPower;
	private float jumpTime;
	
	private float upwardsSpeed = 0;
	
	private boolean inAir = false;
	
	public JumpComponent(float jumpPower) {
		this.jumpPower = jumpPower;
	}
	
	public void jump() {
		if(!inAir) {
			this.upwardsSpeed = jumpPower;
			inAir = true;
			this.jumpTime = (float) DisplayManager.getTime();
		}
	}
	
	public float getJumpPower() {
		return jumpPower;
	}
	
	public void setJumpPower(float jumpPower) {
		this.jumpPower = jumpPower;
	}
	
	public float getJumpTime() {
		return jumpTime;
	}
	
	public void setJumpTime(float jumpTime) {
		this.jumpTime = jumpTime;
	}
	
	public float getUpwardsSpeed() {
		return upwardsSpeed;
	}
	
	public void setUpwardsSpeed(float upwardsSpeed) {
		this.upwardsSpeed = upwardsSpeed;
	}
	
	public boolean getInAir() {
		return inAir;
	}
	
	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}
}
