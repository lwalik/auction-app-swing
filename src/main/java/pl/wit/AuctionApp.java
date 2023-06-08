package pl.wit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuctionApp {
    private final JFrame frame = new JFrame("Auction App");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private Map<Integer, Product> products = new HashMap<>();
    private PrintWriter out;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

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
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        out.println(getName());
        System.out.println("Connected to server.");

        while (true) {
            try {
                Response response = (Response) inputStream.readObject();
                if (response.getStatusCode() == StatusCode.UPDATED.getCode()) {
                    products.clear();
                    products.putAll(response.getProducts());
                    System.out.println("Aktualizacja: " + products);
                }

                if (response.getStatusCode() == StatusCode.OK.getCode()) {
                    products.clear();
                    products.putAll(response.getProducts());
                    System.out.println("Pierwszy raz: " + products);
                }
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            } finally {
                this.generateCards(products);
                frame.setVisible(true);
            }
        }
    }

    public boolean updateProducts() {
        try {
            Response response = (Response) inputStream.readObject();
            if (response.getStatusCode() == StatusCode.UPDATED.getCode()) {
                products = response.getProducts();

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

        buttonsPanel.add(createAuctionButtonsPanel(product));
        buttonsPanel.add(navPanel);

        card.add(imagePanel);
        card.add(info);
        card.add(buttonsPanel);


        return card;
    }

    private JPanel createAuctionButtonsPanel(Product product) {
        Set<JButton> auctionButtons = new HashSet<>();
        auctionButtons.add(new JButton(createBidAction(product)));
        auctionButtons.add(new JButton(buyNowAction));
        return createButtonsPanel(auctionButtons.toArray(new JButton[0]));
    }

    private JPanel createButtonsPanel(JButton... buttons) {
        JPanel buttonPanel = new JPanel();
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        return buttonPanel;
    }


    private Set<JButton> createNavButtons(int idx, int size) {
        Set<JButton> buttons = new HashSet<>();
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
        }
    };

    AbstractAction buyNowAction = new AbstractAction("Buy Now") {
        @Override
        public void actionPerformed(ActionEvent e) {
            out.println("BUY_NOW");
            out.println("Test");
        }
    };

    private AbstractAction createBidAction(Product product) {
        System.out.println("Produkty: " + product.getName() + " currPrice: " + product.getCurrPrice());
        System.out.println("------------------------------------");
        double diff = product.getBuyNowPrice() - product.getCurrPrice();
        double offer = diff >= 20 ? product.getCurrPrice() + 20 : product.getBuyNowPrice();
        String label = "Bid " + offer;
        return new AbstractAction(label) {
            {
                setEnabled(offer != product.getBuyNowPrice());
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                product.setCurrPrice(offer);
                Request request = new Request("POST", product, "BID");
                try {
                    outputStream.writeObject(request);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        };
    }
}
