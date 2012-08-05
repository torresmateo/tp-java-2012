package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import parser.Directory;
import parser.Parser;

/*
 * Ventana principal de la aplicación
 */

public class MainApp extends JFrame {
	
	public static final String DIR_PATH="/home/samu/java_workspace/tp-java-2012/servers/";
	
	JPanel basic;         //panel principal de la App
	JTabbedPane tabpanel; //panel de tabs de la App
	JPanel serverPanel;   //panel del tab server
	JPanel emailPanel;    //panel del tab email
	JTree tree;           //tree de los servers
	
	JToolBar toolbar;     //toolbar de la App
	AddServerDialog dialog; //Ventana de dialogo 
	Hashtable<String, Object> htTree; //Hashtable para el Tree de los servers

    public MainApp() {
    	dialog = new AddServerDialog(this); //dialogo modal
        initUI();
    }

    public final void initUI() {
    	
    	basic = new JPanel(new BorderLayout());
    	
    	//Panel de Tabs
    	tabpanel = new JTabbedPane();
    	//Server tab
		serverPanel = createServerPanel();
		tabpanel.addTab("Server list", serverPanel);
		tabpanel.setSelectedIndex(0);
		//alert emails tab
		emailPanel = createEmailPanel("email tab");
		tabpanel.addTab("Emails sent", emailPanel);
    	
		//Program toolbar
        toolbar = new JToolBar();
        
        ImageIcon addIcon = new ImageIcon(getClass().getResource("add.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("exit.png"));
        
        JButton addButton = new JButton(addIcon);
        addButton.setToolTipText("Add server configuration");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setToolTipText("Exit");
             
        toolbar.add(addButton);
        toolbar.add(exitButton);
        toolbar.setFloatable(false);

        //Anonymous inner class
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	 SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                         dialog.setVisible(true);
                         //como es modal, al llegar aquí es porque se ha cerrado la
                         //ventana
                         if(dialog.newServerFile()){         
                        	 //cargar nuevo server file properties
                        	 serverPanel = createServerPanel();
                         }
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
        basic.add(tabpanel,BorderLayout.CENTER);

        setTitle("Service monitoring");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    /*
     * Creacion del panel del Tree de servidores
     */
    
    public JPanel createServerPanel() {
		JPanel serverPanel = new JPanel(new BorderLayout());
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Servers");
		htTree = new Hashtable<String, Object>();
		
		// Listar los archivos de la carpeta de configuración
		Directory dir = new Directory(MainApp.DIR_PATH);
		// Array de los archivos de configuración de los servidores
		File servers_file[] = dir.list();		
		// Leer los datos de cada archivo de configuración
		for (int i = 0; i < servers_file.length; i++) {
			Parser p = new Parser(servers_file[i].getAbsolutePath());
			htTree.put(servers_file[i].getName(),propertiesToStringArray(p.readProperties()));
		}
		
		JTree.DynamicUtilTreeNode.createChildren(root, htTree);
		tree = new JTree(root);

	    JScrollPane scrollPane = new JScrollPane(tree);
	    serverPanel.add(scrollPane, BorderLayout.CENTER);
	 
		return serverPanel;
	}
    
    public String[] propertiesToStringArray(Properties prop){
    	String[] StringProp = new String[13];
    	StringBuffer sb = new StringBuffer("");
    	int i=0;
    	for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			StringProp[i++]=new String(obj +"="+ prop.getProperty(obj.toString()));
		}
		return StringProp;	
    }
    
    /*
     * Creacion del panel de la Tabla de email
     */
    
    public JPanel createEmailPanel(String text) {
		JPanel emailPanel = new JPanel();
		JLabel jlbDisplay = new JLabel(text);
		jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
		emailPanel.setLayout(new GridLayout(1, 1));
		emailPanel.add(jlbDisplay);
		return emailPanel;
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