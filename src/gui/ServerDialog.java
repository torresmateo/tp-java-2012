package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import parser.Parser;

import utils.ServerProperties;


public class ServerDialog extends JDialog{
	
	 JPanel basic;  //panel principal de la App
	 
	 JLabel hintTitle; //hint title
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
     
     //panel de botones
     JPanel bottom;
     
     //Hashmap para almacenar la información del formulario
     HashMap<ServerProperties, String> data;
     
     boolean new_save;

    public ServerDialog(JFrame father) {
    	//ventana modal
    	super(father,true);
        initUI();
        
    }

    public void initUI() {

        basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);

        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setMaximumSize(new Dimension(450, 0));
        hintTitle = new JLabel("");
        hintTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));
        topPanel.add(hintTitle);

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
        tfaddress = new JTextField("",15);
        JLabel lalias = new JLabel("Alias:");
        tfalias = new JTextField("",15);
        JLabel lcheckint = new JLabel("Check interval:");
        tfcheckint = new JTextField("",15);
        JLabel lcurrentstate = new JLabel("Current state:");
        tfcurrentstate = new JTextField("",15);
        JLabel lemailnotif = new JLabel("Email notification:");
        tfemailnotif = new JTextField("",15);
        JLabel lhostname = new JLabel("Host name:");
        tfhostname = new JTextField("",15);
        JLabel llastcheck = new JLabel("Last check:");
        tflastcheck = new JTextField("",15);
        JLabel llastnotif = new JLabel("Last notification:");
        tflastnotif = new JTextField("",15);
        JLabel lmaxcheckattempts = new JLabel("Max check attempts:");
        tfmaxcheckattempts = new JTextField("",15);
        JLabel lnotifinterval = new JLabel("Notification interval:");
        tfnotifinterval = new JTextField("",15);
        JLabel lportslist = new JLabel("Ports list:");
        tfportslist = new JTextField("",15);
        JLabel lretryint = new JLabel("Retry interval:");
        tfretryint = new JTextField("",15);
        JLabel ltolerance = new JLabel("Tolerance attempts	:");
        tftolerance = new JTextField("",15);
        
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
        bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        basic.add(bottom);

        setTitle("");
        setSize(new Dimension(450, 600));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public void cleanFields(){
          tfaddress.setText("");
          tfalias.setText("");
          tfcheckint.setText("");
          tfcurrentstate.setText("");
          tfemailnotif.setText("");
          tfhostname.setText("");
          tflastcheck.setText("");
          tflastnotif.setText("");
          tfmaxcheckattempts.setText("");
          tfnotifinterval.setText("");
          tfportslist.setText("");
          tfretryint.setText("");
          tftolerance.setText("");
    }
    
    /*
     * Funciones para comunicación con ventana padre
     */
    
    public Properties newServerProp(){
    	if(new_save){
    		String server_file = MainApp.getDIR_PATH() + data.get(ServerProperties.ALIAS)
    				+".properties";	
    		Parser p = new Parser(server_file);
			return p.readProperties();
    	}
    	return null;
    }
    
    public String newServerName(){
    	if(new_save){
    		String server_file = data.get(ServerProperties.ALIAS)+".properties";	
    		return server_file;
    	}
    	return null;
    }
    
}