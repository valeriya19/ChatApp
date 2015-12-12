import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author M-Sh-97
 */
class ContactTableModel extends AbstractTableModel {
  private Vector<Vector<String>> data;
  private Vector<String> columnNames;
  
  public ContactTableModel(Vector<String> columnNames, int rowCount) {
    if (rowCount < 0)
      throw new ArrayIndexOutOfBoundsException();
    else {
      this.columnNames = columnNames;
      data = new Vector<Vector<String>>(rowCount);
      for (Vector<String> dr: data)
	dr = new Vector<String>(0);
    }
  }
  
  public ContactTableModel() {
    this(new Vector<String>(0), 0);
  }
  
  public ContactTableModel(Vector<String> columnNames, Vector<Vector<String>> contactInfo) {
    this.columnNames = columnNames;
    data = contactInfo;
  }
  
  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public int getColumnCount() {
    if (data.size() > 0)
      return data.get(0).size();
    else
      return 0;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if ((data.isEmpty()) || ((rowIndex >= data.size()) || (rowIndex < 0)) || ((columnIndex >= data.get(rowIndex).size()) || (columnIndex < 0))) 
      throw new ArrayIndexOutOfBoundsException();
    else
      return data.get(rowIndex).get(columnIndex);
  }
  
  public void addRow(Vector<String> dataRow) {
    data.add(dataRow);
  }
  
  public void removeRow(int rowIndex) {
    if ((rowIndex < data.size()) && (rowIndex >= 0))
      data.remove(rowIndex);
    else
      throw new ArrayIndexOutOfBoundsException();
  }
  
  public void clear() {
    data.clear();
  }
}
