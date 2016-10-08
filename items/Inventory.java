package items;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class Inventory {

	List<Item> items = new ArrayList<Item>();
	
	private int width = 3;
	private int height = 4;
	private int size = width * height;
	private int maxItems = size;
	
	private Vector2f selected = new Vector2f(0, 0);
	
	public void use() {
		if(Keyboard.getEventKey() == Keyboard.KEY_UP) selected.y++;
		if(Keyboard.getEventKey() == Keyboard.KEY_DOWN) selected.y--;
		if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT) selected.x++;
		if(Keyboard.getEventKey() == Keyboard.KEY_LEFT) selected.x--;
	}
	
	/**
	 * If the inventory already has the item, the item's count is increased
	 */
	public boolean addItem(Item item, int count) {
		if(items.size() == maxItems) return false;
		if(items.contains(item)) {
			int index = items.indexOf(item);
			items.get(index).count += count;
			return true;
		}
		items.add(item);
		return true;
	}
	
	/**
	 * If removeCount is greater than amount of items, this method removes the item from the inventory!
	 * Could be helpful to return the amount removed
	 */
	public void removeItem(Item item, int removeCount) {
		if(items.contains(item)) {
			int index = items.indexOf(item);
			Item i = items.get(index);
			int hasCount = i.count;
			if(hasCount <= removeCount) items.remove(i);
			else i.count -= removeCount;
		}
	}
	
	public void useItem() {
		
	}
}
