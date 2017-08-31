package com.bignerdranch.android.typesetter;

class Font {

  private String fileName;
  private String displayName;

  public Font(String fileName) {
    this.fileName = fileName;
    displayName = cleanUpFileNameForDisplay(fileName);
  }

  private String cleanUpFileNameForDisplay(String fileName) {
    fileName = fileName.replace("-", " ");
    fileName = fileName.replace(".ttf", "");
    fileName = fileName.replace(".otf", "");
    return fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
