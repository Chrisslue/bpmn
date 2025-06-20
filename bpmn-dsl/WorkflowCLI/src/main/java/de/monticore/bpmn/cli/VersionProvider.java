/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cli;

import java.net.URL;
import java.util.Properties;

/**
 * Returns the version information for the BPMN language.
 *
 * @see picocli.CommandLine.IVersionProvider implementation that returns version information from a
 * {@code /build-info.properties} file in the classpath.
 */
public class VersionProvider implements de.monticore.bpmn.cli.CommandLine.IVersionProvider {
  
  public String[] getVersion() throws Exception {
    URL url = getClass().getResource("/build-info.properties");
    if (url == null) {
      return new String[] { "No build-info.properties file found in the classpath." };
    }
    Properties properties = new Properties();
    properties.load(url.openStream());
    return new String[] { properties.getProperty("build.name") + " v" + properties.getProperty(
        "build.version") + " (Build timestamp: " + properties.getProperty("build.timestamp")
        + ")" };
  }
  
}
