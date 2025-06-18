package util;
import com.sun.speech.freetts.*;
public class TextToSpeech 
{
    private Voice voice;
    public TextToSpeech() 
    {
        System.setProperty("freetts.voices","com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm=VoiceManager.getInstance();
        voice=vm.getVoice("kevin16");
        if(voice==null) 
        {
            System.err.println("Voice not found");
            return;
        }
        voice.allocate();
    }

    
    public void speak(String text) 
    {
        if(voice!=null)
            voice.speak(text);
    }

    
    public void stop() 
    {
        if(voice!=null)
        {
            voice.deallocate();
        }
    }
}
