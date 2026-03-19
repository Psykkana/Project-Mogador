import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class Sound {
    private Clip bgmClip;
    private FloatControl fc;
    private Thread fadeThread;
    private URL[] soundURL = new URL[11];

    private int currentTrackIndex = -1; // -1 means nothing loaded
    private float currentVolume = 0.5f; 

    // This Map will store all our short SE clips ready to play instantly
    private Map<Integer, Clip> seClips = new HashMap<>();

    public Sound() {
        // Music
        soundURL[0] = getClass().getResource("/assets/audio/bgm_main.wav");
        soundURL[1] = getClass().getResource("/assets/audio/eerieworld.wav");

        // Sound Effects     
        soundURL[6] = getClass().getResource("/assets/audio/textsingle.wav");        
        soundURL[7] = getClass().getResource("/assets/audio/buttonclick.wav");
        soundURL[8] = getClass().getResource("/assets/audio/buttonclickrelease.wav");
        soundURL[9] = getClass().getResource("/assets/audio/buttonrollover.wav");
        soundURL[10] = getClass().getResource("/assets/audio/door_sfx.wav");        

        // Pre-load all Sound Effects (Indices 6-9)
        for (int i = 6; i <= 10; i++) {
            if (soundURL[i] != null) {
                seClips.put(i, loadClip(soundURL[i]));
            }
        }
    }

    public String getDebugStatus() {
        StringBuilder sb = new StringBuilder();
        
        // --- BGM Section ---
        String trackName = "None";
        switch (currentTrackIndex) {
            case 0 -> trackName = "BGM_MAIN";
            case 1 -> trackName = "EERIE_WORLD";
        }
        String bgmStatus = isPlaying() ? "PLAYING" : "STOPPED";
        sb.append(String.format("BGM: %s (%d)\n", trackName, currentTrackIndex));
        sb.append(String.format("BGM STATUS: %s\n", bgmStatus));
        sb.append(String.format("BGM VOL: %d%%\n", (int)(currentVolume * 100)));
        
        // --- SE Section (Detecting Multiple) ---
        sb.append("--- ACTIVE SE ---\n");
        boolean anySE = false;
        for (Map.Entry<Integer, Clip> entry : seClips.entrySet()) {
            Clip c = entry.getValue();
            if (c != null && c.isRunning()) {
                sb.append(String.format("SE ID %d: RUNNING\n", entry.getKey()));
                anySE = true;
            }
        }
        if (!anySE) sb.append("None\n");

        return sb.toString();
    }

    // Volume setter
    public void setVolume(float volume) { // volume is 0.0f to 1.0f
    if (fc != null) {
        // This formula converts a 0-1 range into a proper decibel curve
        float dB = (float) (Math.log10(Math.max(volume, 0.0001)) * 20);
        fc.setValue(dB);
    }
}

    public void stopFade() {
        if (fadeThread != null && fadeThread.isAlive()) {
            fadeThread.interrupt();
        }
    }

    public void fadeIn(int trackIndex) {
        stopFade(); // Always kill previous threads first

        setFile(trackIndex);
        play();
        loop();

        fadeThread = new Thread(() -> {
            try {
                // Start from 0 for a true fade
                float vol = 0f; 
                while (vol < currentVolume) {
                    vol += 0.01f; 
                    if (vol > currentVolume) vol = currentVolume;
                    
                    setVolume(vol);
                    Thread.sleep(30);
                }
            } catch (InterruptedException e) {
                // If interrupted (like by opening the menu), 
                // ensure we at least land on the correct volume
                setVolume(currentVolume);
            }
        });
        fadeThread.start();
    }

    public void fadeOut() {
        if (fadeThread != null && fadeThread.isAlive()) fadeThread.interrupt();

        fadeThread = new Thread(() -> {
            try {
                float vol = currentVolume; 
                while (vol > 0) {
                    vol -= 0.01f; // Changed from 0.5f to 0.01f
                    if (vol < 0) vol = 0;
                    setVolume(vol);
                    Thread.sleep(30);
                }
                stop();
            } catch (InterruptedException e) { /* Thread stopped */ }
        });
        fadeThread.start();
    }

    // Helper to load a clip into memory
    private Clip loadClip(URL url) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.err.println("Error pre-loading: " + url + " | " + e.getMessage());
            return null;
        }
    }

    // Use this for UI and Typewriter sounds
    public void playSE(int i) {
        Clip c = seClips.get(i);
        if (c != null) {
            c.setFramePosition(0); // Rewind
            c.start();
        }
    }

    /* 
     * BGM METHODS (Still using setFile style because BGM is too large to keep many in RAM)
     */
    public void setFile(int i) {
        try {
            this.currentTrackIndex = i;
            if (bgmClip != null && bgmClip.isOpen()) bgmClip.close();
            
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            
            fc = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);

            // This is CRITICAL: Force the volume to match the slider immediately
            setVolume(currentVolume); 
        } catch (Exception e) {
            System.err.println("BGM Error: " + e.getMessage());            
            this.currentTrackIndex = -1;
        }
    }

    public boolean isPlaying() {
        return bgmClip != null && bgmClip.isRunning();
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public void play() { 
        if (bgmClip != null) 
            bgmClip.start(); 
    }

    public void loop() { 
        if (bgmClip != null) 
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); 
    }

    public void stop() { 
        if (bgmClip != null) 
            bgmClip.stop(); 
    }
}