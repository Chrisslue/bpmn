/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TimerMessages {
  
  public static TimerMessages TEMP = new TimerMessages(Bundle.TEMP.getBundle());
  public static TimerMessages CRON = new TimerMessages(Bundle.CRON.getBundle());
  
  private final ResourceBundle resourceBundle;
  
  private TimerMessages(final String bundleName) {
    this.resourceBundle = ResourceBundle.getBundle(bundleName);
  }
  
  public String err(String key) {
    try {
      return key + " " + resourceBundle.getString(key);
    }
    catch (MissingResourceException e) {
      return key + " -";
    }
  }
  
  public String err(String key, Object... params) {
    try {
      return key + " " + MessageFormat.format(resourceBundle.getString(key), params);
    }
    catch (MissingResourceException e) {
      return key + " -";
    }
  }
  
  private enum Bundle {
    
    TEMP("de.monticore.bpmn.timerconditions.messages"), CRON(
        "de.monticore.timer.cronexpressions.messages");
    
    private final String bundle;
    
    Bundle(final String str) {
      this.bundle = str;
    }
    
    public String getBundle() { return bundle; }
    
    @Override
    public String toString() {
      return bundle;
    }
    
  }
  
}
