import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PlaySound {
	
	private Clip clip;
	private AudioInputStream audioInputStream;
	
	@SuppressWarnings("static-access")
	public PlaySound(String song) 
	{
	    try {
	        audioInputStream = AudioSystem.getAudioInputStream(new File(System.getProperty("user.dir") + "\\assets\\" + song + ".wav").getAbsoluteFile());
	        clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.loop(clip.LOOP_CONTINUOUSLY);
	        //clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	
	
	public void play ()
	{
		clip.start();
	}
	
	
	public void stop ()
	{
		clip.stop();
		clip.setFramePosition(0);
	}
	
}
