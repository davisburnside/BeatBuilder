import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MyAudioPlayer {

    public Clip clip;
    private URL url;

    public MyAudioPlayer(URL url) {
	this.url = url;
    }

    // this method must be called after creation on each AudioPlayer object.
    protected void prepare() throws IOException, LineUnavailableException, UnsupportedAudioFileException {

	// load the sound into memory with the Clip object
	AudioInputStream inputStream = AudioSystem.getAudioInputStream(url.openStream());
	DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
	clip = (Clip) AudioSystem.getLine(info);
	clip.open(inputStream);
    }

    public void play() throws IOException, LineUnavailableException, UnsupportedAudioFileException {

	clip.setFramePosition(0);
	clip.start();
    }

    public void cleanupBeforeProgramExit() {

	if (clip != null) {
	    clip.stop();
	    clip.flush();
	}
    }
}
