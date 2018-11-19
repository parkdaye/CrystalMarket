package com.android.crystalmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import com.android.crystalmarket.helper.BottomNavigationBehavior;
import com.android.crystalmarket.helper.SessionManager;

import info.androidhive.loginandregistration.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.crystalmarket.activity.CategoryFragment;
//import info.androidhive.bottomnavigation.fragment.GiftsFragment;
//import info.androidhive.bottomnavigation.fragment.ProfileFragment;
//import info.androidhive.bottomnavigation.fragment.StoreFragment;
//import info.androidhive.bottomnavigation.helper.BottomNavigationBehavior;

public class MainActivity extends AppCompatActivity {

	private SessionManager session;
	private ActionBar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		session = new SessionManager(getApplicationContext());
		toolbar = getSupportActionBar();
		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



		// attaching bottom sheet behaviour - hide / show on scroll
		CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
		layoutParams.setBehavior(new BottomNavigationBehavior());

		toolbar.setTitle("수정마켓");
		loadFragment(new HomeFragment());

		getSupportActionBar().setTitle("수정마켓");
		getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this,R.color.bgBottomNavigation)); // ActionBar의 배경색 변경
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); //home화면

	}


	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment fragment;
			switch (item.getItemId()) {
				case R.id.navi_home:
					toolbar.setTitle("홈");
					fragment = new HomeFragment();
					loadFragment(fragment);
					return true;
				case R.id.navi_category:
					toolbar.setTitle("카테고리");
					fragment = new CategoryFragment();
					loadFragment(fragment);
					return true;
				case R.id.navi_upload:
					toolbar.setTitle("상품 업로드");
					Intent intent = new Intent(MainActivity.this, UploadActivity.class);
					startActivity(intent);
					finish();
					return true;
				case R.id.navi_alarm:
					toolbar.setTitle("알림");
					//fragment = new ProfileFragment();
					//loadFragment(fragment);
					return true;
				case R.id.navi_admin:
					toolbar.setTitle("계정관리");
					fragment = new AdminFragment();
					loadFragment(fragment);
					return true;
			}

			return false;
		}
	};


	private void loadFragment(Fragment fragment) {
		// load fragment
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.frame_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_actionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home){
			Toast.makeText(this, "로그아웃 했습니다.", Toast.LENGTH_SHORT).show();
			session.logoutUser();
			return true;
		}


		if (id == R.id.search_button) {
			//검색 기능 구현하기
			Intent homeIntent = new Intent(this, SearchActivity.class);
			startActivity(homeIntent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}



}