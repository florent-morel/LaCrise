package org.lacrise.engine.game;

import org.lacrise.engine.Constants;

public class Penalty {

	private Player mPlayer;
	
	private Player mVictim;
	
	private Integer mTurnId;

	private Integer mPenaltyValue = Constants.PENALTY_VALUE;

	public Penalty(Player mPlayer, Player mVictim, Integer turnId) {
		super();
		this.mPlayer = mPlayer;
		this.mVictim = mVictim;
		this.mTurnId = turnId;
	}

	public Player getPlayer() {
		return mPlayer;
	}

	public Player getVictim() {
		return mVictim;
	}

	public Integer getPenaltyValue() {
		return mPenaltyValue;
	}

	public Integer getTurn() {
		return mTurnId;
	}

}
