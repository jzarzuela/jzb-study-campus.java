/****************************************************************************
 **
 ** This file is part of yFiles-2.5. 
 ** 
 ** yWorks proprietary/confidential. Use is subject to license terms.
 **
 ** Redistribution of this file or of an unauthorized byte-code version
 ** of this file is strictly forbidden.
 **
 ** Copyright (c) 2000-2007 by yWorks GmbH, Vor dem Kreuzberg 28, 
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ***************************************************************************/
package demo.browser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * TODO: add documentation
 *
 */
public class Demo implements Displayable
{
  String qualifiedName;
  String displayName;
  String summary;
  String description;
  String sourcePath;
  String source;
  URL base;
  boolean executable;

  Demo()
  {
    this.qualifiedName = "";
    this.displayName = "";
    this.description = "";
    this.sourcePath = "";
    this.source = null;
    this.executable = true;
  }

  public boolean isDemo()
  {
    return true;
  }

  public String getSourcePath()
  {
    return sourcePath;
  }

  public String getQualifiedName()
  {
    return qualifiedName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public String getSummary()
  {
    return summary;
  }

  public String getDescription()
  {
    return description;
  }

  public String getSource()
  {
    return source;
  }

  public boolean isExecutable()
  {
    return executable;
  }

  public URL getBase()
  {
    return base;
  }

  public String toString()
  {
    return "Demo[sourcePath=" + getSourcePath() +
           "; qualifiedName=" + getQualifiedName() +
           "; displayName=" + getDisplayName() +
           "; summary=" + getSummary() +
           "; description=" + getDescription() +
           "; base=" + getBase() +
           "; executable=" + isExecutable() + "]";
  }

  public String readSource()
  {
    final StringBuffer sb = new StringBuffer();
    URL sourceUrl = getClass().getClassLoader().getResource(getSourcePath());
    if (sourceUrl == null) {
      try { //source file not found. try wild heuristic
        StringTokenizer stok = new StringTokenizer(getSourcePath(), "/");
        int slashCount = stok.countTokens() - 1;
        String backPath = "";
        for(int i = 0; i < slashCount; i++) backPath += "../";
        sourceUrl = new URL(getBase(), backPath + "src/" + getSourcePath());
        if(!new File(sourceUrl.getPath()).exists()) sourceUrl = null;
      }catch(MalformedURLException mex) {}
    }
    if (sourceUrl != null) {
      try {
        BufferedReader br = null;
        try {
          br = new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
          br = new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
          String line;
          while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
          }
        } finally {
          if (br != null) {
            br.close();
          }
        }
      } catch (IOException ioe) {
        sb.setLength(0);
        sb.append(ioe.getMessage());
      }
    } else {
      sb.append("Could not locate file \"").append(getSourcePath()).append("\" in classpath.");
    }

    return sb.toString();
  }
}
