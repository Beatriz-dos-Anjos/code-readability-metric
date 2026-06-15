// Expected score: 70-89
// Reason: Medium complexity string building and method chaining.
public class QueryBuilder {
    private String select;
    private String from;
    public QueryBuilder select(String s) { this.select = s; return this; }
    public QueryBuilder from(String f) { this.from = f; return this; }
    public String build() {
        if (select != null && from != null) {
            return "SELECT " + select + " FROM " + from;
        }
        return "";
    }
}
