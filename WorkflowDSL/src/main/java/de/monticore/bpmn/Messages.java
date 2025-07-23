/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Utility class for instantiating log messages. */
public class Messages {
  
  // property file is: src/main/resources/de/monticore/workflow/messages.properties
  private static final String BUNDLE_NAME = "de.monticore.bpmn.messages";
  
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
  
  private Messages() {}
  
  public static String get(String key) {
    try {
      return key + " " + RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      return key + " -";
    }
  }
  
  public static String get(String key, Object... params) {
    try {
      return key + " " + MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
    }
    catch (MissingResourceException e) {
      return key + " -";
    }
  }
  
}
