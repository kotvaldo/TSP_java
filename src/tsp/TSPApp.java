package tsp;

import javax.swing.*;

public class TSPApp {

    private TSPView mainView;

    public void startup() {
        SwingUtilities.invokeLater(() -> {
            mainView = new TSPView();
            mainView.setVisible(true);
        });
    }

    public static void main(String[] args) {
        TSPApp app = new TSPApp();
        app.startup();
    }
}
