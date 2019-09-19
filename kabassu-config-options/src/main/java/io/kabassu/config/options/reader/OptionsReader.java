package io.kabassu.config.options.reader;

import io.kabassu.config.options.configuration.KabassuConfigOptionsConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class OptionsReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OptionsReader.class);

  private final Map<String, List<String>> availableOptions;

  private File modulesDirectory;

  private Map<String, Map<String, Object>> options = new HashMap<>();

  public OptionsReader(KabassuConfigOptionsConfiguration kabassuConfigOptionsConfiguration) {
    this.modulesDirectory = new File(kabassuConfigOptionsConfiguration.getModulesDir());
    this.availableOptions = kabassuConfigOptionsConfiguration.getAvailableOptions();
  }


  public Map<String, Map<String, Object>> read() {
    availableOptions.keySet().forEach(this::readFile);
    return options;
  }

  private void readFile(String file) {
    try {
      String jsonString = FileUtils.readFileToString(new File(modulesDirectory, file));
      JsonObject config = new JsonObject(jsonString).getJsonObject("config");
      availableOptions.get(file).forEach(option -> readOptions(config, option, file));
    } catch (IOException e) {
      LOGGER.error("Problem with reading file: " + file, e);
    }
  }

  private void readOptions(JsonObject config, String option, String file) {
    String[] splitPaths = StringUtils.split(option,".");
    Object value = config.getValue(splitPaths[0]);
    if (splitPaths.length > 1) {
      for (int i = 1; i < splitPaths.length; i++) {
        value = ((JsonObject) value).getValue(splitPaths[i]);
      }
    }
    addToOptions(file, option, value);
  }

  private void addToOptions(String file, String option, Object value) {
    if (!options.containsKey(file)) {
      options.put(file, new HashMap<>());
    }
    options.get(file).put(option, value);
  }
}
