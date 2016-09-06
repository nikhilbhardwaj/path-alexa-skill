package in.nikhilbhardwaj.path.route.model;

public enum StopNames {
  FOURTEEN_ST("14th Street"),
  TWENTY_THIRD_ST("23rd Street"),
  THIRTY_THIRD_ST("33rd Street"),
  NINTH_ST("9th Street"),
  CHRISTOPHER_ST("Christopher Street"),
  EXCHANGE_PL("Exchange Place"),
  GROVE_ST("Grove Street"),
  HARRISON("Harrison"),
  HOBOKEN("Hoboken"),
  JOURNAL_SQ("Journal Square"),
  NEWARK("Newark"),
  NEWPORT("Newport"),
  WTC("World Trade Center");

  private final String name;

  private StopNames(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static StopNames fromString(String text) {
    if (text != null) {
      for (StopNames b : StopNames.values()) {
        if (text.equalsIgnoreCase(b.getName())) {
          return b;
        }
      }
    }
    throw new IllegalArgumentException("No constant with text " + text + " found");
  }
}
