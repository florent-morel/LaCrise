package org.lacrise.activity.run;

import org.lacrise.R;
import org.lacrise.engine.Constants;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;


public class StatsActivity extends Activity {
    
        private ViewPager statsPager;
        private PlayerStatsAdapter statsAdapter;
        
        /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_viewer);
        
		Integer playerId = this.getIntent().getIntExtra(Constants.PLAYER_ID, 0);
        
        statsAdapter = new PlayerStatsAdapter(this);
        statsPager = (ViewPager) findViewById(R.id.stats_pager);
        statsPager.setAdapter(statsAdapter);
        statsPager.setCurrentItem(playerId);
    }
    
}
