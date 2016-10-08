package quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;

public class QuestSystem {
	
	private Map<Quest, List<GUIText>> questTexts = new HashMap<Quest, List<GUIText>>();
	private FontType font;
	
	private List<Quest> quests = new ArrayList<Quest>();
	
	private float x = 0.01f;
	private float y = 0.2f;
	private float yDistance = 0.04f + 0.03f;
	private float questScale = 1.15f;
	private float taskScale = 1f;
	
	private int indicator = 0;
	
	public QuestSystem(FontType font) {
		this.font = font;
	}
	
	public void addQuest(Quest quest) {
		quests.add(quest);
		List<TaskComponent> tasks = quest.getTaskComponents();
		List<GUIText> texts = new ArrayList<GUIText>();
		indicator++;
		texts.add(createQuestText(quest));
		for(int i = 0; i < tasks.size(); i++) {
			indicator++;
			texts.add(createTaskText(tasks.get(i)));
		}
		questTexts.put(quest, texts);
	}
	
	private GUIText createQuestText(Quest quest) {
		GUIText questName = new GUIText(quest.getName(), questScale, font, new Vector2f(x,
				(float) (y + (yDistance * (indicator)))), 0.3f, false);
		questName.setColour(1, 0.8f, 0.8f);
		return questName;
	}
	
	private GUIText createTaskText(TaskComponent task) {
		GUIText text = new GUIText(task.getTaskString(), taskScale, font, new Vector2f(x,
				(float) (y + (yDistance * (indicator)))), 0.2f, false);
		text.setColour(1, 1, 1);
		return text;
	}
	
	public void removeQuest(Quest quest) {
		questTexts.remove(quest);
		quests.remove(quest);
	}
	
	public Quest getQuestAtIndex(int index) {
		return quests.get(index);
	}
	
	public void update() {
		for(Quest quest : quests) {
			quest.updateTaskComponents();
			List<TaskComponent> tasks = quest.getTaskComponents();
			for(int i = 0; i < tasks.size(); i++) {
				if(tasks.get(i).getCompleted()) {
					questTexts.get(quest).get(i + 1).setTextString("Completed");
				}
			}
			if(quest.getCompletedAllTasks() && quest.getRecievedAllRewards()) {
				quests.remove(quest);
			}
		}
	}
	
	public void cleanUp() {
		questTexts.clear();
	}
}
