package org.lacrise.engine.game;

import java.util.Map;

public class Round {

	private Integer mRoundNumber;
	
	private Map<Integer, Integer> mPlayerScoreMap;

	public Round(Integer mRoundNumber, Map<Integer, Integer> mPlayerScoreMap) {
		super();
		this.mRoundNumber = mRoundNumber;
		this.mPlayerScoreMap = mPlayerScoreMap;
	}

	public Map<Integer, Integer> getPlayerScoreMap() {
		return mPlayerScoreMap;
	}
	
}
