package tsp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TSPView extends JFrame {

    private JPanel mainPanel;
    private JTable jTable1;
    private JTextField solutionField;
    private JButton loadFileButton;
    private JButton solveButton;
    private JLabel statusMessageLabel;
    private JProgressBar progressBar;
    private TSP aTSP;

    public TSPView() {
        // Nastavenie hlavného okna
        setTitle("TSP Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Inicializácia GUI komponentov
        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());

        // Tabuľka na zobrazenie matice
        jTable1 = new JTable(new DefaultTableModel(new Object[]{"Dij"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(jTable1);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Ovládacie prvky
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loadFileButton = new JButton("Načítať súbor");
        solveButton = new JButton("Vyriešiť");
        solutionField = new JTextField(40);
        statusMessageLabel = new JLabel("Status: Pripravené");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        // Pridanie komponentov do spodného panela
        controlPanel.add(loadFileButton);
        controlPanel.add(solveButton);
        controlPanel.add(new JLabel("Riešenie:"));
        controlPanel.add(solutionField);
        controlPanel.add(statusMessageLabel);
        controlPanel.add(progressBar);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Vytvorenie menu baru
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Pridanie hlavného panela
        add(mainPanel);

        // Akcie tlačidiel
        loadFileButton.addActionListener(new LoadFileAction());
        solveButton.addActionListener(new SolveAction());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Súbor menu
        JMenu fileMenu = new JMenu("Súbor");
        JMenuItem exitMenuItem = new JMenuItem("Ukončiť");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        // Pomoc menu
        JMenu helpMenu = new JMenu("Pomoc");
        JMenuItem aboutMenuItem = new JMenuItem("O aplikácii");
        aboutMenuItem.addActionListener(e -> showAboutBox());
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showAboutBox() {
        // Zobrazenie okna "O aplikácii"
        TSPAboutBox aboutBox = new TSPAboutBox(this);
        aboutBox.setVisible(true);
    }

    private void displaySolution(int M, int[] x) {
        // Zobrazenie výsledku riešenia
        StringBuilder text = new StringBuilder();
        text.append(x[0]);
        for (int i = 1; i < M; i++) {
            text.append(" -> ").append(x[i]);
        }
        text.append(" -> ").append(x[0]);
        solutionField.setText(text.toString());
    }

    public void initTable(int M, int[][] data) {
        // Inicializácia tabuľky s dátami
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);

        // Pridanie stĺpcov
        for (int i = 0; i <= M; i++) {
            model.addColumn(i == 0 ? " " : i);
        }

        // Pridanie riadkov
        for (int i = 0; i < M; i++) {
            Object[] row = new Object[M + 1];
            row[0] = i + 1;
            for (int j = 0; j < M; j++) {
                row[j + 1] = data[i][j];
            }
            model.addRow(row);
        }

        // Nastavenie šírky stĺpcov
        TableColumn column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(150);
        jTable1.setRowHeight(20);
    }

    // Akcia pre načítanie súboru
    private class LoadFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(TSPView.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                aTSP = new TSP();
                aTSP.read_file(file);
                initTable(aTSP.getM(), aTSP.getdata());
                statusMessageLabel.setText("Súbor načítaný úspešne.");
            }
        }
    }

    // Akcia pre vyriešenie TSP
    private class SolveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (aTSP == null) {
                JOptionPane.showMessageDialog(TSPView.this, "Najprv načítajte súbor.", "Chyba", JOptionPane.ERROR_MESSAGE);
                return;
            }

            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            statusMessageLabel.setText("Riešenie prebieha...");

            SwingUtilities.invokeLater(() -> {
                aTSP.ries();
                displaySolution(aTSP.getM(), aTSP.x);
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                statusMessageLabel.setText("Riešenie dokončené.");
            });
        }
    }


}
