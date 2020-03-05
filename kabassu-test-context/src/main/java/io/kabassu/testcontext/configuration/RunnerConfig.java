package io.kabassu.testcontext.configuration;

public class RunnerConfig {

  private String address;

  private boolean servant;

  public RunnerConfig(String address, boolean servant) {
    this.address = address;
    this.servant = servant;
  }

  public String getAddress() {
    return address;
  }

  public boolean getServant() {
    return servant;
  }
}
