package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import conncheck.ServerMonitor;
import database.Conector;
import database.DBInterface;
import database.SysVar;
import email.MonthlyReport;


import parser.Directory;
import parser.FileManager;
import parser.Parser;

/*
 * Ventana principal de la aplicación
 */

public class MainApp extends JFrame {
	
	private static final long serialVersionUID = -408641277064642917L;

	/*
	 * La ruta hasta el archivo .properties que contiene los datos para
	 * conectarnos a la Base de Datos
	 */
	private ArrayList<ServerMonitor> serverList = new ArrayList<ServerMonitor>();
	private MonthlyReport mReport = new MonthlyReport(serverList);
	
	
	public static final String POSTGRES_PROPERTIES_PATH = "src/postgres.properties2";
	public static String DIR_PATH;
	
	static Logger logger = Logger.getLogger(ServerMonitor.class);
	
	JPanel basic;         //panel principal de la App
	JTabbedPane tabpanel; //panel de tabs de la App
	JPanel serverPanel;   //panel del tab server
	JPanel emailPanel;    //panel del tab email
	JPanel searchEmailPanel; //panel del buscador de alertas
	JTable emailTable;    //tabla con emails de alerta
	
	AlertsTableModel alertsTableModel; //modelo para la tabla de emails de alertas
	
	JTree tree;           //tree de los servers
	DefaultMutableTreeNode root; //nodo raiz tree
	DefaultTreeModel model; //modelo de datos del tree
	Hashtable<String, Properties> serverData; 
	//Hashtable paralelo al Tree, para pasar datos a EditDialog 
	
	String lastSelectedServerName; //nombre del ultimo servidor seleccionado del tree
	DefaultMutableTreeNode lastSelectedNode; //ultimo nodo seleccionado del tree
	
	JToolBar toolbar;     //toolbar de la App
	AddServerDialog AddDialog; //Ventana de dialogo para agregar server
	EditServerDialog EditDialog; //Ventana de dialogo de editar server
	SearchEmailAlertDialog searchEmailAlertDialog; //Ventana de dialogo para buscar alerta
	DefaultConfigDialog ConfigDialog; //Ventana de dialogo de editar server
	
	JButton configButton;
	
	boolean defaultConfig;

    public static String getDIR_PATH() {
		return DIR_PATH;
	}
    public static void setDIR_PATH(String dirPath) {
		DIR_PATH = dirPath;
	}
	public MainApp() {
	//	mReport.start();
		ConfigDialog = new DefaultConfigDialog(this);
		ImageIcon configIcon = new ImageIcon(getClass().getResource("config.png"));
		configButton = new JButton(configIcon);
		configButton.addActionListener(new ConfigButtonListener());
		
		File fichero = new File(MainApp.POSTGRES_PROPERTIES_PATH);		
		if(!fichero.exists()){
			defaultConfig=false; //first time
			this.configButton.doClick();
		}
		defaultConfig=true;

		PropertyConfigurator.configure("src/log4j.properties");
		logger.debug("Iniciada la Ejecucion del Programa");
    	DBInterface db = null;
    	try{		
    		Connection conPostgres = Conector.connectByFile(POSTGRES_PROPERTIES_PATH);
    		db = new DBInterface(conPostgres);
    		ArrayList<SysVar> sv = db.selectSysVarObjByName("DIR_PATH");
    		MainApp.DIR_PATH = sv.get(0).getValue();
    	} catch (ClassNotFoundException e) {
    		System.out.println("Database Driver not found");
    		e.printStackTrace();
    	} catch (SQLException e) {
    		System.out.println("Can not connect to database" + e.getMessage());
    		e.printStackTrace();
    	}
    	
    	AddDialog = new AddServerDialog(this); 
    	EditDialog = new EditServerDialog(this);
    	serverData = new Hashtable<String, Properties>();
    	searchEmailAlertDialog = new SearchEmailAlertDialog(this);
    	
        initUI();
    }

    public final void initUI() {
    	logger.debug("Iniciado el dibujado de la interfaz");
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
        ImageIcon editIcon = new ImageIcon(getClass().getResource("edit.png"));
        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("delete.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("exit2.png"));
        
        JButton addButton = new JButton(addIcon);
        addButton.setToolTipText("Add server configuration");
        JButton editButton = new JButton(editIcon);
        editButton.setToolTipText("Edit server configuration");
        JButton deleteButton = new JButton(deleteIcon);
        deleteButton.setToolTipText("Delete server configuration");
        configButton.setToolTipText("Configure program parameters");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setToolTipText("Exit from program");
             
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(configButton);
        toolbar.add(exitButton);
        toolbar.setFloatable(false);
        
        tree.addTreeSelectionListener(new TreeListener());
        addButton.addActionListener(new AddButtonListener());
        editButton.addActionListener(new EditButtonListener());
        deleteButton.addActionListener(new DeleteButtonListener());
        exitButton.addActionListener(new ExitButtonListener());
        
        add(basic);

        basic.add(toolbar, BorderLayout.NORTH);
        basic.add(tabpanel,BorderLayout.CENTER);

        setTitle("Service monitoring");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    /*
     * Creacion del panel del Tree de servidores
     */
    
    public JPanel createServerPanel() {
		JPanel serverPanel = new JPanel(new BorderLayout());
		
		model = new DefaultTreeModel(null);
		tree = new JTree(model);
		
		readServerFiles();
		
		tree.getSelectionModel().setSelectionMode
          (TreeSelectionModel.SINGLE_TREE_SELECTION);

	    JScrollPane scrollPane = new JScrollPane(tree);
	    serverPanel.add(scrollPane, BorderLayout.CENTER);
	 
		return serverPanel;
	}
    
    public void readServerFiles(){
    	Hashtable<String, Object> htTree = new Hashtable<String, Object>();
    	
    	root = new DefaultMutableTreeNode("Servers");
    	// Listar los archivos de la carpeta de configuración
		Directory dir = new Directory(MainApp.DIR_PATH);
		// Array de los archivos de configuración de los servidores
		File servers_file[] = dir.list();	
		
		if(servers_file!=null){	//si existen archivos .properties
			// Leer los datos de cada archivo de configuración
			for (int i = 0; i < servers_file.length; i++) {
				Parser p = new Parser(servers_file[i].getAbsolutePath());
				serverData.put(servers_file[i].getName(),p.readProperties());
				htTree.put(servers_file[i].getName(),propertiesToStringArray(p.readProperties()));
				ServerMonitor server = new ServerMonitor(p.readProperties(),this);
				server.start();
				serverList.add(server);//guardamos el server monitor recen creado en la lista de servers
				mReport.setServerList(serverList);
			} 
		}
		
		JTree.DynamicUtilTreeNode.createChildren(root, htTree);
		
		 ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }
    
    public void readServerFilesWithoutServerMonitorCall(){
    	Hashtable<String, Object> htTree = new Hashtable<String, Object>();
    	
    	root = new DefaultMutableTreeNode("Servers");
    	// Listar los archivos de la carpeta de configuración
		Directory dir = new Directory(MainApp.DIR_PATH);
		// Array de los archivos de configuración de los servidores
		File servers_file[] = dir.list();	
		
		if(servers_file!=null){	//si existen archivos .properties
			// Leer los datos de cada archivo de configuración
			for (int i = 0; i < servers_file.length; i++) {
				Parser p = new Parser(servers_file[i].getAbsolutePath());
				serverData.put(servers_file[i].getName(),p.readProperties());
				htTree.put(servers_file[i].getName(),propertiesToStringArray(p.readProperties()));
			} 
		}
		
		JTree.DynamicUtilTreeNode.createChildren(root, htTree);
		
		 ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }
    
    /*
     * Listeners
     */
    
    class TreeListener implements TreeSelectionListener{
    	@Override
		public void valueChanged(TreeSelectionEvent e) {
			 lastSelectedNode = (DefaultMutableTreeNode)
                     tree.getLastSelectedPathComponent();
			 
			 if (lastSelectedNode == null || lastSelectedNode.isRoot()){
				 lastSelectedServerName = null;
				 return;
			 }		 
			 if(lastSelectedNode.isLeaf()){
				 lastSelectedServerName = lastSelectedNode.getParent().toString();
			 }
			 else{
				 lastSelectedServerName = lastSelectedNode.toString();
			 }	 
		}
    }
    
    class AddButtonListener implements ActionListener{
    	 public void actionPerformed(ActionEvent event) {
        	 SwingUtilities.invokeLater(new Runnable() {
                 public void run() {
                	 AddDialog.setVisible(true);
                     //como es modal, al llegar aquí es porque se ha cerrado la
                     //ventana
                     if(AddDialog.newServerProp()!=null){         
                    	 //cargar nuevo server file properties
                    	 addNewServerFileToTree(AddDialog);
                     }
                 }
             });
        }
    }
    
    public void removeServerMonitor(String lastServerName){
    	ServerMonitor deleteServer = null;
		 Iterator<ServerMonitor> itr = serverList.iterator();
		 while(itr.hasNext()){
			 ServerMonitor currentServer = itr.next();
			 Properties serverInfo = currentServer.getServerInfo();
			 String serverName = serverInfo.getProperty("alias") + ".properties";
			 if(serverName.compareTo(lastServerName) == 0){
				 deleteServer = currentServer;
				 currentServer.setDie(true);
			 }
		 }
		 serverList.remove(deleteServer);
		mReport.setServerList(serverList);
			
	}
    
    class EditButtonListener implements ActionListener{
    	 public void actionPerformed(ActionEvent event) {
        	 SwingUtilities.invokeLater(new Runnable() {
                 public void run() {
                	 if(lastSelectedNode==null){
                		 JOptionPane.showMessageDialog(basic, "You must " +
                		 		"select a server first!",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                		 return;
                	 }
                	 if(lastSelectedNode.isRoot()){
                		 JOptionPane.showMessageDialog(basic, "Can't " +
                		 		"edit root",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                		 return;
                	 }
                	 EditDialog.loadFields();
                     EditDialog.setVisible(true);
                     //como es modal, al llegar aquí es porque se ha cerrado la
                     //ventana
                     if(EditDialog.newServerProp()!=null){         
                    	 //cargar nuevo server file properties
                    	 removeServerMonitor(lastSelectedServerName);
                    	 if(lastSelectedNode.isLeaf()){
                    		 ((DefaultTreeModel) tree.getModel()).
                        	 removeNodeFromParent((MutableTreeNode) lastSelectedNode.getParent()); 	
                		 }
                		 else{
                			 ((DefaultTreeModel) tree.getModel()).
                        	 removeNodeFromParent(lastSelectedNode);
                		 }
                    	 addNewServerFileToTree(EditDialog);	
                     }
                     
                 }
             });
        }
    }
    
    class DeleteButtonListener implements ActionListener{
    	public void actionPerformed(ActionEvent event) {
       	 SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	 if(lastSelectedNode==null){
                		 JOptionPane.showMessageDialog(basic, "You must " +
                		 		"select a server first!",
                                 "Error", JOptionPane.ERROR_MESSAGE);
                		 return;
                	 }    	
	               	 if(lastSelectedNode.isRoot()){
	               		 JOptionPane.showMessageDialog(basic, "Can't delete root!",
	                                "Error", JOptionPane.ERROR_MESSAGE);
	               		 return;
	               	 }                  	 
	               	 int opt = JOptionPane.showConfirmDialog(basic,"Are you " +
               	 		"sure you want to delete \""+getLastSelectedServerName()+"\"?",
                   		 "Exit",JOptionPane.YES_NO_OPTION);
	               	 if(opt==0){
	               		 serverData.remove(getLastSelectedServerName());
	               		 FileManager.remove(getLastSelectedServerName());
	               		 removeServerMonitor(lastSelectedServerName);
	                   	 if(lastSelectedNode.isLeaf()){
	                   		 ((DefaultTreeModel) tree.getModel()).
	                       	 removeNodeFromParent((MutableTreeNode) lastSelectedNode.getParent()); 	
	               		 }
	               		 else{
	               			 ((DefaultTreeModel) tree.getModel()).
	                       	 removeNodeFromParent(lastSelectedNode);
	               		 }
	               		
	               	 }
                }
            });
       }
    }
    
    class ConfigButtonListener implements ActionListener{
   	 public void actionPerformed(ActionEvent event) {
	   		 if(defaultConfig==true) ConfigDialog.loadFields();
	   		 ConfigDialog.setVisible(true);
	         //como es modal, al llegar aquí es porque se ha cerrado la
	         //ventana
	   		if(defaultConfig==true){
		   		 if(ConfigDialog.getUpdatePath()){
		   			 readServerFiles();
		   		     refreshTreeModel();
		   		 }
	   		}	  
       }
   }
    
    public void refreshTreeModel(){
    	 ((DefaultTreeModel) tree.getModel()).reload();
    }
    
    class ExitButtonListener implements ActionListener{
    	 public void actionPerformed(ActionEvent event) {
         	int opt = JOptionPane.showConfirmDialog(basic,"Are you sure to quit?",
         		 "Exit",JOptionPane.YES_NO_OPTION);
         	if(opt==0)
                System.exit(0);
         }
    }
    
    class SearchEmailAlertButtonListener implements ActionListener{
    	public void actionPerformed( ActionEvent event ){
    		SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	searchEmailAlertDialog.setVisible(true);
                	
                	try{
                		Connection conPostgres = Conector.connectByFile(POSTGRES_PROPERTIES_PATH);
            			DBInterface db = new DBInterface(conPostgres);
            			alertsTableModel.setModel(db.selectBitacoraObj(searchEmailAlertDialog.getWhereParameters()));
                	} catch (SQLException ex) {
            			System.out.println("No se pudo conectar" + ex.getMessage());
            			ex.printStackTrace();
            		} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
                	alertsTableModel.fireTableChanged(null);
                }
    		});
    	}
    }
    
    /*
     * Utils
     */

    
    public String[] propertiesToStringArray(Properties prop){
    	String[] StringProp = new String[13];
    	int i=0;
    	for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			StringProp[i++]=new String(obj +"="+ prop.getProperty(obj.toString()));
		}
		return StringProp;	
    }
    
    /*
     * Add new server file
     */
    
    public void addNewServerFileToTree(ServerDialog sdialog){
    	 Hashtable<String, Object> ht = new Hashtable<String, Object>();
    	 ht.put(sdialog.newServerName(),
    	    propertiesToStringArray(sdialog.newServerProp()));
    	 serverData.put(sdialog.newServerName(), sdialog.newServerProp());
    	 JTree.DynamicUtilTreeNode.createChildren(root,ht);
    	 refreshTreeModel();
    	 ServerMonitor server = new ServerMonitor(sdialog.newServerProp(),this);
		 server.start();
		 serverList.add(server);//guardamos el server monitor recen creado en la lista de servers
		 mReport.setServerList(serverList);
	}
    
    /*
     * Creacion del panel de la Tabla de email
     */
    
    public JPanel createEmailPanel(String text) {
		JPanel emailPanel = new JPanel(new BorderLayout());
		
		DBInterface db = null;
		try{
			Connection conPostgres = Conector.connectByFile(POSTGRES_PROPERTIES_PATH);
    		db = new DBInterface(conPostgres);
			alertsTableModel = new AlertsTableModel(db.selectAllBitacoraObj());
			emailTable = new JTable(alertsTableModel);
		} catch (ClassNotFoundException e) {
			System.out.println("No se encontro el driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("No se pudo conectar" + e.getMessage());
			e.printStackTrace();
		}
		
		JPanel searchEmailPanel = new JPanel();
        
        JButton searchButton = new JButton("Search Options");
        searchButton.addActionListener(new SearchEmailAlertButtonListener());
        searchEmailPanel.add(searchButton);
		
		emailPanel.add(new JScrollPane(emailTable), BorderLayout.CENTER);
		emailPanel.add(searchEmailPanel, BorderLayout.NORTH);
		
		
		return emailPanel;
	}
    
    
    /*
     * Funciones para comunicarse con JDialog
     */
    
    public boolean getDefaulConfig(){
    	return defaultConfig;
    }
    
    public String getLastSelectedServerName(){
    	return lastSelectedServerName;
    }
    
    public Hashtable<String, Properties> getServerData(){
    	return serverData;
    }
    
    /*
     * Java application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainApp w = new MainApp();
                w.setVisible(true);      
            }
        });
    }
}