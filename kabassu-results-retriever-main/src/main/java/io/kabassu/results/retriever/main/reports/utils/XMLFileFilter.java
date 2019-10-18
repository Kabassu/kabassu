package io.kabassu.results.retriever.main.reports.utils;

import java.io.File;
import java.io.FileFilter;
import org.apache.commons.lang3.StringUtils;

public class XMLFileFilter implements FileFilter {

  @Override
  public boolean accept(File pathname) {
    return pathname.isFile() && StringUtils.endsWith(pathname.getAbsolutePath().toLowerCase(),".xml");
  }
}
