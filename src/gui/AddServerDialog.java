package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import parser.FileManager;

import utils.Properties;


public class AddServerDialog extends JDialog{
	
	 JPanel basic;  //panel principal de la App
	 //Campos del formulario
     JTextField tfaddress;
     JTextField tfalias;
     JTextField tfcheckint;
     JTextField tfcurrentstate;
     JTextField tfemailnotif;
     JTextField tfhostname;
     JTextField tflastcheck;
     JTextField tflastnotif;
     JTextField tfmaxcheckattempts;
     JTextField tfnotifinterval;
     JTextField tfportslist;
     JTextField tfretryint;
     JTextField tftolerance;
     //Hashmap para almacenar la información del formulario
     HashMap<Properties, String> data;
     //Variable para comunicar la creación de un nuevo archivo al frame padre
     boolean new_save;

    public AddServerDialog(JFrame father) {
    	//ventana modal
    	super(father,true);
    	data = new HashMap<Properties, String>();
    	new_save=false;
        initUI();
        
    }

    public final void initUI() {

        basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);

        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setMaximumSize(new Dimension(450, 0));
        JLabel hint = new JLabel("Server properties");
        hint.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));
        topPanel.add(hint);

        ImageIcon icon = new ImageIcon(getClass().getResource("server.png"));
        JLabel label = new JLabel(icon);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(label, BorderLayout.WEST);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.DARK_GRAY);

        topPanel.add(separator, BorderLayout.SOUTH);

        basic.add(topPanel);

        //Formulario
        JPanel PropPanel = new JPanel(new GridLayout(13, 2, 1, 5));
        PropPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel laddress = new JLabel("Address:");
        tfaddress = new JTextField("IP address",15);
        JLabel lalias = new JLabel("Alias:");
        tfalias = new JTextField("host alias",15);
        JLabel lcheckint = new JLabel("Check interval:");
        tfcheckint = new JTextField("unit time between controls",15);
        JLabel lcurrentstate = new JLabel("Current state:");
        tfcurrentstate = new JTextField("service current state",15);
        JLabel lemailnotif = new JLabel("Email notification:");
        tfemailnotif = new JTextField("email notification",15);
        JLabel lhostname = new JLabel("Host name:");
        tfhostname = new JTextField("hostname",15);
        JLabel llastcheck = new JLabel("Last check:");
        tflastcheck = new JTextField("last time check successfull",15);
        JLabel llastnotif = new JLabel("Last notificacion:");
        tflastnotif = new JTextField("last successfull notification",15);
        JLabel lmaxcheckattempts = new JLabel("Max check attempts:");
        tfmaxcheckattempts = new JTextField("number of times to retry check",15);
        JLabel lnotifinterval = new JLabel("Notification interval:");
        tfnotifinterval = new JTextField("how often send notif. failure",15);
        JLabel lportslist = new JLabel("Ports list:");
        tfportslist = new JTextField("Ports that will be verified",15);
        JLabel lretryint = new JLabel("Retry interval:");
        tfretryint = new JTextField("minutes to wait before scheduling" +
        		" a new check",15);
        JLabel ltolerance = new JLabel("Tolerance attempts	:");
        tftolerance = new JTextField("times will wait before sending a " +
        		"notice",15);
        
        PropPanel.add(laddress); PropPanel.add(tfaddress);
        PropPanel.add(lalias);   PropPanel.add(tfalias);
        PropPanel.add(lcheckint);PropPanel.add(tfcheckint);
        PropPanel.add(lcurrentstate);PropPanel.add(tfcurrentstate);
        PropPanel.add(lemailnotif);PropPanel.add(tfemailnotif);
        PropPanel.add(lhostname);PropPanel.add(tfhostname);
        PropPanel.add(llastcheck);PropPanel.add(tflastcheck);
        PropPanel.add(llastnotif);PropPanel.add(tflastnotif);
        PropPanel.add(lmaxcheckattempts);PropPanel.add(tfmaxcheckattempts);
        PropPanel.add(lnotifinterval);PropPanel.add(tfnotifinterval);
        PropPanel.add(lportslist);PropPanel.add(tfportslist);
        PropPanel.add(lretryint);PropPanel.add(tfretryint);
        PropPanel.add(ltolerance);PropPanel.add(tftolerance);

        basic.add(PropPanel);

        //Botones
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton save = new JButton("Save");
        save.setMnemonic(KeyEvent.VK_S);
        JButton close = new JButton("Close");
        close.setMnemonic(KeyEvent.VK_C);
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	data.put(Properties.ADDRESS,tfaddress.getText());
            	data.put(Properties.ALIAS,tfalias.getText());
            	data.put(Properties.CHECK_INTERVAL,tfcheckint.getText());
            	data.put(Properties.CURRENT_STATE,tfcurrentstate.getText());
            	data.put(Properties.EMAIL_NOTIF,tfemailnotif.getText());
            	data.put(Properties.HOSTNAME,tfhostname.getText());
            	data.put(Properties.LAST_CHECK,tflastcheck.getText());
            	data.put(Properties.LAST_NOTIF,tflastnotif.getText());
            	data.put(Properties.MAX_CHECK_ATTEMPTS,tfmaxcheckattempts.getText());
            	data.put(Properties.NOTIF_INTERVAL,tfnotifinterval.getText());
            	data.put(Properties.PORTS_LIST,tfportslist.getText());
            	data.put(Properties.RETRY_INTERVAL,tfretryint.getText());
            	data.put(Properties.TOLERANCE_ATTEMPTS,tftolerance.getText());            	
            	FileManager fm = new FileManager(data);
            	if(fm.save(MainApp.DIR_PATH)){
	            	JOptionPane.showMessageDialog(basic, "It has been created successfully!",
	                        "Information", JOptionPane.INFORMATION_MESSAGE);
	            	new_save=true;
	            	dispose();
            	}
            	else{
            		 JOptionPane.showMessageDialog(basic, "File exists!",
                             "Error", JOptionPane.ERROR_MESSAGE);
            	}
            }
        });
        
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new_save=false;
            	dispose();
            }

        });

        bottom.add(save);
        bottom.add(Box.createRigidArea(new Dimension(5, 0)));
        bottom.add(close);
        bottom.add(Box.createRigidArea(new Dimension(15, 0)));
        basic.add(bottom);
        basic.add(Box.createRigidArea(new Dimension(0, 15)));

        bottom.setMaximumSize(new Dimension(450, 0));

        setTitle("Add new server configuration");
        setSize(new Dimension(450, 550));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public boolean newServerFile(){
    	return new_save;
    }
    
}