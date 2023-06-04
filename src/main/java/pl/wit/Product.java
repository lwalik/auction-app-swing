package pl.wit;

import javax.swing.*;
import java.net.URL;

public class Product {
    private final String name;
    private final double buyNowPrice;
    private final ImageIcon image;
    private final double currPrice;
    private final String currBuyer;

    public Product(String name, double currPrice, double buyNowPrice, String imagePath) {
        this.name = name;
        this.currPrice = currPrice;
        this.buyNowPrice = buyNowPrice;
        this.currBuyer = "Jakiś chuj";

        URL url = this.getClass().getResource(imagePath);

        if (url != null) {
            this.image = new ImageIcon(url);
        } else {
            this.image = new ImageIcon("/images/pobrane.png");
        }
    }

    public String getName() {
        return this.name;
    }

    public String getCurrPriceAsString() {
        return Double.toString(this.currPrice);
    }

    public String getBuyNowPriceAsString() {
        return Double.toString(this.buyNowPrice);
    }

    public ImageIcon getImage() {
        return this.image;
    }

    public String getCurrBuyer() {
        return this.currBuyer;
    }

}
