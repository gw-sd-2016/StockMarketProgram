package helpers;

import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

public interface IFrequency {
	public Map<String, Integer> wordFrequency(String xlo, JTextPane printArea);
	public void printMap(Map<String, Integer> map, JTextPane printArea);
}
