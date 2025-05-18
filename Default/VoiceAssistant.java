package Default;

public class VoiceAssistant 
{
	package Default;

	import org.vosk.Model;
	import org.vosk.Recognizer;
	import org.vosk.LibVosk;

	import javax.sound.sampled.*;

	public class VoiceAssistant implements Runnable {

	    @Override
	    public void run() {
	        try {
	            LibVosk.setLogLevel(0);
	            Model model = new Model("models/vosk-model-small-en-us-0.15");
	            Recognizer recognizer = new Recognizer(model, 16000.0f);

	            AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
	            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
	            microphone.open(format);
	            microphone.start();

	            System.out.println("🎤 Voice Assistant Started. Speak now...");

	            byte[] buffer = new byte[4096];
	            while (true) {
	                int bytesRead = microphone.read(buffer, 0, buffer.length);
	                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
	                    System.out.println("✅ Result: " + recognizer.getResult());
	                } else {
	                    System.out.println("🟡 Partial: " + recognizer.getPartialResult());
	                }
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

}
