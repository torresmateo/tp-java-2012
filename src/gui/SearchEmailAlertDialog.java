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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import utils.SearchParameters;

public class SearchEmailAlertDialog extends JDialog{
	
	JPanel basic;
	
	JLabel hintTitle; //hint title
	
	JTextField tfAlias;
	JTextField tfAddress;
	JTextField tfPuerto;
	JTextField tfEmail;
	JTextField tfEstado;
	JTextField tfFechaInicial;
	JTextField tfFechaFinal;
	
	JPanel bottom;
	
	//Hashmap para almacenar la información del formulario
    HashMap<SearchParameters, String> data;
	
    boolean searchPressed;
    
	public SearchEmailAlertDialog( JFrame father ){
		//ventana modal
    	super(father,true);
		
		data = new HashMap<SearchParameters, String>();
		searchPressed = false;
		
		basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);   
		
		JPanel topPanel = new JPanel(new BorderLayout(0, 0));
		topPanel.setMaximumSize(new Dimension(450, 0));
		hintTitle = new JLabel("");
        hintTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));
        topPanel.add(hintTitle);
        
        JLabel label = new JLabel(new ImageIcon(getClass().getResource("search.png")));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(label, BorderLayout.WEST);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.DARK_GRAY);
        
        topPanel.add(separator, BorderLayout.SOUTH);
        
        basic.add(topPanel);
        
		JPanel formPanel = new JPanel(new GridLayout(7, 2, 1, 5));
		formPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
		tfAlias = new JTextField("",15);
		tfAddress = new JTextField("",15);
		tfPuerto = new JTextField("",15);
		tfEmail = new JTextField("",15);
		tfEstado = new JTextField("",15);
		tfFechaInicial = new JTextField("",15);
		tfFechaFinal = new JTextField("",15);
		
		formPanel.add(new JLabel("Alias:"));
		formPanel.add(tfAlias);
		
		formPanel.add(new JLabel("Address:"));
		formPanel.add(tfAddress);
	   
		formPanel.add(new JLabel("Port:"));
		formPanel.add(tfPuerto);
	    
		formPanel.add(new JLabel("E-mail:")); 
		formPanel.add(tfEmail);
	    
		formPanel.add(new JLabel("Status:")); 
		formPanel.add(tfEstado);
	    
		formPanel.add(new JLabel("Init Time:"));
		formPanel.add(tfFechaInicial);
	    
		formPanel.add(new JLabel("End Time:")); 
		formPanel.add(tfFechaFinal);
		
		basic.add(formPanel);
		
		//Botones
        bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        basic.add(bottom);
        
        //JCalendar calEjemplo1 = new  JCalendar();
        //basic.add(calEjemplo1);
        
        setTitle("");
        setSize(new Dimension(450, 400));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        hintTitle.setText("Set Search Parameters");
		
		JButton searchButton = new JButton("Search");
		searchButton.setMnemonic(KeyEvent.VK_S);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	data.put(SearchParameters.ALIAS,tfAlias.getText());
            	data.put(SearchParameters.ADDRESS,tfAddress.getText());
            	data.put(SearchParameters.PUERTO,tfPuerto.getText());
            	data.put(SearchParameters.EMAIL,tfEmail.getText());
            	data.put(SearchParameters.ESTADO,tfEstado.getText());
            	data.put(SearchParameters.FECHAINICIAL,tfFechaInicial.getText());
            	data.put(SearchParameters.FECHAFINAL,tfFechaFinal.getText()); 
            	
            	searchPressed = true;
            	cleanFields();
            	dispose();
            	
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	searchPressed=false;
            	cleanFields();
            	dispose();
            }

        });
        
        bottom.add(searchButton);
        bottom.add(Box.createRigidArea(new Dimension(5, 0)));
        bottom.add(cancelButton);
        bottom.add(Box.createRigidArea(new Dimension(15, 0)));
        
        basic.add(Box.createRigidArea(new Dimension(0, 30)));

        bottom.setMaximumSize(new Dimension(450, 0));

        setTitle("Search Options");
	}
	
	public void cleanFields(){
		tfAlias.setText("");
		tfAddress.setText("");
		tfPuerto.setText("");
		tfEmail.setText("");
		tfEstado.setText("");
		tfFechaInicial.setText("");
		tfFechaFinal.setText("");
	}
	
	public String getWhereParameters(){
		String whereSQL = new String("");
		if(data.containsKey(SearchParameters.ALIAS) && !data.get(SearchParameters.ALIAS).equals("")){
			whereSQL += " alias = '"+data.get(SearchParameters.ALIAS)+"' AND ";
		}
		if(data.containsKey(SearchParameters.ADDRESS) && !data.get(SearchParameters.ADDRESS).equals("")){
			whereSQL += " direccion_ip = '"+data.get(SearchParameters.ADDRESS)+"' AND ";
		}
		if(data.containsKey(SearchParameters.PUERTO) && !data.get(SearchParameters.PUERTO).equals("")){
			whereSQL += " puerto = '"+data.get(SearchParameters.PUERTO)+"' AND ";
		}
		if(data.containsKey(SearchParameters.EMAIL) && !data.get(SearchParameters.EMAIL).equals("")){
			whereSQL += " email = '"+data.get(SearchParameters.EMAIL)+"' AND ";
		}
		if(data.containsKey(SearchParameters.ESTADO) && !data.get(SearchParameters.ESTADO).equals("")){
			whereSQL += " estado = '"+data.get(SearchParameters.ESTADO)+"' AND ";
		}
		if(data.containsKey(SearchParameters.FECHAINICIAL) && !data.get(SearchParameters.FECHAINICIAL).equals("")){
			whereSQL += " marca_tiempo >= '"+data.get(SearchParameters.FECHAINICIAL)+"' AND ";
		}
		if(data.containsKey(SearchParameters.FECHAFINAL) && !data.get(SearchParameters.FECHAFINAL).equals("")){
			whereSQL += " marca_tiempo <= '"+data.get(SearchParameters.FECHAFINAL)+"' AND ";
		}
		whereSQL += " TRUE ";
		
    	return whereSQL;
    }
	
}









