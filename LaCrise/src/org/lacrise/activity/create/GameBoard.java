package org.lacrise.activity.create;

import android.app.ListActivity;

/**
 * @author florent
 * @deprecated this activity is not needed anymore since 
 * Game is directly launched by {@link#NewGame}
 */
public class GameBoard extends ListActivity {
//
//	private static final int ACTIVITY_NEW = 0;
//
//	private static final int LAUNCH_ID = Menu.FIRST;
//
//	private static GameManager mGameManager;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setTitle(R.string.board_game);
//
//		mGameManager = GameManager.getSingletonObject();
//
//		refreshList();
//
//		ListView lv = getListView();
//		lv.setTextFilterEnabled(true);
//
//		lv.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				ArrayAdapter<String> adapter = (ArrayAdapter) parent
//						.getAdapter();
//				String playerName = adapter.getItem(position);
//				createRenameDialog(position, playerName);
//			}
//		});
//	}
//
//	private void refreshList() {
//		setListAdapter(new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, mGameManager.getGame()
//						.getPlayerNames()));
//	}
//
//	private void createRenameDialog(int position, String playerName) {
//		Intent intent = new Intent(this, RenamePlayer.class);
//
//		intent.putExtra(Constants.PLAYER_ID, position);
//		intent.putExtra(Constants.PLAYER_NAME, playerName);
//
//		startActivityForResult(intent, ACTIVITY_NEW);
//	}
//
//	private void launchGame() {
//		Intent intent = new Intent(this, ScoreBoard.class);
//		startActivityForResult(intent, Constants.GAME_NEW);
//	}
//
//	private void continueGame() {
//		Intent intent = new Intent(this, ScoreBoard.class);
//		startActivityForResult(intent, Constants.GAME_CONTINUE);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// If the request went well (OK) and the request was ACTIVITY_NEW
//		// TODO if (resultCode == Activity.RESULT_OK && requestCode ==
//		// ACTIVITY_NEW) {
//		if (requestCode == ACTIVITY_NEW) {
//			refreshList();
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		menu.add(0, LAUNCH_ID, 0, R.string.menu_launch);
//
//		// FIXME this code is dupe with LaCrise
//		if (mGameManager.getGame().allPlayerStarted()) {
//			// A game is ongoing, add menu to continue
//			if (menu.findItem(Constants.GAME_CONTINUE) == null) {
//				menu.add(0, Constants.GAME_CONTINUE, 1, R.string.menu_continue);
//			}
//		} else {
//			if (menu.findItem(Constants.GAME_CONTINUE) != null) {
//				menu.removeItem(Constants.GAME_CONTINUE);
//			}
//		}
//		return true;
//	}
//
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		switch (item.getItemId()) {
//		case LAUNCH_ID:
//			launchGame();
//			return true;
//		case Constants.GAME_CONTINUE:
//			continueGame();
//			return true;
//		}
//
//		return super.onMenuItemSelected(featureId, item);
//	}
}
