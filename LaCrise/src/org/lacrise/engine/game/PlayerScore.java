package org.lacrise.engine.game;

import java.util.ArrayList;
import java.util.List;

import org.lacrise.engine.Constants;

public class PlayerScore {

	/**
	 * Score is null until player starts.
	 */
	private Integer mTotal;

	private boolean mHasZero;

	private List<Turn> mTurnList;

	private List<Penalty> mPenaltyList = new ArrayList<Penalty>();

	public PlayerScore() {
		mTurnList = new ArrayList<Turn>();
		mPenaltyList = new ArrayList<Penalty>();
	}

	public Integer getTotal() {
		return mTotal;
	}

	public void setTotal(Integer total) {
		this.mTotal = total;
	}

	public boolean hasZero() {
		return mHasZero;
	}

	public void setHasZero(boolean hasZero) {
		this.mHasZero = hasZero;
	}

	public List<Turn> getTurnList() {
		return mTurnList;
	}

	public void addTurn(Turn turn) {
		mTurnList.add(turn);
	}

	/**
	 * Add the penalty to the list of player's penalties. Commit penalty score
	 * to player's total.
	 * 
	 * @param penalty
	 */
	public void applyPenalty(Penalty penalty) {
		mPenaltyList.add(penalty);
		this.addTurnScoreToTotal(penalty.getPenaltyValue());
	}

	public List<Penalty> getPenaltyList() {
		return mPenaltyList;
	}

	public void setPenaltyList(List<Penalty> penaltyList) {
		this.mPenaltyList = penaltyList;
	}

	public String toString() {
		StringBuilder playerString = new StringBuilder();
		playerString.append(this.getTotal());

		return playerString.toString();
	}

	/**
	 * Add given score to player's total.
	 * 
	 * @param score
	 *            value <i>to be added</i> to the player's total.
	 */
	public void addTurnScoreToTotal(Integer score) {
		Integer playerScore = this.getTotal();
		if (playerScore == null) {
			playerScore = Constants.ZERO_VALUE;
		}

		this.setTotal(playerScore + score);
	}

}
