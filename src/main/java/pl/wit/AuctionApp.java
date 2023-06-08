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
    private final Map<Integer, Product> products = new HashMap<>();
    private ObjectOutputStream outputStream;

    public static void main(String[] args) throws Exception {
        AuctionApp client = new AuctionApp();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setSize(350, 800);
        client.run();
    }

    public void run() throws IOException {
        Socket socket = new Socket("localhost", 9001);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        out.println(getName());
        System.out.println("Connected to server.");

        while (true) {
            try {
                Response response = (Response) inputStream.readObject();
                if (response.getStatusCode() == StatusCode.UPDATED.getCode()) {
                    products.clear();
                    products.putAll(response.getProducts());
                    System.out.println("Data updated!");
                }

                if (response.getStatusCode() == StatusCode.OK.getCode()) {
                    products.clear();
                    products.putAll(response.getProducts());
                    System.out.println("Data provided!");
                    frame.setTitle("Auction App " + response.getUserName());
                }
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            } finally {
                this.generateCards(products);
                frame.setVisible(true);
            }
        }
    }

    private void generateCards(Map<Integer, Product> list) {
        cards.removeAll();
        int idx = 0;
        for (Map.Entry<Integer, Product> entry : list.entrySet()) {
            Map<Integer, JButton> navButtons = createNavButtons(idx, list.size());
            JPanel navPanel = createButtonsPanel(navButtons);


            JPanel card = this.createCard(entry.getValue(), navPanel);

            cards.add(card);
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
        Map<Integer, JButton> auctionButtons = new HashMap<>();
        auctionButtons.put(1, new JButton(createBidAction(product)));
        auctionButtons.put(2, new JButton(createBuyNowAction(product)));
        return createButtonsPanel(auctionButtons);
    }

    private JPanel createButtonsPanel(Map<Integer, JButton> buttons) {
        JPanel buttonPanel = new JPanel();
        for (Map.Entry<Integer, JButton> entry : buttons.entrySet()) {
            JButton button = entry.getValue();
            buttonPanel.add(button);
        }

        return buttonPanel;
    }


    private Map<Integer, JButton> createNavButtons(int idx, int size) {
        Map<Integer, JButton> buttons = new HashMap<>();
        int i = 1;
        if (idx != 0) {
            buttons.put(i, new JButton(backAction));
            i++;
        }

        if (idx != size - 1) {
            buttons.put(i, new JButton(nextAction));
        }

        return buttons;
    }

    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter name:",
                "Login",
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

    private AbstractAction createBuyNowAction(Product product) {
        return new AbstractAction("Buy Now") {
            {
                setEnabled(product.getCurrPrice() != product.getBuyNowPrice());
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                product.buy();
                Request request = new Request("POST", product, "BUY_NOW");
                try {
                    outputStream.writeObject(request);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        };
    }

    private AbstractAction createBidAction(Product product) {
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
