package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import parser.FileManager;
import parser.Parser;

import utils.ServerProperties;


public class AddServerDialog extends ServerDialog{
	

    public AddServerDialog(JFrame father) {
    	//ventana modal
    	super(father);
    	data = new HashMap<ServerProperties, String>();
    	new_save=false;
        initAddUI();
        
    }

    public final void initAddUI() {

    	hintTitle.setText("Add Server properties");
    	
        JButton save = new JButton("Save");
        save.setMnemonic(KeyEvent.VK_S);
        JButton close = new JButton("Close");
        close.setMnemonic(KeyEvent.VK_C);
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String msg;
            	if((msg=emptyFields())!=null){
            		JOptionPane.showMessageDialog(basic, msg,
	                        "Error", JOptionPane.ERROR_MESSAGE);
            		return;
            	}
            	data.put(ServerProperties.ADDRESS,tfaddress.getText());
            	data.put(ServerProperties.ALIAS,tfalias.getText());
            	data.put(ServerProperties.CHECK_INTERVAL,tfcheckint.getText());
            	data.put(ServerProperties.CURRENT_STATE,tfcurrentstate.getText());
            	data.put(ServerProperties.EMAIL_NOTIF,tfemailnotif.getText());
            	data.put(ServerProperties.HOSTNAME,tfhostname.getText());
            	data.put(ServerProperties.LAST_CHECK,tflastcheck.getText());
            	data.put(ServerProperties.LAST_NOTIF,tflastnotif.getText());
            	data.put(ServerProperties.MAX_CHECK_ATTEMPTS,tfmaxcheckattempts.getText());
            	data.put(ServerProperties.NOTIF_INTERVAL,tfnotifinterval.getText());
            	data.put(ServerProperties.PORTS_LIST,tfportslist.getText());
            	data.put(ServerProperties.RETRY_INTERVAL,tfretryint.getText());
            	data.put(ServerProperties.TOLERANCE_ATTEMPTS,tftolerance.getText());            	
            	if(FileManager.save(data)){
	            	JOptionPane.showMessageDialog(basic, "It has been created successfully!",
	                        "Information", JOptionPane.INFORMATION_MESSAGE);
	            	new_save=true;
	            	cleanFields();
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
            	cleanFields();
            	dispose();
            }

        });

        bottom.add(save);
        bottom.add(Box.createRigidArea(new Dimension(5, 0)));
        bottom.add(close);
        bottom.add(Box.createRigidArea(new Dimension(15, 0)));
        
        basic.add(Box.createRigidArea(new Dimension(0, 30)));

        bottom.setMaximumSize(new Dimension(450, 0));

        setTitle("Add new server configuration");
    }
    
}