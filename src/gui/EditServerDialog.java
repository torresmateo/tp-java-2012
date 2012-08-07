package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


import parser.FileManager;
import parser.Parser;

import utils.ServerProperties;


public class EditServerDialog extends ServerDialog{
	
     MainApp father;
     Properties serverProp;

    public EditServerDialog(MainApp father) {
    	//ventana modal
    	super((JFrame)father);
    	this.father=father;
    	data = new HashMap<ServerProperties, String>();
    	new_save=false;
        initEditUI();
        
    }
    
    public void loadFields(){
    	String serverName = father.getLastSelectedServerName();
    	Hashtable<String, Properties> serverData = father.getServerData();
    	Properties serverProp = serverData.get(serverName);
    	tfaddress.setText((String) serverProp.get("address"));
    	tfalias.setText((String) serverProp.get("alias"));
    	tfcheckint.setText((String) serverProp.get("check_interval"));
    	tfcurrentstate.setText((String) serverProp.get("current_state"));
        tfemailnotif.setText((String) serverProp.get("email_notification"));
        tfhostname.setText((String) serverProp.get("host_name"));
        tflastcheck.setText((String) serverProp.get("last_check"));
        tflastnotif.setText((String) serverProp.get("last_notification"));
        tfmaxcheckattempts.setText((String) serverProp.get("max_check_attempts"));
        tfnotifinterval.setText((String) serverProp.get("notification_interval"));
        tfportslist.setText((String) serverProp.get("ports_list"));
        tfretryint.setText((String) serverProp.get("retry_interval"));
        tftolerance.setText((String) serverProp.get("tolerance_attempts"));
    }

    public final void initEditUI() {

    	hintTitle.setText("Edit Server properties");
    	
        JButton update = new JButton("Update");
        update.setMnemonic(KeyEvent.VK_S);
        JButton close = new JButton("Close");
        close.setMnemonic(KeyEvent.VK_C);
        
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
            	if(FileManager.update(father.getLastSelectedServerName(),data)){
	            	JOptionPane.showMessageDialog(basic, "It has been updated successfully!",
	                        "Information", JOptionPane.INFORMATION_MESSAGE);
	            	new_save=true;
	            	cleanFields();
	            	dispose();
            	}
            	else{
            		 JOptionPane.showMessageDialog(basic, "Another server has the same alias",
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

        bottom.add(update);
        bottom.add(Box.createRigidArea(new Dimension(5, 0)));
        bottom.add(close);
        bottom.add(Box.createRigidArea(new Dimension(15, 0)));
        
        basic.add(Box.createRigidArea(new Dimension(0, 25)));

        bottom.setMaximumSize(new Dimension(450, 0));

        setTitle("Edit server configuration");
    }
    
}