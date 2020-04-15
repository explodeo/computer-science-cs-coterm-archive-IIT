package morcom.christopher.stockwatch;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable, Comparable<Stock>{

    public Stock(String s, String cn, double lp, double cng, double cp){
        this.symbol = s;
        this.companyName = cn;
        this.latestPrice = lp;
        this.change = cng;
        this.changepercent = cp;
    }

    private String symbol;
    private String companyName;
    private double latestPrice, change, changepercent;

    public double getChange() {
        return change;
    }
    public double getChangepercent() {
        return changepercent;
    }
    public double getLatestPrice() {
        return latestPrice;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setChange(double change) {
        this.change = change;
    }
    public void setChangepercent(double changepercent) {
        this.changepercent = changepercent;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public int compareTo(@NonNull Stock other){
        return this.getSymbol().compareTo(other.getSymbol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock s = (Stock) o;
        return getSymbol() != null ? getSymbol().equals(s.getSymbol()) : s.getSymbol() == null;
    }

    @Override
    public int hashCode() {
        return getSymbol() != null ? getSymbol().hashCode() : 0;
    }

    public  String toString(){
        return String.format("%s, %s, %f, %f, %f", symbol, companyName, latestPrice, change, changepercent);
    }
}
