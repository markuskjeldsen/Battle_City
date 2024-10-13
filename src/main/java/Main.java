import view.GamePanel;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Battle City");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);


        window.pack();


        window.setLocationRelativeTo(null);
        window.setVisible(true);



        gamePanel.starGameThread();
    }
}