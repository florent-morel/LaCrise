package org.lacrise.activity.create;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RenamePlayer extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private EditText mPlayerNameText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rename_player);

		mPlayerNameText = (EditText) findViewById(R.id.name_field);
		mPlayerNameText.setText(this.getIntent().getStringExtra(Constants.PLAYER_NAME));
		
		mPlayerNameText.setOnClickListener(this);
		
		Button button = (Button) findViewById(R.id.rename);
		button.setOnClickListener(this);

	}

	private void renamePlayer() {

		mGameManager = GameManager.getSingletonObject();

		mGameManager.setPlayerName(
				this.getIntent().getIntExtra(Constants.PLAYER_ID, 0),
				mPlayerNameText.getText().toString());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.name_field:
			mPlayerNameText.selectAll();
			break;
		case R.id.rename:
			this.renamePlayer();
			finish();
			break;
		default:
			break;
		}
		
	}

}
