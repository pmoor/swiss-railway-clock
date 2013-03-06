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

import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class MathSupport {

  private Interpolator linearInterpolator = new LinearInterpolator();

  private static long SECOND_ARM_ONE_MINUTE_DURATION = 58500; // 58s

  public Animation getSecondArmAnimation(Calendar currentTime) {
    long currentMilliSecond = 1000 * currentTime.get(Calendar.SECOND)
            + currentTime.get(Calendar.MILLISECOND);
    if (currentMilliSecond >= SECOND_ARM_ONE_MINUTE_DURATION) {
      // no animation needed, we should wait until the next minute
      RotateAnimation anim = new RotateAnimation(0, 0, Animation.RELATIVE_TO_SELF, 0.5f,
              Animation.RELATIVE_TO_SELF, 0.5f);
      anim.setInterpolator(linearInterpolator);
      anim.setFillAfter(true);
      return anim;
    }

    float currentOffset = currentMilliSecond * (360.0f / SECOND_ARM_ONE_MINUTE_DURATION);
    RotateAnimation anim = new RotateAnimation(currentOffset, 360, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    anim.setInterpolator(linearInterpolator);
    anim.setRepeatCount(0);
    anim.setDuration(SECOND_ARM_ONE_MINUTE_DURATION - currentMilliSecond);
    anim.setFillAfter(true);
    return anim;
  }

  public Animation getMinuteArmAnimation(Calendar currentTime) {
    long currentMinute = currentTime.get(Calendar.MINUTE);
    long currentMilliSecond = 1000 * currentTime.get(Calendar.SECOND)
            + currentTime.get(Calendar.MILLISECOND);
    if (currentMilliSecond < 200) {
      // animate it
      RotateAnimation anim = new RotateAnimation(6 * (currentMinute - 1), 6 * currentMinute,
              Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
      anim.setInterpolator(linearInterpolator);
      anim.setRepeatCount(0);
      anim.setDuration(200);
      anim.setFillAfter(true);
      return anim;
    } else {
      // just set it
      RotateAnimation anim = new RotateAnimation(6 * currentMinute, 6 * currentMinute,
              Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
      anim.setInterpolator(linearInterpolator);
      anim.setRepeatCount(0);
      anim.setDuration(0);
      anim.setFillAfter(true);
      return anim;
    }
  }

  public Animation getHourArmAnimation(Calendar currentTime) {
    float currentHour = currentTime.get(Calendar.HOUR_OF_DAY) % 12
            + currentTime.get(Calendar.MINUTE) / 60.0f;
    RotateAnimation anim = new RotateAnimation(30 * currentHour, 30 * currentHour,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    anim.setInterpolator(linearInterpolator);
    anim.setRepeatCount(0);
    anim.setDuration(0);
    anim.setFillAfter(true);
    return anim;
  }

  public Calendar getNextRunTime(Calendar currentTime) {
    Calendar next = (Calendar) currentTime.clone();
    next.add(Calendar.MINUTE, 1);
    next.set(Calendar.SECOND, 0);
    next.set(Calendar.MILLISECOND, 0);
    return next;
  }

  public long milisecondDifference(Calendar now, Calendar then) {
    return then.getTimeInMillis() - now.getTimeInMillis();
  }
}
