package quests;

public class ExperienceRewardComponent extends RewardComponent {
	
	private float experience;
	
	public ExperienceRewardComponent(float experience, boolean rewarded) {
		this.experience = experience;
		this.rewarded = rewarded;
	}
	
	@Override
	public void reward() {
		rewardExperience();
	}
	
	/**
	 * @return The amount of experience to be rewarded
	 */
	public float rewardExperience() {
		return experience;
	}
}
