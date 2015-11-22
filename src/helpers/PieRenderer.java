package helpers;

import java.awt.Color;
import java.util.List;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;

import analyzers.TopicIdentification;

public class PieRenderer {

	public void setColor(PiePlot3D plot, PieDataset dataset) {
		List<Comparable> keys = dataset.getKeys();

		for (int i = 0; i < keys.size(); i++) {
			plot.setSectionPaint(keys.get(i), Color.WHITE);
		}

		plot.setSectionPaint(TopicIdentification.BESTCATEGORY, Color.YELLOW);

	}
}
