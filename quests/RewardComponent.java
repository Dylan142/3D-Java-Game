package quests;

public class RewardComponent {
	
	protected boolean rewarded;
	
	/**
	 * @return True if the reward has been claimed
	 */
	public boolean getRewarded() {
		return rewarded;
	}
	
	/**
	 * Called to receive reward. Sets rewarded to true.
	 */
	public void reward() {
		rewarded = true;
	}
}
