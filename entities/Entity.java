package entities;

import java.util.ArrayList;
import java.util.List;


public class Entity {
	
	List<EntityComponent> components = new ArrayList<EntityComponent>();
	
	private boolean removed = false;
	
	public boolean getRemoved() {
		return removed;
	}
	
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	public boolean hasComponent(Class<?> component) {
		for(EntityComponent entityComponent : components) {
			if(entityComponent.getClass().equals(component)) {
				return true;
			}
		}
		return false;
	}
	
	public EntityComponent getComponent(Class<?> component) {
		for(EntityComponent entityComponent : components) {
			if(entityComponent.getClass().equals(component)) {
				return entityComponent;
			}
		}
		return null;
	}
	
	public void addComponent(EntityComponent component) {
		components.add(component);
	}
	
	public void removeComponent(Class<?> component) {
		components.remove(getComponent(component));
	}
	
	public List<EntityComponent> getComponents() {
		return components;
	}
}
