package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class MainApp extends JFrame {

    public MainApp() {
        initUI();
    }

    public final void initUI() {

        JToolBar toolbar = new JToolBar();
        
        ImageIcon addIcon = new ImageIcon(getClass().getResource("add.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("exit.png"));
        
        JButton addButton = new JButton(addIcon);
        addButton.setToolTipText("Add server configuration");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setToolTipText("Exit");
             
        toolbar.add(addButton);
        toolbar.add(exitButton);
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	 SwingUtilities.invokeLater(new Runnable() {

                     public void run() {
                         AddServerDialog asd = new AddServerDialog();
                         asd.setVisible(true);
                     }
                 });
            }

        });
        
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }

        });

        add(toolbar, BorderLayout.NORTH);

        setTitle("Monitoreo de Servicios");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainApp w = new MainApp();
                w.setVisible(true);
            }
        });
    }
}