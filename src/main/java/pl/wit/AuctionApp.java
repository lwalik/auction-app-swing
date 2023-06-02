package pl.wit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Array;
import java.util.*;
import java.util.List;

public class AuctionApp {
    private JFrame frame = new JFrame("Auction App");
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    private List<String> products = Arrays.asList("Product 1", "Product 2", "Product 3", "Product 4");
    public AuctionApp() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);

        this.generateCards(products);
    }

    private void generateCards(List<String> list) {
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

        frame.getContentPane().add(cards, BorderLayout.CENTER);
    }

    private JPanel createCard(String message, JButton... buttons) {
        JPanel card = new JPanel(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        card.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    public void run(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
