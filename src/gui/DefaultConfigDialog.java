package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import parser.Parser;

import database.Conector;
import database.DBInterface;
import database.SysVar;


import utils.DefaultConfig;
import utils.ServerProperties;

public class DefaultConfigDialog extends JDialog {

	JPanel basic;  //panel principal de la App
	 
	JLabel hintTitle; //hint title
	 //Campos del formulario
	JTextField tfDBipaddr;
    JTextField tfDBname;
    JTextField tfDBuser;
    JTextField tfDBpass;
    JTextField tfDirPath;
    
    //panel de botones
    JPanel bottom;
    
    //Hashmap para almacenar la información del formulario
    HashMap<DefaultConfig, String> data;
    
    MainApp father;
    boolean update_path;
    
    public DefaultConfigDialog(MainApp father) {
    	//ventana modal
    	super((JFrame)father,true);
    	this.father = father;
    	data = new HashMap<DefaultConfig, String>();
    	update_path=false;
        initUI();     
    }
    
    public void initUI() {

        basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);

        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setMaximumSize(new Dimension(450, 0));
        hintTitle = new JLabel("Default Configuration Parameters");
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
        JPanel PropPanel = new JPanel(new GridLayout(5, 2, 1, 5));
        PropPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel lDBipaddr = new JLabel("Database IP address:");
        tfDBipaddr = new JTextField("",15);
        JLabel lDBname = new JLabel("Database Name:");
        tfDBname = new JTextField("",15);
        JLabel lDBuser = new JLabel("Database Username:");
        tfDBuser = new JTextField("",15);
        JLabel lDBpass = new JLabel("Database Password:");
        tfDBpass = new JTextField("",15);
        JLabel lDirPath = new JLabel("Properties Dir_Path:");
        tfDirPath = new JTextField("",15);
        
        PropPanel.add(lDBipaddr); PropPanel.add(tfDBipaddr);
        PropPanel.add(lDBname); PropPanel.add(tfDBname);
        PropPanel.add(lDBuser);   PropPanel.add(tfDBuser);
        PropPanel.add(lDBpass);   PropPanel.add(tfDBpass);
        PropPanel.add(lDirPath);PropPanel.add(tfDirPath);

        basic.add(PropPanel);

        //Botones
        bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton save = new JButton("Save");
        save.setMnemonic(KeyEvent.VK_S);
        JButton close = new JButton("Close");
        close.setMnemonic(KeyEvent.VK_C);
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String msg;
            	Hashtable<String, String> info = new Hashtable<String, String>();
            	
            	if((msg=emptyFields())!=null){
            		JOptionPane.showMessageDialog(basic, msg,
	                        "Error", JOptionPane.ERROR_MESSAGE);
            		return;
            	}
            	
            	File fichero = new File(tfDirPath.getText());	
            	if(!fichero.isDirectory()){
            		JOptionPane.showMessageDialog(basic, "Dir_Path must be a directory!",
	                        "Error", JOptionPane.ERROR_MESSAGE);
            		return;
            	}
            	
            	info.put("Driver", "org.postgresql.Driver");
            	info.put("Url","jdbc:postgresql://"+tfDBipaddr.getText()+
            			"/"+tfDBname.getText());
            	info.put("UserName", tfDBuser.getText());
            	info.put("Password", tfDBpass.getText());    

				try {
					Connection conPostgres = Conector.connectByHash(info);
					if(conPostgres.getAutoCommit()){
						JOptionPane.showMessageDialog(basic, "Database connection successful!",
		                        "Information", JOptionPane.INFORMATION_MESSAGE);
					}
					else{
						JOptionPane.showMessageDialog(basic, "Database connection failed!",
		                        "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(basic, "Database connection failed!\n" +
							e1.getMessage(),
	                        "Error", JOptionPane.ERROR_MESSAGE);
					return;
					//e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					System.out.println("Database Driver not found");
					e1.printStackTrace();			
				}
				
				File f = new File(MainApp.POSTGRES_PROPERTIES_PATH);
				if (f.exists()){
					f.delete();
				}
				Parser.writeConfiguration(info);
				if(father.getDefaulConfig()==false)
					insertDirPathToDB(tfDirPath.getText());
				else{
					if(MainApp.getDIR_PATH().compareTo(tfDirPath.getText())!=0){
						updateDirPathInDB(tfDirPath.getText());
					}else{
						update_path=false;
					}
				}
				dispose();
            }
        });
        
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(father.getDefaulConfig()==false){
	            	JOptionPane.showMessageDialog(basic, "The application will close!" +
	            			"\nYou must load default configuration values.",
	                        "Information", JOptionPane.INFORMATION_MESSAGE);
	            	System.exit(ABORT);
            	}
            	update_path=false;
            	dispose();
            }

        });
        
        bottom.add(save);
        bottom.add(Box.createRigidArea(new Dimension(5, 0)));
        bottom.add(close);
        bottom.add(Box.createRigidArea(new Dimension(15, 0)));
        bottom.setMaximumSize(new Dimension(450, 0));
        basic.add(bottom);
        basic.add(Box.createRigidArea(new Dimension(0, 15)));

        

        setTitle("Default Configuration Parameters");
        setSize(new Dimension(400, 350));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public boolean insertDirPathToDB(String dirPath){
    	DBInterface db = null;
    	try{		
    		Connection conPostgres = Conector.connectByFile(MainApp.POSTGRES_PROPERTIES_PATH);
    		db = new DBInterface(conPostgres);
    		db.insertSysVarObj(new SysVar("DIR_PATH", dirPath));    		
    	} catch (ClassNotFoundException e) {
    		System.out.println("Database Driver not found");
    		e.printStackTrace();
    	} catch (SQLException e) {
    		System.out.println("No se pudo conectar" + e.getMessage());
    		e.printStackTrace();
    	}
    	return true;
    }
    
    public boolean updateDirPathInDB(String dirPath){
    	DBInterface db = null;
    	try{		
    		Connection conPostgres = Conector.connectByFile(MainApp.POSTGRES_PROPERTIES_PATH);
    		db = new DBInterface(conPostgres);
    		db.deleteSysVar("name=\'DIR_PATH\'");
    		db.insertSysVarObj(new SysVar("DIR_PATH", dirPath)); 
    		MainApp.setDIR_PATH(dirPath);
    		update_path=true;
    	} catch (ClassNotFoundException e) {
    		System.out.println("Database Driver not found");
    		e.printStackTrace();
    	} catch (SQLException e) {
    		System.out.println("No se pudo conectar" + e.getMessage());
    		e.printStackTrace();
    	}
    	return true;
    }
    
    public String emptyFields(){
		
    	if(tfDBipaddr.getText().trim().length()==0){
    		return new String("Complete Database address");
    	}
    	if(tfDBname.getText().trim().length()==0){
    		return new String("Complete Database name");
    	}
    	if(tfDBuser.getText().trim().length()==0){
    		return new String("Complete Database username");
    	}
    	if(tfDBpass.getText().trim().length()==0){
    		return new String("Complete Database password");
    	}
    	if(tfDirPath.getText().trim().length()==0){
    		return new String("Complete Server Properties Directory Path");
    	}
    	return null;
    	
    }
    
    public void cleanFields(){
    	tfDBipaddr.setText("");
    	tfDBname.setText("");
        tfDBuser.setText("");
        tfDBpass.setText("");
        tfDirPath.setText("");
    }
    
    public void loadFields(){
    	Properties prop = Parser.readConfiguration();
    	String[] strList = prop.getProperty("Url").split("//");
    	String[] data = strList[1].split("/");
    	tfDBipaddr.setText(data[0]);
    	tfDBname.setText(data[1]);
    	tfDBuser.setText(prop.getProperty("UserName"));
    	tfDBpass.setText(prop.getProperty("Password"));
    	tfDirPath.setText(MainApp.getDIR_PATH());
    }
    
    public boolean getUpdatePath(){
    	return update_path;
    }
    
}
