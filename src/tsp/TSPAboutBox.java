package tsp;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class TSPAboutBox extends JDialog {

    public TSPAboutBox(Frame parent) {
        super(parent, "About: TSP Solver", true);
        initComponents();
    }

    private void initComponents() {
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JLabel appTitleLabel = new JLabel("TSP Solver");
        appTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel appDescLabel = new JLabel("<html>This application solves the Traveling Salesman Problem (TSP).<br>"
                + "It demonstrates TSP visualization and solution approaches.</html>");

        JLabel versionLabel = new JLabel("Product Version:");
        JLabel appVersionLabel = new JLabel("1.0");

        JLabel vendorLabel = new JLabel("Vendor:");
        JLabel appVendorLabel = new JLabel("Your Company");

        JLabel homepageLabel = new JLabel("Homepage:");
        JLabel appHomepageLabel = new JLabel("<html><a href='http://example.com'>http://example.com</a></html>");

        // Načítanie obrázka
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/tsp/resources/about.png"))));


        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.add(versionLabel);
        infoPanel.add(appVersionLabel);
        infoPanel.add(vendorLabel);
        infoPanel.add(appVendorLabel);
        infoPanel.add(homepageLabel);
        infoPanel.add(appHomepageLabel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(appTitleLabel, BorderLayout.NORTH);
        mainPanel.add(appDescLabel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        getContentPane().add(imageLabel, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }
}
