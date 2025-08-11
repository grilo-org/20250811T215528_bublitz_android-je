package br.jus.tremt.ondevoto;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VemPraUrna extends Activity implements AnimationListener {

	Animation anim1, anim2;
	TextView txt2, txt3, txt4;
	LinearLayout ll1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vem_pra_urna);

		anim1 = AnimationUtils.loadAnimation(this, R.anim.animat1);
		anim2 = AnimationUtils.loadAnimation(this, R.anim.sequential);
		anim2.setAnimationListener(this);

		ll1 = (LinearLayout) findViewById(R.id.LinearLayout1);
		txt2 = (TextView) findViewById(R.id.textView2);
		txt3 = (TextView) findViewById(R.id.textView3);
		txt4 = (TextView) findViewById(R.id.txt4);
		String vem = "<font color=#ffeb50>#</font>"
				+ "<font color=#619d6a>vem</font>"
				+ "<font color=#ffeb50>pra</font>"
				+ "<font color=#619d6a>urna</font>";
		txt4.setText(Html.fromHtml(vem));
	}

	public void inicia(View v) {
		txt2.startAnimation(anim1);
		txt3.startAnimation(anim1);
		txt4.startAnimation(anim2);
	}

	@Override
	protected void onResume() {
		super.onResume();
		txt2.startAnimation(anim1);
		txt3.startAnimation(anim1);
		txt4.startAnimation(anim2);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == anim2) {
			anim2.start();
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
	}
}
