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
        url[0] = getClass().getResource("/res/tetoris-active.wav");
        if (url[0] == null) {
            throw new RuntimeException("Resource not found: /res/tetoris-active.wav");
        }
        url[1] = getClass().getResource("/res/delete line.wav");
        url[2] = getClass().getResource("/res/gameover.wav");
        url[3] = getClass().getResource("/res/rotation.wav");
        url[4] = getClass().getResource("/res/touch floor.wav");
        url[5] = getClass().getResource("/res/tetris-active.wav");
        url[6] = getClass().getResource("/res/marunouchi survivor.wav");
        url[7] = getClass().getResource("/res/utsuho incoming.wav");
        url[8] = getClass().getResource("/res/dragon defeated.wav");
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
            // Only close the clip for sound effects, not for music
            if (!music) {
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            }

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

    public void pause() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop(); // Pauses the music at the current position
        }
    }

    public void resume() {
        if (musicClip != null && !musicClip.isRunning()) {
            musicClip.start(); // Resumes from where it was paused
        }
    }
}
