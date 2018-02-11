
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.swing.JComponent;

class TimeDisplay extends JComponent {

    @Override
    public void paintComponent(Graphics g) {

	Graphics2D g2 = (Graphics2D) g;

	// Get references to BeatBuilder Class's variables
	int beatsPerMeasure = test1.beatsPerMeasure;
	float lengthOfMeasureInSeconds = test1.lengthOfMeasureInSeconds;
	float currentProgressOfMeasure = test1.currentProgressOfMeasure;

	// Draw the background
	g2.setColor(Color.DARK_GRAY);
	g2.fillRect(0, 0, 10000, 10000);

	// Draw the moving bar
	int pixelPositionOfBar = (int) (this.getWidth() * (currentProgressOfMeasure / lengthOfMeasureInSeconds));
	int heightOfDrawBar = (int) (this.getHeight() * 0.8f);
	
 	Stroke stroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	Color yellow = Color.decode("#EAC338");
	
	g2.setStroke(stroke);
	g2.setColor(yellow);
	g2.drawLine(pixelPositionOfBar, 0, pixelPositionOfBar, heightOfDrawBar);

	// Draw the measure bars
	g2.setColor(Color.WHITE);
	int yOffset = (int) (this.getHeight() * 0.2f);
	for (int loop = 0; loop < beatsPerMeasure; loop++) {

	    int xPositionToDraw = (int) (this.getWidth() * (loop / (float) beatsPerMeasure));
	    g2.drawLine(xPositionToDraw, heightOfDrawBar, xPositionToDraw, yOffset);
	}
    }
}
