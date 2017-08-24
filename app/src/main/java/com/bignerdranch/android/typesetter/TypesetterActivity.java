package com.bignerdranch.android.typesetter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.bignerdranch.android.typesetter.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class TypesetterActivity extends AppCompatActivity {

  private static final String TAG = "TypesetterActivity";
  private static final String DEFAULT_TEXT_SIZE = "24";

  private List<Font> fonts;
  private ActivityMainBinding binding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.fontSizeEditText.setText(DEFAULT_TEXT_SIZE);

    fonts = Font.listAssetFonts(this);

    binding.fontSpinner.setAdapter(new FontAdapter(this, fonts));
    binding.fontSpinner.setOnItemSelectedListener(onItemSelectedListener);
    binding.renderButton.setOnClickListener(v -> {
      renderValues();
      clearInputFocus();
    });
    binding.floatingActionButton.setOnClickListener(v -> {
      renderValues();
      clearInputFocus();
      shareScreenshot();
    });

    initializeEditTextValues();
    renderValues();
  }

  private void renderValues() {
    applyTextSize();
    applyLetterSpacing();
    applyLineSpacing();
  }

  private void clearInputFocus() {
    Utils.hideKeyboard(this);
    binding.containerLayout.requestFocus();
  }

  private void shareScreenshot() {
    Bitmap bitmap = Utils.getBitmapFromView(binding.constraint);
    File dir = getFilesDir();
    File file = new File(dir, "font-screenshot.png");
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
      outputStream.close();
    } catch (IOException e) {
      Log.e(TAG, "Failed to save screenshot");
      Snackbar.make(binding.containerLayout, R.string.failed_to_save_screenshot, Snackbar.LENGTH_SHORT).show();
      return;
    }

    Uri uri = FileProvider.getUriForFile(TypesetterActivity.this,
        "com.bignerdranch.android.typesetter.fileprovider",
        file);
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/png");
    intent.putExtra(Intent.EXTRA_STREAM, uri);

    startActivity(intent);
  }

  private void initializeEditTextValues() {
    initializeLetterSpacing();
    initializeLineSpacing();
  }

  private void initializeLetterSpacing() {
    binding.letterSpacingTextInputLayout.setEnabled(true);
    binding.letterSpacingEditText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
    float letterSpacing = binding.fillerTextView.getLetterSpacing();
    if (letterSpacing == 0) {
      binding.letterSpacingEditText.setText("0.00");
    } else {
      binding.letterSpacingEditText.setText(Utils.formatFloatToDisplay(letterSpacing));
    }
  }

  private void initializeLineSpacing() {
    float lineSpacing = binding.fillerTextView.getLineSpacingExtra();
    lineSpacing = lineSpacing / getResources().getDisplayMetrics().scaledDensity;
    binding.lineSpacingEditText.setText(Utils.formatFloatToDisplay(lineSpacing));
  }

  private void applyTextSize() {
    float size = Float.parseFloat(binding.fontSizeEditText.getText().toString());
    binding.fillerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  private void applyLetterSpacing() {
    String letterSpacing = binding.letterSpacingEditText.getText().toString();
    try {
      float letterEms = Float.parseFloat(letterSpacing);
      binding.fillerTextView.setLetterSpacing(letterEms);
//      binding.letterSpacingTextInputLayout.setErrorEnabled(false);
    } catch (NumberFormatException e) {
      Log.e(TAG, "Unable to format letter spacing");
//      binding.letterSpacingTextInputLayout.setErrorEnabled(true);
//      binding.letterSpacingTextInputLayout.setError(getString(R.string.nah));
    }
  }

  private void applyLineSpacing() {
    String lineSpacing = binding.lineSpacingEditText.getText().toString();
    try {
      float lineSpacingSp = Float.parseFloat(lineSpacing);
      float lineSpacingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lineSpacingSp, getResources().getDisplayMetrics());
      float multiplier = binding.fillerTextView.getLineSpacingMultiplier();
      binding.fillerTextView.setLineSpacing(lineSpacingPx, multiplier);
//      binding.lineSpacingTextInputLayout.setErrorEnabled(false);
    } catch (NumberFormatException e) {
      Log.e(TAG, "Unable to format line spacing");
//      binding.lineSpacingTextInputLayout.setErrorEnabled(true);
//      binding.lineSpacingTextInputLayout.setError(getString(R.string.nah));
    }
  }

  private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      Typeface typeface = TypefaceUtils.load(getAssets(), "fonts/" + fonts.get(position).getFileName());
      binding.fillerTextView.setTypeface(typeface);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
  };

  private static class FontAdapter extends ArrayAdapter<Font> {

    public FontAdapter(@NonNull Context context, @NonNull List<Font> fonts) {
      super(context, R.layout.dropdown_row, fonts);
      setDropDownViewResource(R.layout.dropdown_row);
    }
  }
}
