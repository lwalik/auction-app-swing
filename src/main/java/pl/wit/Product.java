package pl.wit;

import javax.swing.*;
import java.io.Serializable;
import java.net.URL;

public class Product implements Serializable {
    private final String name;
    private final double buyNowPrice;
    private double currPrice;
    private final String currBuyer;
    private static final long serialVersionUID = -1L;
    private final String imagePath;
    private final int id;

    public Product(int id, String name, double currPrice, double buyNowPrice, String imagePath) {
        this.id = id;
        this.name = name;
        this.currPrice = currPrice;
        this.buyNowPrice = buyNowPrice;
        this.currBuyer = "";

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

    public double getCurrPrice() {
        return this.currPrice;
    }

    public void setCurrPrice(Double value) {
        this.currPrice = value;
    }

    public int getId() {
        return this.id;
    }

    public double getBuyNowPrice() {
        return this.buyNowPrice;
    }
}
