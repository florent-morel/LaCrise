package org.lacrise.activity.create;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;

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

		mNbPlayerText = (EditText) findViewById(R.id.nbPlayers);

		mScoreToReachText = (EditText) findViewById(R.id.scoreToReach);
		
		mWarmUps = (EditText) findViewById(R.id.warmUpRounds);
		
		Button button = (Button) findViewById(R.id.create);
		button.setOnClickListener(this);

	}

	private void startNewGame() {

		mGameManager = GameManager.getSingletonObject();

		mGameManager.startNewGame(
				Integer.valueOf(mNbPlayerText.getText().toString()),
				Integer.valueOf(mScoreToReachText.getText().toString()), 
				Integer.valueOf(mWarmUps.getText().toString()), false);
	}

	@Override
	public void onClick(View v) {
		this.startNewGame();
		this.createBoardGame();
	}

	private void createBoardGame() {
		Intent i = new Intent(this, GameBoard.class);
		startActivityForResult(i, Constants.NEW_GAME);
	}

}
