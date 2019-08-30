package io.kabassu.commons.checks;


import java.util.Arrays;

public final class FilesDownloadChecker {

  private static final String []  REQUIRED = {"git"};

  public static boolean checkIfFilesRetrieveIsRequired(String locationType){
    return Arrays.stream(REQUIRED).anyMatch(type -> type.equals(locationType));
  }

  private FilesDownloadChecker(){
    //for sonar
  }

}
