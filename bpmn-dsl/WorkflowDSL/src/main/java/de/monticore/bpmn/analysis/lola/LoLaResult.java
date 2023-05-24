package de.monticore.bpmn.analysis.lola;

import com.google.gson.JsonObject;
import java.util.Map;

/** Contains the output produced by LoLA. Reflects only the most important attributes. */
public class LoLaResult {

  // for debugging only
  JsonObject rawResult;

  private Map<String, String> state;

  // private Collection<String> path;

  private Map<String, LoLaFile> files;

  private LoLaAnalysis analysis;

  public Map<String, String> getState() {
    return state;
  }

  /*
      public Collection<String> getPath() {
          return path;
      }
  */

  public Map<String, LoLaFile> getFiles() {
    return files;
  }

  public LoLaAnalysis getAnalysis() {
    return analysis;
  }

  public static class LoLaFile {
    private String filename;

    public String getFilename() {
      return filename;
    }
  }

  public static class LoLaAnalysis {
    private boolean result;

    private LoLaFormula formula;

    public boolean isResult() {
      return result;
    }

    public LoLaFormula getFormula() {
      return formula;
    }

    public static class LoLaFormula {
      private String type;

      private String parsed;

      public String getType() {
        return type;
      }

      public String getParsed() {
        return parsed;
      }
    }
  }
}
