package gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import java.util.Iterator;

import database.Bitacora;

public class AlertsTableModel extends AbstractTableModel  {
    String[]   columnNames;
    Object[][] data;
    public AlertsTableModel( ArrayList<Bitacora> alerts ) {
    	columnNames = new String[] {"Alias","Dir IP","Puerto","e-mail","Estado","Marca Tiempo"};
    	setModel(alerts);
    }  
    
    public void setModel(ArrayList<Bitacora> alerts) {
    	data = new Object[alerts.size()][6];
    	int i = 0;
        for(Iterator<Bitacora> itr = alerts.iterator(); itr.hasNext();i++){
        	Bitacora registro = itr.next();
    		data[i][0] = registro.getAlias();
    		data[i][1] = registro.getDireccionIP();
    		data[i][2] = registro.getPuerto();
    		data[i][3] = registro.getEmail();
    		data[i][4] = registro.getEstado();
    		data[i][5] = registro.getMarcaTiempo();
    	}
    }
    
    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}
