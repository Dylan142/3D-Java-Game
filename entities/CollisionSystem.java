package entities;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import engineTester.MainGameLoop;
import terrains.Terrain;
import toolbox.Quadtree;


public class CollisionSystem extends EntitySystem {
	
	private List<Entity> entities;
	private Quadtree quad = new Quadtree(0, new Rectangle(0, 0, (int) (MainGameLoop.WIDTH * Terrain.SIZE), (int) (MainGameLoop.WIDTH * Terrain.SIZE)));
	
	public CollisionSystem(List<Entity> entities) {
		this.entities = entities;
	}
	
	public void fillTree() {
		quad.clear();
		for(Entity e : entities) {
			if(e.hasComponent(CollisionComponent.class)) {
				if(e.hasComponent(MovementComponent.class)) {
					MovementComponent move = (MovementComponent) e.getComponent(MovementComponent.class);
					if(move.isMoving() || move.isTurning()) {
						((CollisionComponent) e.getComponent(CollisionComponent.class)).recalculate();
					}
				}
				quad.insert(e);
			}
		}
	}
	
	public boolean collides(Entity entity) {
		CollisionComponent collision = (CollisionComponent) entity.getComponent(CollisionComponent.class);
		List<Entity> collidableEntities = new ArrayList<Entity>();
		quad.retrieve(collidableEntities, entity);
		
		for(Entity e : collidableEntities) {
			if(e != entity) {
				CollisionComponent c = (CollisionComponent) e.getComponent(CollisionComponent.class);
				if(collision.boundingBox.collidesWith(c.boundingBox)) {
					System.out.println(e);
					return true;
				}
			}
		}
		
		return false;
	}
}
