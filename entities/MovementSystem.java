package entities;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.Maths;
import engineTester.MainGameLoop;

public class MovementSystem extends EntitySystem {
	
	private List<Entity> entities;
	private Terrain[][] terrains;
	private Player player;
	
	private MovementComponent move;
	private JumpComponent jump;
	
	private CollisionSystem collisionSystem;
	
	private Vector3f recalculation = new Vector3f(0, 0, 0);
	
	private float gravity = -50f;
	
	public MovementSystem(List<Entity> entities, Terrain[][] terrains, Player player, CollisionSystem collisionSystem) {
		this.entities = entities;
		this.terrains = terrains;
		this.player = player;
		move = (MovementComponent) player.getComponent(MovementComponent.class);
		jump = (JumpComponent) player.getComponent(JumpComponent.class);
		this.collisionSystem = collisionSystem;
	}
	
	@Override
	public void update() {
		collisionSystem.fillTree();
		
		checkInputs();
		Iterator<Entity> iterator = entities.iterator();
		while(iterator.hasNext()) {
			Entity entity = iterator.next();
			if(entity.getRemoved()) {
				System.out.println("entity removed");
				iterator.remove();
			}
			RenderComponent render = (RenderComponent) entity.getComponent(RenderComponent.class);
			move(entity, getTerrain(render.getPosition().x, render.getPosition().z), render, (MovementComponent) entity.getComponent(MovementComponent.class),
					(JumpComponent) entity.getComponent(JumpComponent.class));
		}
	}
	
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ||
				Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if(move.getCurrentSpeed() < move.getRunSpeed()) move.setCurrentSpeed(move.getRunSpeed());
			else if(move.getCurrentSpeed() < move.getSprintSpeed()) move.increaseCurrentSpeed(move.getSprintSpeed() * 0.07f);
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) move.setCurrentSpeed(move.getRunSpeed());
		else if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) move.setCurrentSpeed(-move.getRunSpeed() / 2);
		else move.setCurrentSpeed(0);
		if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) move.setCurrentTurnSpeed(move.getTurnSpeed());
		else if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) move.setCurrentTurnSpeed(-move.getTurnSpeed());
		else move.setCurrentTurnSpeed(0);
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) jump.jump();
		smoothRotation(move);
		
		if(jump.getInAir()) jump.setUpwardsSpeed(jump.getUpwardsSpeed() * (DisplayManager.getTime() - jump.getJumpTime()) * 1000000);
	}
	
	private void smoothRotation(MovementComponent move) {
		if(move.getCurrentTurnSpeed() != 0) {
			  player.currentTurnLag = move.getCurrentTurnSpeed() * 0.02f;
			  player.totalTurnLag = Maths.clamp(player.totalTurnLag + player.currentTurnLag, -move.getTurnSpeed(), move.getTurnSpeed());
			move.setLastTurnSpeed(move.increaseCurrentTurnSpeed(player.currentTurnLag));
		}
		else if(player.totalTurnLag != 0){
			move.setCurrentTurnSpeed(player.currentTurnLag + player.totalTurnLag * 0.9f);
			player.totalTurnLag -= player.currentTurnLag * 0.9;
			if(move.getLastTurnSpeed() < 0 && player.totalTurnLag > 0 || move.getLastTurnSpeed() > 0 && player.totalTurnLag < 0) {
				player.totalTurnLag = player.currentTurnLag = 0;
				move.setCurrentTurnSpeed(0);
			}
		}
	}
	
	private void move(Entity entity, Terrain terrain, RenderComponent render, MovementComponent movement, JumpComponent jump) {
		if(movement != null) {
			render.increaseRoatation(0, movement.getCurrentTurnSpeed() * DisplayManager.getDelta(), 0);
			
			float distance = movement.getCurrentSpeed() * DisplayManager.getDelta();
			float dx = (float) (distance * Math.sin(Math.toRadians(render.getRoty())));
			float dz = (float) (distance * Math.cos(Math.toRadians(render.getRoty())));
			
			if(entity.hasComponent(CollisionComponent.class)) {
				CollisionComponent collision = (CollisionComponent) entity.getComponent(CollisionComponent.class);
				recalculation.x = render.getPosition().x + dx;
				recalculation.z = render.getPosition().z + dz;
				collision.boundingBox.recalculate(recalculation);
				if(collisionSystem.collides(entity)) {
					dx = 0;
					dz = 0;
				}
				collision.boundingBox.recalculate();
			}
			
			render.increasePosition(dx, 0, dz);			
		}
		float terrainHeight = terrain.getHeightOfTerrain(render.getPosition().x, render.getPosition().z);
		
		if(jump != null) {
			jump.setUpwardsSpeed(jump.getUpwardsSpeed() + gravity);
			render.increasePosition(0, jump.getUpwardsSpeed() * DisplayManager.getDelta(), 0);
			if(render.getPosition().y < terrainHeight) {
				jump.setUpwardsSpeed(0);
				jump.setInAir(false);
				render.getPosition().y = terrainHeight;
			}
		}
		else {
			render.increasePosition(0, gravity * DisplayManager.getDelta(), 0);
			if(render.getPosition().y < terrainHeight) render.getPosition().y = terrainHeight;
		}
		
	}
	
	public int getRandomSign() {
		int number = Maths.getRandomInt(3, -1);
		if(number != 0) return number;
		for(int i = 0; i < 3; i++) {
			number = Maths.getRandomInt(3, -1);
			if(number != 0) return number;
		}
		return number;
	}
	
	private Terrain getTerrain(float worldX, float worldZ) {
		int x = (int) (worldX / Terrain.SIZE);
		int z = (int) (worldZ / Terrain.SIZE);
		if(x < MainGameLoop.WIDTH && x >= 0 && z < MainGameLoop.WIDTH && z >= 0) return terrains[x][z];
		return terrains[0][0];
	}
}
