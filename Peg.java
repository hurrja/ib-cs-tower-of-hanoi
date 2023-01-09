public enum Peg
{
  A("From"), B("Intermediate"), C("To");

  private String defaultLabel;

  public String getDefaultLabel() {
    return this.defaultLabel;
  }

  Peg(String label) {
    defaultLabel = label;
  }
}
