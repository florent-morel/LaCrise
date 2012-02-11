package org.lacrise.engine.game;

import java.util.List;

import org.lacrise.engine.Constants;

public class Player implements Comparable<Player> {

	private Integer mId;

	private String mName;

	private PlayerScore mPlayerScore;

	private Turn mCurrentTurn;

	private boolean mHasStarted;

	private boolean mActive;


	public Player(Integer internalId) {
		super();
		this.mId = internalId;
		this.initPlayer();
	}

	public void initPlayer() {
		this.mPlayerScore = new PlayerScore();
		this.mCurrentTurn = null;
		this.mHasStarted = false;
		this.mActive = true;
	}

	public Turn getCurrentTurn() {
		return mCurrentTurn;
	}

	public void setCurrentTurn(Turn currentTurn) {
		this.mCurrentTurn = currentTurn;
	}

	/**
	 * Get last played turn id.
	 *
	 * @return
	 */
	public Integer getLastPlayedTurnId() {
		Integer nbTurns = Constants.ZERO_VALUE;
		List<Turn> turnList = mPlayerScore.getTurnList();
		if (!turnList.isEmpty()) {
			nbTurns = turnList.get(turnList.size() - 1).getId();
		}
		return nbTurns;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public Integer getId() {
		return mId;
	}

	/**
	 * Return current total score.
	 *
	 * @param nullable
	 *            if true and score is null, return null if false, return zero
	 * @return Player current score.
	 *
	 */
	public Integer getTotalScore(boolean nullable) {
		Integer total = mPlayerScore.getTotal();

		if (total == null && !nullable) {
			total = Constants.ZERO_VALUE;
		}

		return total;
	}

	public PlayerScore getPlayerScore() {
		return mPlayerScore;
	}

	public boolean hasStarted() {
		return mHasStarted;
	}

	/**
	 * Set mHasStarted to true. No way to set it to false.
	 */
	public void setHasStarted() {
		this.mHasStarted = true;
	}

	public boolean isActive() {
		return mActive;
	}

	public boolean toggleActive() {
		this.mActive = !this.mActive;
		return this.mActive;
	}

	@Override
	public int compareTo(Player another) {
		int toReturn = 0;

		if (!this.equals(another)) {
			if (this.getTotalScore(true) == null) {
				// Current player has no score yet.
				if (another.getTotalScore(true) != null) {
					toReturn = -1;
				} else {
					// Both have null score, order by id
					toReturn = another.getId().compareTo(this.getId());
				}
			} else {
				if (another.getTotalScore(true) != null) {
					// Both have a non null score, order by score
					toReturn = this.getTotalScore(true).compareTo(
							another.getTotalScore(true));
				} else {
					toReturn = 1;
				}
			}
		}

		return toReturn;
	}

	@Override
	public boolean equals(Object o) {
		boolean toReturn = false;
		Player other = (Player) o;
		if (this.getId().equals(other.getId())) {
			toReturn = true;
		}
		return toReturn;
	}

	public String toString() {
		StringBuilder playerString = new StringBuilder(this.getName());
		playerString.append("(");
		playerString.append(this.getId());
		playerString.append(")");
		playerString.append(" - ");
		playerString.append(this.getPlayerScore().toString());

		return playerString.toString();
	}

}
