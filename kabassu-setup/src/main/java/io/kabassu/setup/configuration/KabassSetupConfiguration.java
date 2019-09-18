package io.kabassu.setup.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class KabassSetupConfiguration {

  private List<User> users = new ArrayList<>();

  public KabassSetupConfiguration(JsonObject config) {
    prepareUsers(config.getJsonArray("users"));
  }

  private void prepareUsers(JsonArray users) {
    if (users != null) {
      users.stream().map(user -> (JsonObject) user).forEach(userJson ->
        this.users.add(
          new User(userJson.getString("username"), userJson.getString("password")))
      );
    }
  }

  public List<User> getUsers() {
    return users;
  }
}
