package Service_Desk.BalPharma.location;

public enum Location {
    UNIT_1("Bommasandara", "Unit 1"),
    UNIT_2("Bommasandara", "Unit 2"),
    UNIT_4("Rudrapur",     "Unit 4"),
    UNIT_5("Sangli",       "Unit 5"),
    UNIT_6("Udaipur",      "Unit 6"),
    CWH   ("Bommasandara", "CWH"),
    GGM   ("Gurugram",     "GGM");

    private final String city;
    private final String displayName;

    Location(String city, String displayName) {
        this.city = city;
        this.displayName = displayName;
    }

    public String getCity() {
        return city;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFullName() {
        return displayName + ": " + city;
    }
}