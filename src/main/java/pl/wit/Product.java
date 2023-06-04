package pl.wit;

import javax.swing.*;
import java.net.URL;

public class Product {
    private String name;
    private double buyNowPrice;
    private ImageIcon image;
    private double currPrice;
    private String currBuyer;

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

    public double getCurrPrice() {
        return this.currPrice;
    }

    public String getCurrPriceAsString() {
        return Double.toString(this.currPrice);
    }

    public double getBuyNowPrice() {
        return this.buyNowPrice;
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
