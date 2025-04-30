package main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class Sound {
    private Clip musicClip;
    private List<Clip> activeClips = new ArrayList<>(); // Track all active clips
    URL url[] = new URL[10];
    FloatControl volume;

    public Sound() {
        url[0] = getClass().getResource("/tetoris-active.wav");
        if (url[0] == null) {
            throw new RuntimeException("Resource not found: /tetoris-active.wav");
        }
        url[1] = getClass().getResource("/delete line.wav");
        url[2] = getClass().getResource("/gameover.wav");
        url[3] = getClass().getResource("/rotation.wav");
        url[4] = getClass().getResource("/touch floor.wav");
        url[5] = getClass().getResource("/tetris-active.wav");
        url[6] = getClass().getResource("/marunouchi survivor.wav");
        url[7] = getClass().getResource("/utsuho incoming.wav");
    }

    public void play(int i, boolean music) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();

            if (music) {
                if (musicClip != null && musicClip.isRunning()) {
                    musicClip.stop(); // Stop the previous music
                }
                musicClip = clip; // Set the new music clip
            }

            clip.open(ais);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            activeClips.add(clip); // Track this clip
            ais.close();
            volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10.0f);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        if (musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        // Stop the main music clip
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }

        // Stop all active clips
        for (Clip clip : activeClips) {
            if (clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
        activeClips.clear(); // Clear the list of active clips
    }

    public void setVolume(float value) {
        if (volume != null) {
            volume.setValue(value);
        }
    }   
}
