package org.lacrise.activity.run;

import java.util.List;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.adapter.TurnHistoryAdapter;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;

public class TurnHistory extends ListActivity {

	private static GameManager mGameManager;

	private Resources mResources;

	private Player mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();

		Integer playerId = this.getIntent().getIntExtra(Constants.PLAYER_ID, 0);

		 mPlayer = mGameManager.getGame().getPlayerById(playerId);

		refreshList();

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
	}

	private void refreshList() {
		TurnHistoryAdapter turnHistoryAdapter = new TurnHistoryAdapter(this);

		if (mPlayer != null) {
		setTitle(String.format(mResources
				.getString(R.string.turn_history_title), mPlayer.getName()));


			List<Turn> turnList = mPlayer.getPlayerScore().getTurnList();

			for (Turn turn : turnList) {
				turnHistoryAdapter.addItem(turn);
			}

		}

		setListAdapter(turnHistoryAdapter);
	}

}
