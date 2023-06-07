package pl.wit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class AuctionApp {
    private final JFrame frame = new JFrame("Auction App");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private Map<Integer, Product> products = new HashMap<>();
    private PrintWriter out;
    private ObjectInputStream inputStream;

    public static void main(String[] args) throws Exception {
        AuctionApp client = new AuctionApp();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setSize(300, 700);
        client.run();
    }

    public void run() throws IOException {
        Socket socket = new Socket("localhost", 9001);
        out = new PrintWriter(socket.getOutputStream(), true);
        inputStream = new ObjectInputStream(socket.getInputStream());

        out.println(getName());
        System.out.println("Connected to server.");

        while (true) {
            if (products != null && this.updateProducts()) {
                this.generateCards(products);
                frame.setVisible(true);
            }

        }
    }

    public boolean updateProducts() {
        try {
            Map<Integer, Product> response = (Map<Integer, Product>) inputStream.readObject();
            if (!products.equals(response)) {
                products = response;
                System.out.println("Aktualizacja: " + products);
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }

        return false;
    }

    private void generateCards(Map<Integer, Product> list) {
        cards.removeAll();
        int idx = 0;
        for (Map.Entry<Integer, Product> entry : list.entrySet()) {
            System.out.println("Produkt: " + entry.getValue().getName());

            Set<JButton> navButtons = createNavButtons(idx, list.size());
            JPanel navPanel = createButtonsPanel(navButtons.toArray(new JButton[0]));


            JPanel card = this.createCard(entry.getValue(), navPanel);

            cards.add(card, "Page " + idx);
            idx++;
        }

        cards.revalidate();
        cards.repaint();

        frame.getContentPane().add(cards);
    }

    private JPanel createCard(Product product, JPanel navPanel) {
        JPanel card = new JPanel(new GridLayout(3, 1));

        JLabel image = new JLabel(product.getImage(), SwingConstants.CENTER);
        JLabel name = new JLabel("Nazwa: " + product.getName(), SwingConstants.CENTER);
        JLabel buyNowPrice = new JLabel("Cena (Kup Teraz): " + product.getBuyNowPriceAsString(), SwingConstants.CENTER);
        JLabel currPrice = new JLabel("Aktualna cena (Licytacja): " + product.getCurrPriceAsString(), SwingConstants.CENTER);
        JLabel currBuyer = new JLabel("Aktualny kupujący: " + product.getCurrBuyer(), SwingConstants.CENTER);


        JPanel info = new JPanel(new GridLayout(4, 1));
        JPanel imagePanel = new JPanel();
        imagePanel.add(image);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));


        info.add(name);
        info.add(buyNowPrice);
        info.add(currPrice);
        info.add(currBuyer);

        buttonsPanel.add(createAuctionButtonsPanel());
        buttonsPanel.add(navPanel);

        card.add(imagePanel);
        card.add(info);
        card.add(buttonsPanel);


        return card;
    }

    private JPanel createAuctionButtonsPanel() {
        Set<JButton> auctionButtons = new HashSet<JButton>();
        auctionButtons.add(new JButton(bidAction));
        auctionButtons.add(new JButton(buyNowAction));
        return createButtonsPanel(auctionButtons.toArray(new JButton[0]));
    }

    private JPanel createButtonsPanel(JButton... buttons) {
        JPanel buttonPanel = new JPanel();
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }
        ;

        return buttonPanel;
    }


    private Set<JButton> createNavButtons(int idx, int size) {
        Set<JButton> buttons = new HashSet<JButton>();
        if (idx != 0) {
            buttons.add(new JButton(backAction));
        }

        if (idx != size - 1) {
            buttons.add(new JButton(nextAction));
        }

        return buttons;
    }

    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Choose a screen name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);
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
            out.println("accepted");
        }
    };

    AbstractAction buyNowAction = new AbstractAction("Buy Now") {
        @Override
        public void actionPerformed(ActionEvent e) {
            out.println("BUY_NOW");
        }
    };

    AbstractAction bidAction = new AbstractAction("Bid") {
        @Override
        public void actionPerformed(ActionEvent e) {
            out.println("BID");
        }
    };
}
