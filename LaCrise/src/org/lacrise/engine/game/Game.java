package org.lacrise.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

	private Round currentRound;

	private Integer mMaxScore = Constants.ZERO_VALUE;

	private Integer mMinScore = Constants.ZERO_VALUE;

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

	public Map<Integer, List<Integer>> getPlayerScorePerRound(Integer playerId) {
		Map<Integer, List<Integer>> mapRoundScore = new HashMap<Integer, List<Integer>>();
		for (Round round : this.mRoundList) {
			List<Integer> scoreList = round.getPlayerScoreMap().get(playerId);
			if (scoreList != null && !scoreList.isEmpty()) {
				mapRoundScore.put(round.getRoundNumber(), scoreList);
			}
		}
		return mapRoundScore;
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

	/**
	 * Shortcut to get current #1 ranked player in current game.
	 * 
	 * @return
	 */
	public Player getFirstRankedPlayer() {
		return this.getPlayersByRank().first();
	}

	/**
	 * Get the set of game players sorted by score value.
	 * 
	 * @return
	 */
	public SortedSet<Player> getPlayersByRank() {
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

	public Integer getMaxScore() {
		return mMaxScore;
	}

	public Integer getMinScore() {
		return mMinScore;
	}

	public void createNewRound() {

		this.mRoundNumber++;

		Map<Integer, List<Integer>> playerScoreMap = new HashMap<Integer, List<Integer>>();

		for (Player player : this.getPlayerList()) {
			List<Integer> listRoundScore = new ArrayList<Integer>();
			playerScoreMap.put(player.getId(), listRoundScore);
		}

		currentRound = new Round(mRoundNumber, playerScoreMap);

		this.mRoundList.add(currentRound);
	}

	/**
	 * Check if all player entered the game (i.e. are not in warm-up rounds
	 * anymore).
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

	/**
	 * Add given score to player's total.
	 * 
	 * @param newScore
	 *            value <i>to be added</i> to the player's total.
	 */
	public void addTurnScoreToTotal(Player player, Integer newScore) {
		PlayerScore playerScore = player.getPlayerScore();
		Integer score = playerScore.getTotal();
		if (score == null) {
			score = Constants.ZERO_VALUE;
		}

		Integer newTotal = score + newScore;
		playerScore.setTotal(newTotal);

		// Add score to current round player score
		List<Integer> list = currentRound.getPlayerScoreMap().get(
				player.getId());
		list.add(newTotal);
		currentRound.getPlayerScoreMap().put(player.getId(), list);

		// Check it against max and min scores so far
		if (mMaxScore.compareTo(newTotal) < 0) {
			mMaxScore = newTotal;
		}

		if (mMinScore.compareTo(newTotal) > 0) {
			mMinScore = newTotal;
		}

	}

	/**
	 * A quick dirty way to get the current rank of given player.
	 * 
	 * @param player
	 * @return
	 */
	public Integer getPlayerRank(Player player) {
		SortedSet<Player> sortedPlayers = getPlayersByRank();
		Integer rank = 1;

		for (Player currentPlayer : sortedPlayers) {
			if (currentPlayer.getId().equals(player.getId())) {
				break;
			} else {
				rank++;
			}
		}

		return rank;
	}

	/**
	 * Add the penalty to the list of player's penalties. Commit penalty score
	 * to player's total.
	 * 
	 * @param penalty
	 */
	public void applyPenalty(Player player, Penalty penalty) {
		PlayerScore playerScore = player.getPlayerScore();
		playerScore.getPenaltyList().add(penalty);
		this.addTurnScoreToTotal(player, penalty.getPenaltyValue());
	}

	public int getNumberActivePlayer() {
		int nbActive = 0;
		for (Player player : this.getPlayerList()) {
			if (player.isActive()) {
				nbActive++;
			}
		}
		return nbActive;
	}

	/**
	 * Get the best score so far for a given player.
	 * 
	 * @param mPlayer
	 * @return
	 */
	public Integer getBestRoundScore(Player mPlayer) {
		Integer bestScore = Constants.ZERO_VALUE;
		Set<Entry<Integer, Integer>> entrySet = getPlayerRoundsScore(mPlayer)
				.entrySet();
		for (Entry<Integer, Integer> entry : entrySet) {

			Integer roundScore = entry.getValue();

			if (roundScore.compareTo(bestScore) > 0) {
				bestScore = roundScore;
			}

		}
		return bestScore;
	}

	public Map<Integer, Integer> getPlayerRoundsScore(Player player) {
		Map<Integer, Integer> roundScores = new HashMap<Integer, Integer>();
		for (Turn turn : player.getPlayerScore().getTurnList()) {
			roundScores.put(turn.getId(), turn.getScore());
		}
		return roundScores;
	}

	public Integer getAverageScore(Player player) {
		Integer averageScore = Constants.ZERO_VALUE;
		if (player.getLastPlayedTurnId().compareTo(Constants.ZERO_VALUE) > 0) {
			averageScore = player.getTotalScore(false)
					/ player.getLastPlayedTurnId();
		}
		return averageScore;
	}

	/**
	 * Returns the list of player who hit given player, the dirty way.
	 * 
	 * @param player
	 * @return
	 */
	public List<Player> getMaxHit(Player player, boolean isVictim) {

		Map<Player, List<Penalty>> penaltyPerPlayer = new HashMap<Player, List<Penalty>>();

		PlayerScore playerScore = player.getPlayerScore();
		for (Penalty penalty : playerScore.getPenaltyList()) {

			boolean computeCurrentPlayer = false;
			// victim case: player is not the one who hit but the one who was
			// hit

			if (isVictim) {
				if (!penalty.getPlayer().getId().equals(player.getId())
						&& penalty.getVictim().getId().equals(player.getId())) {
					computeCurrentPlayer = true;
				}
			} else {
				// Hit case: player is the one who hit
				if (penalty.getPlayer().getId().equals(player.getId())
						&& !penalty.getVictim().getId().equals(player.getId())) {
					computeCurrentPlayer = true;
				}
			}

			if (computeCurrentPlayer) {
				List<Penalty> list = penaltyPerPlayer.get(penalty.getPlayer());
				if (list == null) {
					list = new ArrayList<Penalty>();
				}
				if (!list.contains(penalty)) {
					list.add(penalty);
				}
				penaltyPerPlayer.put(penalty.getPlayer(), list);
			}
		}
		return getPenaltyPlayerList(penaltyPerPlayer);
	}

	/**
	 * Return the list of player having the maximum number of penalties.
	 * 
	 * @param penaltyPerPlayer
	 * @return
	 */
	private List<Player> getPenaltyPlayerList(
			Map<Player, List<Penalty>> penaltyPerPlayer) {
		int maxHits = 0;
		List<Player> playerList = new ArrayList<Player>();

		Map<Integer, List<Player>> penaltyMap = new HashMap<Integer, List<Player>>();
		// Get Player who has maximum hits
		for (Entry<Player, List<Penalty>> entry : penaltyPerPlayer.entrySet()) {
			List<Penalty> list = entry.getValue();
			if (list.size() > maxHits) {
				maxHits = list.size();
				// New maximum, init list
				penaltyMap = new HashMap<Integer, List<Player>>();
				playerList = new ArrayList<Player>();
				playerList.add(entry.getKey());
				penaltyMap.put(maxHits, playerList);
			} else if (list.size() == maxHits) {
				// Another player with same number of hits
				playerList = penaltyMap.get(Integer.valueOf(maxHits));
				if (playerList == null) {
					playerList = new ArrayList<Player>();
				}
				penaltyMap.put(maxHits, playerList);
			}

		}
		return penaltyMap.get(maxHits);
	}

	public Integer getNumberZeroPenalty(Player player) {
		Integer number = Constants.ZERO_VALUE;
		PlayerScore playerScore = player.getPlayerScore();
		for (Penalty penalty : playerScore.getPenaltyList()) {

			if (penalty.getPlayer().getId().equals(player.getId())
					&& penalty.getVictim().getId().equals(player.getId())) {
				number++;
			}
		}

		return number;
	}
}
