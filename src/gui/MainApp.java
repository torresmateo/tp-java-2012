package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class MainApp extends JFrame {
	
	JPanel basic;

    public MainApp() {
        initUI();
    }

    public final void initUI() {
    	
    	basic = new JPanel(new BorderLayout());
    	
        JToolBar toolbar = new JToolBar();
        
        ImageIcon addIcon = new ImageIcon(getClass().getResource("add.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("exit.png"));
        
        JButton addButton = new JButton(addIcon);
        addButton.setToolTipText("Add server configuration");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setToolTipText("Exit");
             
        toolbar.add(addButton);
        toolbar.add(exitButton);
        
        //Anonymous inner class
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
            	int opt = JOptionPane.showConfirmDialog(basic,"Are you sure to quit?",
            		 "Exit",JOptionPane.YES_NO_OPTION);
            	if(opt==0)
                   System.exit(0);
            }

        });
        
        add(basic);

        basic.add(toolbar, BorderLayout.NORTH);

        setTitle("Service monitoring");
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