package org.lacrise.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.lacrise.engine.Constants;

public class Game {
	
	/**
	 * List of players currently playing the game.
	 */
	private List<Player> mPlayerList;
	
	private List<Round> mRoundList;

	private Integer mScoreToReach = Constants.DEFAULT_SCORE_TO_REACH;

	private Integer mWarmUpRounds = Constants.DEFAULT_WARM_UP_ROUNDS;

	private Integer mRoundNumber = Constants.ZERO_VALUE;
	
	private boolean mIsGameOver = false;
	
	private boolean mIsTotalReached = false;

	public Game() {
		super();
		mPlayerList = new ArrayList<Player>();
		mRoundList = new ArrayList<Round>();
	}

	public void setGameOver(boolean mIsGameOver) {
		this.mIsGameOver = mIsGameOver;
	}

	public void setTotalReached(boolean isTotalReached) {
		this.mIsTotalReached = isTotalReached;
	}

	public boolean isGameOver() {
		return mIsGameOver;
	}
	
	public boolean isTotalReached() {
		return mIsTotalReached;
	}
	
	public List<Round> getRoundList() {
		return mRoundList;
	}
	
	public List<Integer> getPlayerScorePerRound(Integer playerId) {
		List<Integer> roundsScores = new ArrayList<Integer>();
		for (Round round : this.mRoundList) {
			Integer score = round.getPlayerScoreMap().get(playerId);
			roundsScores.add(score);
		}
		
		return roundsScores;
	}

	public Player getPlayerById(Integer playerId) {
		Player player = null;

		for (Player currentPlayer : mPlayerList) {
			if (currentPlayer.getId().equals(playerId)) {
				player = currentPlayer;
				break;
			}
		}

		return player;
	}

	/**
	 * @return
	 * @deprecated should get the players instead
	 */
	public List<String> getPlayerNames() {
		List<String> names = new ArrayList<String>();
		for (Player player : mPlayerList) {
			names.add(player.getName());
		}
		return names;
	}

	public SortedSet<Player> getSortedPlayers() {
		SortedSet<Player> sortedSet = null;
		TreeSet<Player> sortedPlayers = new TreeSet<Player>(mPlayerList);
			sortedSet = sortedPlayers.descendingSet();
		return sortedSet;
	}

	public List<Player> getPlayerList() {
		return mPlayerList;
	}

	public void addPlayerToList(Player player) {
		this.mPlayerList.add(player);
	}

	public void setPlayerList(List<Player> playerList) {
		this.mPlayerList = playerList;
	}

	public Integer getScoreToReach() {
		return mScoreToReach;
	}

	public void setScoreToReach(Integer scoreToReach) {
		this.mScoreToReach = scoreToReach;
	}

	public Integer getWarmUpRounds() {
		return mWarmUpRounds;
	}

	public void setWarmUpRounds(Integer warmUpRounds) {
		this.mWarmUpRounds = warmUpRounds;
	}

	public Integer getRoundNumber() {
		return mRoundNumber;
	}

	public void createNewRound() {
		this.mRoundNumber++;
		
		Map <Integer, Integer> playerScoreMap = new HashMap<Integer, Integer>();
		
		for (Player player : this.getPlayerList()) {
			playerScoreMap.put(player.getId(), player.getTotalScore());
		}
		
		Round newRound = new Round(mRoundNumber, playerScoreMap);
		
		this.mRoundList.add(newRound);
	}

	/**
	 * Check if all player entered the game (i.e. are not in warm-up rounds anymore).
	 * 
	 * @return false if at least one player is still in warm-up rounds.
	 */
	public boolean allPlayerStarted() {
		boolean allPlayerStarted = true;

		if (getPlayerList().isEmpty()) {
			allPlayerStarted = false;
		} else {
			for (Player player : getPlayerList()) {
				if (!player.hasStarted()) {
					allPlayerStarted = false;
					break;
				}
			}
		}
		return allPlayerStarted;
	}
}
