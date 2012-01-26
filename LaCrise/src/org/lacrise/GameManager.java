package org.lacrise;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Game;
import org.lacrise.engine.game.Penalty;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.PlayerScore;
import org.lacrise.engine.game.Round;
import org.lacrise.engine.game.Turn;

/**
 * Singleton handling the game instance.
 *
 * Starts a new game, sorts players by rank, computes a turn score...
 *
 * @author florent
 *
 */
public class GameManager {

	private static GameManager mGameManager;

	private Game mGame;

	private Turn mCurrentTurn;

	private Player mCurrentPlayer;

	private Integer mRoundNumberPlayers = Constants.ZERO_VALUE;

	public static Pattern mScorePattern = Pattern.compile("[0-9]{1,5}");

	private GameManager() {
		if (mGame == null) {
			mGame = new Game();
		}
	}

	public static GameManager getSingletonObject() {
		if (mGameManager == null) {
			mGameManager = new GameManager();
		}
		return mGameManager;
	}

	public Game getGame() {
		return mGame;
	}

	public void startNewGame(Integer numberOfPlayers, Integer scoreToReach,
			Integer nbWarmUps, List<Player> playerList) {
		mGame = new Game();
		mCurrentPlayer = null;

		if (playerList == null) {
			playerList = new ArrayList<Player>(numberOfPlayers);
		}

		for (int i = 0; i < numberOfPlayers; i++) {

			if (i < playerList.size()) {
				Player player = playerList.get(i);
				if (player == null) {
					createNewPlayer(playerList, i);
				} else {
					// Reset player game history
					player.initPlayer();
				}
			} else {
				// More requested players than existing
				createNewPlayer(playerList, i);
			}

		}

		mGame.setPlayerList(playerList);

		mGame.setScoreToReach(scoreToReach);

		mGame.setWarmUpRounds(nbWarmUps);

	}

	private void createNewPlayer(List<Player> playerList, int i) {
		Player player;
		player = new Player(Integer.valueOf(i));
		// TODO StringBuilder nameBuilder = new
		// StringBuilder(R.string.player_name_default);
		// nameBuilder.append((i+1));
		player.setName("P #" + (i + 1));
		playerList.add(player);
	}

	/**
	 * Get the next or previous player to play and initialize him for next turn.
	 *
	 * @param currentPlayer
	 * @param initPlayer
	 * @param getNext
	 *            get next player if true. get previous player otherwise.
	 * @return
	 */
	public Player getNextPlayer(Player currentPlayer, boolean displayPurpose,
			boolean getNext) {
		Player nextPlayer = computeNextPlayer(currentPlayer, displayPurpose, getNext);

		if (!displayPurpose) {

			if (Constants.ZERO_VALUE.equals(nextPlayer.getId())) {
			  Round currentRound = mGame.getCurrentRound();
			  // TODO check that we can create a new round in a cleaner way.
        if (currentRound == null || currentRound.atLeastOneScore()) {
          // Each time first player plays, increment the number of played
          // rounds
          mGame.createNewRound();
        }
			}
			// In case warm rounds are over, force player to enter the game
			if (!nextPlayer.hasStarted()
					&& mGame.getRoundNumber() > mGame.getWarmUpRounds()) {
				nextPlayer.setHasStarted();
			}
		}

		if (mGame.getNumberActivePlayer() > 0) {
			// Skip player if not active
			while (!nextPlayer.isActive()) {
				nextPlayer = getNextPlayer(nextPlayer, displayPurpose, getNext);
			}
		}

		return nextPlayer;
	}

	/**
	 * Get the next player to play.
	 *
	 * @param getNext
	 *            get next player if true. get previous player otherwise.
	 *
	 * @return
	 */
  private Player computeNextPlayer(Player currentPlayer, boolean displayPurpose, boolean getNext) {
    Player nextPlayer;
    int nbPlayers = mGame.getPlayerList().size();
    Player players[] = new Player[nbPlayers];
    players = mGame.getPlayerList().toArray(players);

    Integer nextId = Constants.ZERO_VALUE;
    if (displayPurpose) {
      if (currentPlayer == null) {
        nextId = Constants.ZERO_VALUE;
      }
      else {
        // Increment to get next player id
        nextId = currentPlayer.getId();
        if (getNext) {
          nextId += 1;
        }
        else {
          if (nextId == Constants.ZERO_VALUE) {
            // first player, set next id as player list size to get last
            // player
            nextId = mGame.getPlayerList().size();
          }
          nextId -= 1;
        }
      }
    }
    else {
      nextId = mRoundNumberPlayers;
    }

    int modulo = nextId % nbPlayers;
    nextPlayer = players[modulo];

    return nextPlayer;
  }

	private Integer checkEndGame(Integer result) {
		if (mGame.getPlayersByRank().size() > 0
				&& mGame.getPlayersByRank().first().getTotalScore(true) != null) {
			if (mGame.getPlayersByRank().first().getTotalScore(true) >= mGame
					.getScoreToReach()) {
				// Total score reached
				if (!mGame.isTotalReached()) {
					result = Constants.TOTAL_REACHED;
					mGame.setTotalReached(true);
				}

				// If current player is the last one, game is over
				// TODO get the last player properly
				if (mGame.getPlayerList().lastIndexOf(mCurrentPlayer) == mGame
						.getPlayerList().size() - 1) {
					// End of game
					result = Constants.GAME_OVER;
					mGame.setGameOver(true);
				}
			}

		}
		return result;
	}

	/**
	 * Get the set of game players sorted by gap value compared to given
	 * {@link#Player}.
	 *
	 * @return
	 */
	public SortedMap<Integer, Player> getPlayersGap(Player player) {
		TreeMap<Integer, Player> playerGap = new TreeMap<Integer, Player>();
		Integer playerScore = player.getTotalScore(true);
		if (playerScore == null) {
			playerScore = Constants.ZERO_VALUE;
		}

		for (Player otherPlayer : mGame.getPlayersByRank()) {
			Integer otherScore = otherPlayer.getTotalScore(true);
			if (otherScore != null) {
				playerGap.put(otherScore - playerScore, otherPlayer);

			}
		}

		return playerGap.descendingMap();
	}

	/**
	 * Initiate a turn: get next player and create new turn.
	 */
	public void playTurn() {
		mCurrentPlayer = getNextPlayer(mCurrentPlayer, false, true);

		mCurrentTurn = new Turn(mGame.getRoundNumber(),
				!mCurrentPlayer.hasStarted());
	}

	/**
	 * Terminates a run. Store turn score. Compute other scores for penalties.
	 * Check for end of game after this turn.
	 *
	 * @param turnScore
	 * @return
	 */
	public Turn endTurn(Integer turnScore) {
		Integer result = Constants.RESULT_OK;

		mCurrentPlayer.setCurrentTurn(mCurrentTurn);

		mCurrentTurn.setScore(turnScore);

		PlayerScore playerScore = mCurrentPlayer.getPlayerScore();
		playerScore.addTurn(mCurrentTurn);

		// Compute score
		result = computePlayerScore(mCurrentPlayer);

		// Commit and apply penalties on other players
		List<Player> playerList = new ArrayList<Player>();
		playerList.addAll(mGame.getPlayerList());
		commitPlayerScore(mCurrentPlayer, playerList);
		if (mCurrentTurn.getPenaltyList().size() > 0) {
			result = Constants.PENALTY_APPLIED;
		}

		// Check if total score is reached or game is over after this turn
		Integer checkEndGame = checkEndGame(result);
		if (!Integer.valueOf(Constants.RESULT_OK).equals(checkEndGame)) {
			result = checkEndGame;
		}

		mCurrentTurn.setTurnResultCode(result);

		// First update players rank at the end of current turn
		mGame.updateTurnRanks(mCurrentTurn);

		// Add the current turn for this player to current round
		mGame.getCurrentRound().addTurnToPlayerMap(mCurrentPlayer.getId(), mCurrentTurn);

		mRoundNumberPlayers++;

		return mCurrentTurn;
	}

	/**
	 * Check if a score is valid (Integer multiple of 50).
	 *
	 * @param score
	 * @return
	 */
	public boolean isTurnScoreValid(String score) {
		boolean isValid = false;

		if (score != null) {

			Matcher m = mScorePattern.matcher(score);
			isValid = m.matches();

			if (isValid) {
				Integer playerScore = Integer.valueOf(score);
				if (!Constants.ZERO_VALUE.equals(playerScore)) {
					// If not a zero, check that is x*50
					int modulo = playerScore % 50;
					if (modulo != 0) {
						isValid = false;
					}
				}
			}

		}

		return isValid;
	}

	/**
	 * Compute player score only if the player has already started the game
	 * (i.e. he is done with the warm-up rounds).
	 *
	 * @param player
	 * @return
	 */
	private Integer computePlayerScore(Player player) {
		Integer result = Constants.RESULT_OK;

		if (mCurrentPlayer.hasStarted()
				|| mCurrentTurn.getScore() > Constants.ZERO_VALUE) {
			mCurrentPlayer.setHasStarted();
			PlayerScore playerScore = player.getPlayerScore();

			if (Constants.ZERO_VALUE.equals(mCurrentTurn.getScore())) {
				if (playerScore.hasZero()) {
					Penalty penalty = new Penalty(mCurrentPlayer,
							mCurrentPlayer, mCurrentTurn.getId());
					mGame.applyPenalty(player, penalty);
					playerScore.setHasZero(false);
					result = Constants.PENALTY_APPLIED;
				} else {
					playerScore.setHasZero(true);
				}
			} else {
				playerScore.setHasZero(false);
			}

			if (player.getTotalScore(true) != null
					|| !Constants.ZERO_VALUE.equals(mCurrentTurn.getScore())) {
				mGame.addTurnScoreToTotal(player, mCurrentTurn.getScore());
			}
		}

		return result;

	}

	/**
	 * Check recursively whether current player got another's total score.
	 *
	 * @param currentPlayer
	 * @param filteredPlayerList
	 * @deprecated TODO use a linear way (list) instead of recursivity.
	 */
	private boolean commitPlayerScore(Player currentPlayer,
			List<Player> filteredPlayerList) {

		boolean penaltyApplied = false;

		filteredPlayerList.remove(currentPlayer);

		for (Player otherPlayer : filteredPlayerList) {
			// Do not apply penalty in case other player is still in warm up
			if (otherPlayer.hasStarted()
					&& otherPlayer.getPlayerScore().getTotal() != null) {
				if (otherPlayer.getPlayerScore().getTotal()
						.equals(currentPlayer.getPlayerScore().getTotal())) {

					Penalty penalty = new Penalty(currentPlayer, otherPlayer,
							mCurrentTurn.getId());
					mGame.applyPenalty(otherPlayer, penalty);
					mCurrentTurn.addPenalty(penalty);
					penaltyApplied = true;
					List<Player> newPlayerList = new ArrayList<Player>();
					newPlayerList.addAll(filteredPlayerList);
					commitPlayerScore(otherPlayer, newPlayerList);
				}
			}

		}
		return penaltyApplied;
	}

	/**
	 * Set player name.
	 *
	 * @param playerId
	 * @param playerName
	 */
	public void setPlayerName(Integer playerId, String playerName) {
		mGame.getPlayerById(playerId).setName(playerName);
	}

	public Player getCurrentPlayer() {
		return mCurrentPlayer;
	}

}
