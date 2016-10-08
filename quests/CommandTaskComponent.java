package quests;

import org.lwjgl.input.Keyboard;

public class CommandTaskComponent extends TaskComponent {
	
	private int buttonToPress;
	
	public CommandTaskComponent(int buttonToPress, String taskString) {
		super(taskString);
		this.buttonToPress = buttonToPress;
	}
	
	public int getButtonToPress() {
		return buttonToPress;
	}
	
	@Override
	public void update() {
		if(Keyboard.isKeyDown(buttonToPress)) {
			super.setCompleted(true);
		}
	}
}
