package org.lacrise.activity.run;

import java.util.SortedSet;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.activity.create.AddPlayer;
import org.lacrise.activity.create.NewGame;
import org.lacrise.activity.create.RenamePlayer;
import org.lacrise.adapter.PlayerScoreAdapter;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Penalty;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ScoreBoard extends Activity {

	private static GameManager mGameManager;

	private Resources mResources;

	private ListView mScoreList;

	private PlayerScoreAdapter mScoreAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_board);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();

		initListView();
		displayWelcomeDialog();
	}

	private void initListView() {
		refreshList();

		mScoreList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mGameManager.getGame().isGameOver()) {
					// Prevent from creating next turn
					buildGameOverMessage(null, null, new StringBuilder());
				} else {
					playNextTurn();
				}

			}
		});

	}

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    menu.add(0, Constants.MENU_GAME_OPTIONS, 0, R.string.menu_game_options);
    menu.add(0, Constants.MENU_ADD_PLAYER, 0, R.string.menu_add_player);
    menu.add(0, Constants.MENU_STAR_NEW, 0, R.string.menu_start_new_game);

    return true;
  }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case Constants.MENU_GAME_OPTIONS:
			launchGameOptions();
			return true;
		case Constants.MENU_ADD_PLAYER:
			launchAddPlayer();
			return true;
		case Constants.MENU_STAR_NEW:
			launchNewGame();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void launchGameOptions() {
		Intent intent = new Intent(this, GameOptions.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

	private void launchAddPlayer() {
		Intent intent = new Intent(this, AddPlayer.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

	private void launchNewGame() {
		Intent i = new Intent(this, NewGame.class);
		startActivityForResult(i, Constants.GAME_NEW);
	}

	/**
	 * Display welcome dialog message. Display toast with remaining number of
	 * warm-up rounds.
	 */
	private void displayWelcomeDialog() {
		if (!mGameManager.getGame().allPlayerStarted()) {
			Context context = getApplicationContext();
			Toast toast = Toast.makeText(context, String.format(mResources
					.getString(R.string.dialog_welcome), mGameManager.getGame()
					.getWarmUpRounds()),
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.score_board_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Player player = (Player) mScoreList.getAdapter().getItem(info.position);
		switch (item.getItemId()) {
		case R.id.rename:
			renamePlayers(player.getId(), player.getName());
			return true;
		case R.id.turn_history_id:
			displayTurnHistory(player.getId());
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void displayTurnHistory(Integer playerId) {
		Intent intent = new Intent(this, TurnHistory.class);
		intent.putExtra(Constants.PLAYER_ID, playerId);
		startActivityForResult(intent, Constants.GAME_TURN_HISTORY);
	}

	private void renamePlayers(Integer playerId, String playerName) {
		Intent intent = new Intent(this, RenamePlayer.class);
		intent.putExtra(Constants.PLAYER_ID, playerId);
		intent.putExtra(Constants.PLAYER_NAME, playerName);
		startActivityForResult(intent, Constants.GAME_RENAME_PLAYERS);
	}

	private void playNextTurn() {
		mGameManager.playTurn();
		Intent intent = new Intent(this, EnterTurnScore.class);
		startActivityForResult(intent, Constants.ENTER_TURN_SCORE);
	}

	/**
	 * Refresh the score board player list.
	 *
	 * @param descending
	 */
	private void refreshList() {
		setTitle(String.format(mResources.getString(R.string.score_board),
				mGameManager.getGame().getRoundNumber(), mGameManager.getGame()
						.getScoreToReach()));

		mScoreList = (ListView) findViewById(R.id.scoreList);
		mScoreAdapter = new PlayerScoreAdapter(this, R.layout.player_row);

		SortedSet<Player> playersByRank = mGameManager
				.getPlayersByRank();

		for (Player player : playersByRank) {
			mScoreAdapter.addItem(player);
		}

		mScoreList.setAdapter(mScoreAdapter);

		registerForContextMenu(mScoreList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		StringBuilder message = new StringBuilder();
		Turn playedTurn = null;
		Player player = mGameManager.getCurrentPlayer();
		if (player != null) {
			playedTurn = player.getCurrentTurn();

			switch (resultCode) {
			case RESULT_OK:
				break;
			case Constants.PENALTY_APPLIED:
				buildPenaltyMessage(player, playedTurn, message);
				break;
			case Constants.TOTAL_REACHED:
				buildTotalReachedMessage(player, playedTurn, message);
				break;
			case Constants.GAME_OVER:
				buildGameOverMessage(player, playedTurn, message);
				break;

			default:
				// TODO Something went wrong
				break;
			}

			// In any case, build the zero, score and next player messages
			this.buildZeroMessage(player, message);
			this.buildScoreMessage(player, message);
			if (!mGameManager.getGame().isGameOver()) {
				this.buildNextPlayerMessage(message);
			}

			// Create the dialog
			createAlert(message.toString());
		}

		refreshList();
	}

	private void buildNextPlayerMessage(StringBuilder message) {
		message.append(Constants.NEW_LINE);
		message.append(String.format(
				mResources.getString(R.string.dialog_next_player),
				mGameManager.getNextPlayer().getName()));
	}

	private void buildPenaltyMessage(Player player, Turn playedTurn,
			StringBuilder message) {
		if (playedTurn != null) {
			message.append(mResources.getString(R.string.dialog_penalty));
			for (Penalty penalty : playedTurn.getPenaltyList()) {
				message.append(Constants.NEW_LINE);

				Player victim = penalty.getVictim();
				if (player.getId().equals(victim.getId())) {
					message.append(mResources
							.getString(R.string.penalty_reason_own));
				} else {
					message.append(String.format(
							mResources.getString(R.string.penalty_reason_other),
							penalty.getPlayer().getName(), victim.getName()));
					message.append(Constants.NEW_LINE);
					message.append(String.format(mResources
							.getString(R.string.dialog_player_new_score),
							victim.getName(), victim.getTotalScore()));
				}

			}
		}
	}

	private void buildTotalReachedMessage(Player player, Turn playedTurn,
			StringBuilder message) {
		if (playedTurn != null) {
			if (playedTurn.getPenaltyList().size() > 0) {
				buildPenaltyMessage(player, playedTurn, message);
			}
			message.append(mResources.getString(R.string.dialog_total_reached));
		}
	}

	private void buildZeroMessage(Player player, StringBuilder message) {
		if (player.getPlayerScore().hasZero()) {
			message.append(mResources
					.getString(R.string.turn_score_player_warning));
		}
	}

	private void buildScoreMessage(Player player, StringBuilder message) {
		StringBuilder builder = new StringBuilder();
		if (player.getPlayerScore().getTotal() == null) {
			builder.append(mResources.getString(R.string.no_score));
		} else {
			builder.append(player.getPlayerScore().getTotal().toString());
		}

		message.append(Constants.NEW_LINE);
		message.append(String.format(
				mResources.getString(R.string.dialog_player_new_score),
				player.getName(), builder));
	}

	private void buildGameOverMessage(Player player, Turn playedTurn,
			StringBuilder message) {
		if (player != null && playedTurn != null
				&& playedTurn.getPenaltyList().size() > 0) {
			buildPenaltyMessage(player, playedTurn, message);
			message.append(Constants.NEW_LINE);
		}

		message.append(mResources.getString(R.string.dialog_game_over));
		message.append(Constants.NEW_LINE);
		message.append(String.format(mResources
				.getString(R.string.dialog_winner_is), mGameManager
				.getFirstRankedPlayer().getName()));
	}

	private void createAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(mResources.getString(R.string.dialog_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();
	}
}
