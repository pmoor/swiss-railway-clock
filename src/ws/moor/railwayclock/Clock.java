/*
 * Swiss Railway Clock - A clock app for Android.
 * Copyright (C) 2011  Patrick Moor <patrick@moor.ws>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ws.moor.railwayclock;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.widget.ImageView;

public class Clock extends Activity {

  private static final Object TOKEN = new Object();

  private MathSupport mathSupport = new MathSupport();

  private ImageView secondArmImage;
  private ImageView minuteArmImage;
  private ImageView hourArmImage;

  private Handler handler;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    secondArmImage = (ImageView) findViewById(R.id.second);
    minuteArmImage = (ImageView) findViewById(R.id.minute);
    hourArmImage = (ImageView) findViewById(R.id.hour);

    handler = new Handler();
  }

  @Override
  protected void onStop() {
    super.onStop();
    handler.removeCallbacksAndMessages(TOKEN);
  }

  @Override
  protected void onStart() {
    super.onStart();

    Calendar currentTime = Calendar.getInstance();
    applyAnimations(currentTime);

    Calendar nextRunTime = mathSupport.getNextRunTime(currentTime);
    long difference = mathSupport.milisecondDifference(currentTime, nextRunTime);

    handler.postAtTime(new UiRunnable(new AnimationUpdater(nextRunTime)), TOKEN,
            SystemClock.uptimeMillis() + difference);
  }

  private void applyAnimations(Calendar currentTime) {
    Animation secondAnimation = mathSupport.getSecondArmAnimation(currentTime);
    secondArmImage.startAnimation(secondAnimation);
    Animation minuteAnimation = mathSupport.getMinuteArmAnimation(currentTime);
    minuteArmImage.startAnimation(minuteAnimation);
    Animation hourAnimation = mathSupport.getHourArmAnimation(currentTime);
    hourArmImage.startAnimation(hourAnimation);
  }

  private class AnimationUpdater implements Runnable {

    private final Calendar nextRunTime;

    public AnimationUpdater(Calendar nextRunTime) {
      this.nextRunTime = nextRunTime;
    }

    public void run() {
      applyAnimations(nextRunTime);

      Calendar currentTime = Calendar.getInstance();
      Calendar newNextRunTime = mathSupport.getNextRunTime(nextRunTime);
      long difference = mathSupport.milisecondDifference(currentTime, newNextRunTime);
      handler.postAtTime(new UiRunnable(new AnimationUpdater(newNextRunTime)), TOKEN,
              SystemClock.uptimeMillis() + difference);
    }
  }

  private class UiRunnable implements Runnable {

    private final Runnable delegate;

    private UiRunnable(Runnable delegate) {
      this.delegate = delegate;
    }

    public void run() {
      runOnUiThread(delegate);
    }
  }
}