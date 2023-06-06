package pl.wit;

import javax.swing.*;
import java.io.Serializable;
import java.net.URL;

public class Product implements Serializable {
    private final String name;
    private final double buyNowPrice;
    private final double currPrice;
    private final String currBuyer;
    private static final long serialVersionUID = -1L;
    private final String imagePath;

    public Product(String name, double currPrice, double buyNowPrice, String imagePath) {
        this.name = name;
        this.currPrice = currPrice;
        this.buyNowPrice = buyNowPrice;
        this.currBuyer = "Jakiś chuj";

        URL url = this.getClass().getResource(imagePath);

        if (url != null) {
            this.imagePath = imagePath;
        } else {
            this.imagePath = "/images/pobrane.png";
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
        URL url = this.getClass().getResource(imagePath);

        if (url != null) {
            return new ImageIcon(url);
        } else {
            return new ImageIcon("/images/pobrane.png");
        }
    }

    public String getCurrBuyer() {
        return this.currBuyer;
    }

}
