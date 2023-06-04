package pl.wit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuctionApp {
    private final JFrame frame = new JFrame("Auction App");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final List<Product> products = Arrays.asList(
            new Product("Product 1", 100.00, 500.00, "/images/pobrane.png"),
            new Product("Product 2", 85.00, 170.00, "/images/apple-iphone-xs.jpg"),
            new Product("Product 3", 15.00, 45.00,  "/images/apple-iphone-xs.jpg")
    );
    public AuctionApp() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,800);

        this.generateCards(products);
    }

    public void run(){
        frame.setVisible(true);
    }

    private void generateCards(List<Product> list) {
        for (int i = 0; i < list.size(); i++) {
            List<JButton> buttons = new ArrayList<>();
            if (i != 0) {
                buttons.add(new JButton(backAction));
            }

            if (i != list.size() - 1) {
                buttons.add(new JButton(nextAction));
            }

            JPanel card = buttons.size() > 1
                    ? this.createCard(list.get(i), buttons.get(0), buttons.get(1))
                    : this.createCard(list.get(i), buttons.get(0));
            cards.add(card, "Page " + i);
        }

        frame.getContentPane().add(cards);
    }

    private JPanel createCard(Product product, JButton... buttons) {
        JPanel card = new JPanel(new GridLayout(3,2));

        JLabel image = new JLabel(product.getImage(), SwingConstants.CENTER);
        JLabel name = new JLabel("Nazwa: " + product.getName(), SwingConstants.CENTER);
        JLabel buyNowPrice = new JLabel("Cena (Kup Teraz): " + product.getBuyNowPriceAsString(), SwingConstants.CENTER);
        JLabel currPrice = new JLabel("Aktualna cena (Licytacja): " + product.getCurrPriceAsString(), SwingConstants.CENTER);
        JLabel currBuyer = new JLabel("Aktualny kupujący: " + product.getCurrBuyer(), SwingConstants.CENTER);

        JPanel info = new JPanel(new GridLayout(4,1));

        card.add(image);

        info.add(name);
        info.add(buyNowPrice);
        info.add(currPrice);
        info.add(currBuyer);

        card.add(info);

        card.add(createNavButtons(buttons));

        return card;
    }

    private JPanel createNavButtons(JButton... buttons) {
        JPanel buttonPanel = new JPanel();
        for (JButton button : buttons) {
            buttonPanel.add(button);
        };

        return buttonPanel;
    }

    AbstractAction nextAction = new AbstractAction("Next") {
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.next(cards);
        }
    };

    AbstractAction backAction = new AbstractAction("Back") {
        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.previous(cards);
        }
    };
}
