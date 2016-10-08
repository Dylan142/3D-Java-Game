package quests;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class MultipleCommandsTaskComponent extends TaskComponent {

	private List<Integer> buttonsToPress;
	
	public MultipleCommandsTaskComponent(List<Integer> buttonsToPress, String taskString) {
		super(taskString);
		this.buttonsToPress = buttonsToPress;
	}
	
	@Override
	public void update() {
		if(!buttonsToPress.isEmpty()) {
			Iterator<Integer> iterator = buttonsToPress.iterator();
			while(iterator.hasNext()) {
				if(Keyboard.isKeyDown(iterator.next())) {
					iterator.remove();
				}
			}
		}
		else {
			super.setCompleted(true);
		}
	}
}
