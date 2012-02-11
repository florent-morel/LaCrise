package org.lacrise.engine.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Round {

  private Integer mRoundNumber;

  private boolean mAtLeastOneScore = false;

  private Map<Integer, List<Integer>> mPlayerScoreMap;

  private Map<Integer, Turn> mPlayerTurnMap;

	public Round(Integer mRoundNumber, Map<Integer, List<Integer>> mPlayerScoreMap) {
		super();
		this.mRoundNumber = mRoundNumber;
		this.mPlayerScoreMap = mPlayerScoreMap;
		mPlayerTurnMap = new HashMap<Integer, Turn>();
	}

	public Map<Integer, List<Integer>> getPlayerScoreMap() {
		return mPlayerScoreMap;
	}

	public Integer getRoundNumber() {
    return mRoundNumber;
  }

  public Map<Integer, Turn> getPlayerTurnMap() {
    return mPlayerTurnMap;
  }

  public void addTurnToPlayerMap(Integer playerId, Turn turn) {
    this.mPlayerTurnMap.put(playerId, turn);
  }

  public boolean atLeastOneScore() {
    return mAtLeastOneScore;
  }

  public void setAtLeastOneScore(boolean atLeastOneScore) {
    this.mAtLeastOneScore = atLeastOneScore;
  }

}
