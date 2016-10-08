package entities;



public class EntitySystem {
	
	private MovementSystem move;
	
	public void init(MovementSystem move) {
		this.move = move;
	}
	
	public void update() {
		move.update();
	}
	
	public EntitySystem() {
		
	}
}
