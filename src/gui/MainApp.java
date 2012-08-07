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

import conncheck.ServerMonitor;
import database.Conector;
import database.DBInterface;
import database.SysVar;


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
	private static final String POSTGRES_PROPERTIES_PATH = "src/postgres.properties";
	private static String DIR_PATH;
	
	JPanel basic;         //panel principal de la App
	JTabbedPane tabpanel; //panel de tabs de la App
	JPanel serverPanel;   //panel del tab server
	JPanel emailPanel;    //panel del tab email
	JTable emailTable;    //tabla con emails de alerta
	
	JTree tree;           //tree de los servers
	DefaultMutableTreeNode root; //nodo raiz tree
	DefaultTreeModel model; //modelo de datos del tree
	Hashtable<String, Object> htTree; //Hashtable para el Tree de los servers
	Hashtable<String, Properties> serverData; 
	//Hashtable paralelo al Tree, para pasar datos a EditDialog 
	
	String lastSelectedServerName; //nombre del ultimo servidor seleccionado del tree
	DefaultMutableTreeNode lastSelectedNode; //ultimo nodo seleccionado del tree
	
	JToolBar toolbar;     //toolbar de la App
	AddServerDialog AddDialog; //Ventana de dialogo para agregar server
	EditServerDialog EditDialog; //Ventana de dialogo de editar server
	

    public static String getDIR_PATH() {
		return DIR_PATH;
	}


	public MainApp() {
    	
    	DBInterface db = null;
    	try{		
    		Connection conPostgres = Conector.conectar(POSTGRES_PROPERTIES_PATH);
    		db = new DBInterface(conPostgres);
    		ArrayList<SysVar> sv = db.selectSysVarObjByName("DIR_PATH");
    		if(!sv.isEmpty())
    			MainApp.DIR_PATH = sv.get(0).getValue();
    		else{
    			System.err.println("No se encontro la variable DIR_PATH en la base de datos");
    			System.exit(ABORT);
    		}
    	} catch (ClassNotFoundException e) {
    		System.out.println("No se encontro el driver");
    		e.printStackTrace();
    	} catch (SQLException e) {
    		System.out.println("No se pudo conectar" + e.getMessage());
    		e.printStackTrace();
    	}
    	
    	AddDialog = new AddServerDialog(this); 
    	EditDialog = new EditServerDialog(this);
    	serverData = new Hashtable<String, Properties>();
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
        ImageIcon editIcon = new ImageIcon(getClass().getResource("edit.png"));
        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("delete.png"));
        ImageIcon configIcon = new ImageIcon(getClass().getResource("config.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("exit2.png"));
        
        JButton addButton = new JButton(addIcon);
        addButton.setToolTipText("Add server configuration");
        JButton editButton = new JButton(editIcon);
        addButton.setToolTipText("Edit server configuration");
        JButton deleteButton = new JButton(deleteIcon);
        addButton.setToolTipText("Delete server configuration");
        JButton configButton = new JButton(configIcon);
        addButton.setToolTipText("Configure program parameters");
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
		
		root = new DefaultMutableTreeNode("Servers");
		model = new DefaultTreeModel(root);
		htTree = new Hashtable<String, Object>();
		
		// Listar los archivos de la carpeta de configuración
		Directory dir = new Directory(MainApp.DIR_PATH);
		// Array de los archivos de configuración de los servidores
		File servers_file[] = dir.list();		
		// Leer los datos de cada archivo de configuración
		for (int i = 0; i < servers_file.length; i++) {
			Parser p = new Parser(servers_file[i].getAbsolutePath());
			serverData.put(servers_file[i].getName(),p.readProperties());
			htTree.put(servers_file[i].getName(),propertiesToStringArray(p.readProperties()));
			//TODO iniciar el hilo de monitor de este server
			ServerMonitor server = new ServerMonitor(p.readProperties());
			server.start();
		} 
		
		JTree.DynamicUtilTreeNode.createChildren(root, htTree);
		tree = new JTree(model);
		tree.getSelectionModel().setSelectionMode
          (TreeSelectionModel.SINGLE_TREE_SELECTION);

	    JScrollPane scrollPane = new JScrollPane(tree);
	    serverPanel.add(scrollPane, BorderLayout.CENTER);
	 
		return serverPanel;
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
				// System.out.println(node.getParent().toString());
			 }
			 else{
				 lastSelectedServerName = lastSelectedNode.toString();
				// System.out.println(node.toString()); 
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
    
    class ExitButtonListener implements ActionListener{
    	 public void actionPerformed(ActionEvent event) {
         	int opt = JOptionPane.showConfirmDialog(basic,"Are you sure to quit?",
         		 "Exit",JOptionPane.YES_NO_OPTION);
         	if(opt==0)
                System.exit(0);
         }
    }
    
    
    /*
     * Utils
     */

    
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
     * Add new server file
     */
    
    public void addNewServerFileToTree(ServerDialog sdialog){
    	 Hashtable<String, Object> ht = new Hashtable<String, Object>();
    	 ht.put(sdialog.newServerName(),
    	    propertiesToStringArray(sdialog.newServerProp()));
    	 serverData.put(sdialog.newServerName(), sdialog.newServerProp());
    	 JTree.DynamicUtilTreeNode.createChildren(root,ht);
    	 ((DefaultTreeModel) tree.getModel()).reload();
    	 
    	 //TODO iniciar hilo de monitor para este server
    	 // hay que pasarle las propiedades dialog.newServerProp()
    }
    
    /*
     * Creacion del panel de la Tabla de email
     */
    
    public JPanel createEmailPanel(String text) {
		JPanel emailPanel = new JPanel();
		
		DBInterface db = null;
		try{
			Connection conPostgres = Conector.conectar(POSTGRES_PROPERTIES_PATH);
    		db = new DBInterface(conPostgres);
			emailTable = new JTable(new AlertsTableModel(db.selectAllBitacoraObj()));
		} catch (ClassNotFoundException e) {
			System.out.println("No se encontro el driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("No se pudo conectar" + e.getMessage());
			e.printStackTrace();
		}
		
		
		//new JTable
		//System.out.println(db.selectAllBitacoraObj());
		JScrollPane scrollPane = new JScrollPane(emailTable);
		emailPanel.add(scrollPane);
		
		return emailPanel;
	}
    
    
    /*
     * Funciones para comunicarse con JDialog
     */
    
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