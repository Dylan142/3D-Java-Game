package items;

import java.util.ArrayList;
import java.util.List;

public class Item {
	
	public int count;
	public ItemIcon icon;
	
	public List<ItemComponent> components = new ArrayList<ItemComponent>();
	
	public Item setIcon(ItemIcon icon) {
		this.icon = icon;
		return this;
	}
	
	public void addComponent(ItemComponent component) {
		components.add(component);
	}
	
	public void removeComponent(Class<?> component) {
		components.remove(getComponent(component));
	}
	
	public ItemComponent getComponent(Class<?> component) {
		for(ItemComponent c : components) {
			if(c.getClass().equals(component)) return c;
		}
		return null;
	}
}
