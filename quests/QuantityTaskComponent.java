package quests;

public class QuantityTaskComponent extends TaskComponent {
	
	private float current;
	private float required;
	
	public QuantityTaskComponent(float current, float required, String taskString) {
		super(taskString);
		this.current = current;
		this.required = required;
		super.setCompleted(current >= required ? true : false);
	}
	
	public void updateCurrent(float number) {
		current += number;
		if(current >= required) super.setCompleted(true);
	}
}
