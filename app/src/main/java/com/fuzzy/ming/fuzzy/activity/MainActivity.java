package com.fuzzy.ming.fuzzy.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fuzzy.ming.fuzzy.R;
import com.fuzzy.ming.fuzzy.fragment.MainFragment;


public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideNavigationBar(getWindow().getDecorView());
		setContentView(R.layout.activity_main);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fragment, new MainFragment());
		ft.commit();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		if(getFragmentManager().getBackStackEntryCount() == 0){
			super.onBackPressed();
		}else{
			getFragmentManager().popBackStack();
		}
	}
}
