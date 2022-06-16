package model.helpers.AddressSearch;

public class SearchRegexs {
    private final static String STREET = "(?<street>[A-Za-zæøåÆØÅ0-9\\s\\.]+)";
    private final static String HOUSENUMBER = "(?<number>[0-9]{1,3}[A-Z]?)";
    private final static String POSTCODE = "(?<postcode>[0-9]{4})";
    private final static String CITY = "(?<city>[A-Za-zæøåÆØÅ\\s]+)";

    public static String getFullAddressRegex() {
        StringBuilder sb = new StringBuilder();
        sb.append(STREET).append(" ").append(HOUSENUMBER).append(", ");
        sb.append(POSTCODE).append(" ").append(CITY);
        return sb.toString();
    }

    public static String getAddressRegex() {
        StringBuilder sb = new StringBuilder();
        sb.append(STREET).append(", ").append(POSTCODE).append(" ").append(CITY);
        return sb.toString();
    }

    public static String getHouseRegex() {
        StringBuilder sb = new StringBuilder();
        sb.append(STREET).append(" ").append(HOUSENUMBER);
        return sb.toString();
    }
}
