
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class BeatBuilder extends JFrame implements ActionListener {

    static final int beatsPerMeasure = 8;
    static final int soundsPerColumn = 2;

    static final float lengthOfMeasureInSeconds = 2f;
    static float currentProgressOfMeasure = 0f;

    // The 2-dimensional arrays that keep references to the buttons, whether they are activated, and thir associated sound effect
    JButton[][] buttonArray = new JButton[beatsPerMeasure][soundsPerColumn];
    static boolean[][] activatedButtons = new boolean[beatsPerMeasure][soundsPerColumn];
    MyAudioPlayer[][] playerArray = new MyAudioPlayer[beatsPerMeasure][soundsPerColumn];

    // The timers that handle the redrawing / measure progression
    Timer beatTimer = new Timer();
    Timer drawTimer = new Timer();

    // The masterPanel is the highest-level UI element. It contains the button grid and time tracker
    JPanel masterPanel = new JPanel();

    // Another JPanel is needed to hold the buttons in an organized box
    JPanel buttonGridPanel = new JPanel();

    TimeDisplay timeDisplay = new TimeDisplay();

    public BeatBuilder() {

	super();

	loadSoundFiles();

	setupGUI();

	drawTimer.scheduleAtFixedRate(new TimerTask() {

	    @Override
	    public void run() {
		timeDisplay.repaint();
	    }
	}, 0, (long) 40);

	beatTimer.scheduleAtFixedRate(new TimerTask() {

	    int beatNumber = 0;

	    @Override
	    public void run() {

		// Increment the current progress of the measure bar, 
		currentProgressOfMeasure += (1f / 1000f);

		if (currentProgressOfMeasure > lengthOfMeasureInSeconds) {

		    currentProgressOfMeasure = 0;
		    beatNumber = 0;
		}

		if ((int) (currentProgressOfMeasure * beatsPerMeasure / lengthOfMeasureInSeconds) > beatNumber - 1) {

		    beatNumber++;
		    beatHit(beatNumber - 1);
		}
	    }
	}, 0, 1);

	// The shutdown hook will execute right before the program exits.
	// It's purpose is to close the Stream resources from the MyAudioPlayer objects
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    public void run() {

		for (int yLoop = 0; yLoop < soundsPerColumn; yLoop++) {
		    for (int xLoop = 0; xLoop < beatsPerMeasure; xLoop++) {

			playerArray[xLoop][yLoop].cleanupBeforeProgramExit();
		    }
		}
	    }
	});
    }

    private void setupGUI() {

	// Create the master layout
	BoxLayout boxLayout = new BoxLayout(masterPanel, BoxLayout.Y_AXIS);
	masterPanel.setLayout(boxLayout);

	// Create the buttons and grid layout
	GridLayout gridLayout = new GridLayout(soundsPerColumn, beatsPerMeasure, 15, 15);
	buttonGridPanel.setLayout(gridLayout);
	buttonGridPanel.setBackground(Color.DARK_GRAY);

	for (int yLoop = 0; yLoop < soundsPerColumn; yLoop++) {
	    for (int xLoop = 0; xLoop < beatsPerMeasure; xLoop++) {

		activatedButtons[xLoop][yLoop] = new Boolean(false);

	    }
	}

	for (int yLoop = 0; yLoop < soundsPerColumn; yLoop++) {
	    for (int xLoop = 0; xLoop < beatsPerMeasure; xLoop++) {

		// Giving the buttons a name is not normally necessary, but in this case the name is used to identify the action to take when the button is pressed
		// Note: When adding things to some Layouts, the order you add things will determine the order they are presented
		String buttonName = xLoop + "_" + yLoop;
		buttonArray[xLoop][yLoop] = new JButton();
		buttonArray[xLoop][yLoop].setName(buttonName);
		buttonArray[xLoop][yLoop].setBackground(Color.WHITE);
		buttonArray[xLoop][yLoop].setOpaque(true);
		buttonArray[xLoop][yLoop].setBorderPainted(false);
		buttonArray[xLoop][yLoop].addActionListener(this);
		buttonArray[xLoop][yLoop].setText("Sound" + yLoop);
		buttonGridPanel.add(buttonArray[xLoop][yLoop]);
	    }
	}

	masterPanel.add(buttonGridPanel);
	this.add(masterPanel);

	timeDisplay.setSize(900, 100);
	masterPanel.add(timeDisplay);

	// Finalize the program window and make viewable		
	this.pack();
	this.setSize(900, 500);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setVisible(true);
    }

    private void loadSoundFiles() {

	File file;
	for (int loop = 0; loop < soundsPerColumn; loop++) {

	    URL url = this.getClass().getResource("sound" + (loop) + ".wav");
	    file = new File(url.getFile());

	    if (file.exists()) {

		for (int loop2 = 0; loop2 < beatsPerMeasure; loop2++) {

		    try {

			playerArray[loop2][loop] = new MyAudioPlayer(file.toURI().toURL());
			playerArray[loop2][loop].prepare();
			System.out.println("loaded file: (" + url + ") into index: " + loop2 + "," + loop);

		    } catch (Exception e) {
			System.out.println(e);
		    }
		}
	    } else {

		System.out.println("File sound" + (loop) + ".wav does not exist");
	    }
	}
    }

    private void beatHit(int beatNumber) {

	for (int yLoop = 0; yLoop < soundsPerColumn; yLoop++) {

	    if (activatedButtons[beatNumber][yLoop] && playerArray[beatNumber][yLoop] != null) {

		try {
		    playerArray[beatNumber][yLoop].play();

		} catch (Exception ex) {
		    System.out.println(ex);
		}
	    }
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	// Determine the XY index of the pressed button
	JButton buttonPressed = (JButton) e.getSource();
	String[] coordsString = buttonPressed.getName().split("_");
	int xIndex = Integer.parseInt(coordsString[0]);
	int yIndex = Integer.parseInt(coordsString[1]);


	// flip the value in the activatedButtons array
	boolean boolValue = activatedButtons[xIndex][yIndex];
	boolValue = !boolValue;
	activatedButtons[xIndex][yIndex] = boolValue;

	// Change the color of the button
	if (boolValue) {
	    Color yellow = Color.decode("#EAC338");
	    buttonArray[xIndex][yIndex].setBackground(yellow);
	} else {
	    buttonArray[xIndex][yIndex].setBackground(Color.WHITE);
	}

	String isActivated = activatedButtons[xIndex][yIndex] ? "activated" : "deactivated";
	System.out.println("row: " + xIndex + ", column: " + yIndex + " is " + isActivated);

    }

    public static void main(String[] args) {
	new BeatBuilder();
    }

}
