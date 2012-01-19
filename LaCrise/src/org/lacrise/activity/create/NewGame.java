package org.lacrise.activity.create;

import java.util.List;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.activity.run.ScoreBoard;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Game;
import org.lacrise.engine.game.Player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewGame extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private EditText mNbPlayerText;

	private EditText mScoreToReachText;

	private EditText mWarmUps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_game);
		setTitle(R.string.create_game);

		mGameManager = GameManager.getSingletonObject();

		mNbPlayerText = (EditText) findViewById(R.id.nbPlayers);

		mScoreToReachText = (EditText) findViewById(R.id.scoreToReach);

		mWarmUps = (EditText) findViewById(R.id.warmUpRounds);

		Button button = (Button) findViewById(R.id.create);
		button.setOnClickListener(this);

		// Prefill with existing game values if any
		Game currentGame = mGameManager.getGame();

		if (currentGame != null && !currentGame.getPlayerList().isEmpty()) {
			mNbPlayerText.setText(String.valueOf(currentGame.getPlayerList().size()));
			mScoreToReachText.setText(currentGame.getScoreToReach().toString());
			mWarmUps.setText(currentGame.getWarmUpRounds().toString());
		}

	}

	private void startNewGame() {

		List<Player> playerList = null;
		
		if (mGameManager.getGame() != null) {
			playerList = mGameManager.getGame().getPlayerList();
		}
		
		mGameManager.startNewGame(
				Integer.valueOf(mNbPlayerText.getText().toString()),
				Integer.valueOf(mScoreToReachText.getText().toString()),
				Integer.valueOf(mWarmUps.getText().toString()), false,
				playerList);

		Intent intent = new Intent(this, ScoreBoard.class);
		startActivityForResult(intent, Constants.GAME_NEW);
	}

	@Override
	public void onClick(View v) {
		this.startNewGame();
//		this.createBoardGame();
	}

//	private void createBoardGame() {
//		Intent i = new Intent(this, GameBoard.class);
//		startActivityForResult(i, Constants.NEW_GAME);
//	}

}
