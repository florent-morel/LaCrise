package org.lacrise.activity.run;

import org.lacrise.GameManager;
import org.lacrise.R;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Display ongoing game options.
 *
 *
 * @author fmorel
 *
 */
public class GameOptions extends Activity implements OnClickListener {

  private static GameManager mGameManager;

  private Resources mResources;

  private EditText mScoreToReachText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_options);
    mResources = getResources();
    mGameManager = GameManager.getSingletonObject();


    mScoreToReachText = (EditText) findViewById(R.id.options_score_field);

    mScoreToReachText.setText(mGameManager.getGame().getScoreToReach());

    Button button = (Button) findViewById(R.id.submit_options);
    button.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    mGameManager.getGame().setScoreToReach(Integer.valueOf(mScoreToReachText.getText().toString()));
  }

}
