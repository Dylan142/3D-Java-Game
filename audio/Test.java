package audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL10;

public class Test {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		List<Source> sources = new ArrayList<Source>();
		
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		int buffer = AudioMaster.loadSound("audio/bounce.wav");
		Source source = new Source(4, 10, 40);
		sources.add(source);
		source.setLooping(true);
		source.play(buffer);
		
		Source source2 = new Source(4, 10, 40);
		sources.add(source2);
		source2.setPitch(2);
		
		float xPos = 0;
		source.setPosition(xPos, 0, 0);
		
		char c = ' ';
		while(c != 'q') {
			
			xPos -= 0.03f;
			source.setPosition(xPos, 0, 0);
			System.out.println(xPos);
			Thread.sleep(10);
		}
		
		for(Source s : sources) {
			s.delete();
		}
		AudioMaster.cleanUp();
	}

}
