package esgi.fyc.model.money;

public class Currency {
    private final String name;
    private final String symbol;

    public Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
