package quests;

import java.util.ArrayList;
import java.util.List;

public class Quest {

	private List<TaskComponent> tasks = new ArrayList<TaskComponent>();
	private List<RewardComponent> rewards = new ArrayList<RewardComponent>();
	
	private boolean completedAllTasks = false;
	private boolean recievedAllRewards = false;
	
	private String questName;
	
	public Quest(String questName) {
		this.questName = questName;
	}
	
	public void addTaskComponent(TaskComponent task) {
		tasks.add(task);
	}
	
	public void removeTaskComponent(TaskComponent task) {
		tasks.remove(task);
	}
	
	public List<TaskComponent> getTaskComponents() {
		return tasks;
	}
	
	/**
	 * Updates all task components and checks for completed tasks and deletes them.
	 * This should be called every frame.
	 */
	public void updateTaskComponents() {
		if(!completedAllTasks) {
			int numberOfCompletedTasks = 0;
			for(TaskComponent task : tasks) {
				task.update();
				if(task.getCompleted()) numberOfCompletedTasks++;
			}
			if(numberOfCompletedTasks == tasks.size()) {
				completedAllTasks = true;
			}			
		}
	}
	
	public void addRewardComponent(RewardComponent reward) {
		rewards.add(reward);
	}
	
	public void removeRewardComponent(RewardComponent reward) {
		rewards.add(reward);
	}
	
	public List<RewardComponent> getRewardComponents() {
		return rewards;
	}
	
	public boolean getCompletedAllTasks() {
		return completedAllTasks;
	}

	public boolean getRecievedAllRewards() {
		return recievedAllRewards;
	}
	
	public String getName() {
		return questName;
	}
}
