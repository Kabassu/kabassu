/*
 *   Copyright (C) 2018 Kabassu
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.kabassu.commons.checks;


import java.util.Arrays;

public final class FilesDownloadChecker {

  private static final String []  REQUIRED = {"git","none"};

  public static boolean checkIfFilesRetrieveIsRequired(String locationType){
    return Arrays.stream(REQUIRED).anyMatch(type -> type.equals(locationType));
  }

  private FilesDownloadChecker(){
    //for sonar
  }

}
