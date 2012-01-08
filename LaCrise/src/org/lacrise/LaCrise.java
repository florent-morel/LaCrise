package org.lacrise;

import org.lacrise.activity.create.NewGame;
import org.lacrise.activity.run.ScoreBoard;
import org.lacrise.engine.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * LaCrise home activity. 
 * 
 * @author florent
 *
 */
public class LaCrise extends Activity {

	private GameManager mGameManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mGameManager = GameManager.getSingletonObject();
	}

	private void buildMenu(Menu menu, boolean isOnCreate) {
		if (isOnCreate) {
			menu.add(0, Constants.GAME_NEW, 0, R.string.menu_new);
			menu.add(0, Constants.NEW_GAME_DEBUG, 1, R.string.menu_new_debug);
			menu.add(0, Constants.NEW_GAME_QUICK, 1, R.string.menu_new_quick);
		}

		if (mGameManager.getGame().getPlayerList() != null) {
			// A game is ongoing, add menu to continue
			if (menu.findItem(Constants.GAME_CONTINUE) == null) {
				menu.add(0, Constants.GAME_CONTINUE, 2, R.string.menu_continue);
			}
		} else {
			if (menu.findItem(Constants.GAME_CONTINUE) != null) {
				menu.removeItem(Constants.GAME_CONTINUE);
			}
		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		super.onMenuOpened(featureId, menu);
		buildMenu(menu, false);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		buildMenu(menu, true);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case Constants.GAME_NEW:
			createGame();
			return true;
		case Constants.NEW_GAME_QUICK:
			createQuickGame();
			return true;
		case Constants.NEW_GAME_DEBUG:
			createGameDebug();
			return true;
		case Constants.GAME_CONTINUE:
			continueGame();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
		
	}

	private void createGame() {
		Intent i = new Intent(this, NewGame.class);
		startActivityForResult(i, Constants.GAME_NEW);
	}

	private void continueGame() {
		Intent intent = new Intent(this, ScoreBoard.class);
		startActivityForResult(intent, Constants.GAME_CONTINUE);
	}

	private void createQuickGame() {
		mGameManager.startNewGame(3, 1500, 1, false);
		Intent intent = new Intent(this, ScoreBoard.class);
		startActivityForResult(intent, Constants.NEW_GAME_QUICK);
	}

	private void createGameDebug() {
		mGameManager.startNewGame(3, 1500, 1, true);
		Intent intent = new Intent(this, ScoreBoard.class);
		startActivityForResult(intent, Constants.NEW_GAME_DEBUG);
	}
}