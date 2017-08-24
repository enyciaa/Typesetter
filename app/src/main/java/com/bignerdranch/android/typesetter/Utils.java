package com.bignerdranch.android.typesetter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

  public static void hideKeyboard(@NonNull Activity activity) {
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public static String formatFloatToDisplay(float floatValue) {
    if (floatValue == (int) floatValue) {
      // Return without decimal place
      return String.format("%d", (int) floatValue);
    } else {
      // Return with decimal place
      return String.format("%s", floatValue);
    }
  }

  public static Bitmap getBitmapFromView(@NonNull View view) {
    Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    Drawable bgDrawable = view.getBackground();
    if (bgDrawable != null) {
      bgDrawable.draw(canvas);
    } else {
      canvas.drawColor(Color.WHITE);
    }
    view.draw(canvas);

    return returnedBitmap;
  }
}
