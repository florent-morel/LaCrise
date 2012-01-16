package org.lacrise.engine.game;

import java.util.List;
import java.util.Map;

public class Round {

	private Integer mRoundNumber;

	private Map<Integer, List<Integer>> mPlayerScoreMap;

	public Round(Integer mRoundNumber, Map<Integer, List<Integer>> mPlayerScoreMap) {
		super();
		this.mRoundNumber = mRoundNumber;
		this.mPlayerScoreMap = mPlayerScoreMap;
	}

	public Map<Integer, List<Integer>> getPlayerScoreMap() {
		return mPlayerScoreMap;
	}

  protected Integer getRoundNumber() {
    return mRoundNumber;
  }

}
