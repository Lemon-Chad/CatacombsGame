package com.lemon.catacombs.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioHandler {
    public static class Sound {
        private final String name;
        private final Clip clip;
        private final AudioInputStream stream;

        public Sound(String name, Clip clip, AudioInputStream stream) {
            this.name = name;
            this.clip = clip;
            this.stream = stream;
        }

        public String getName() {
            return name;
        }

        public Clip getClip() {
            return clip;
        }

        public AudioInputStream getStream() {
            return stream;
        }

        public void play() {
            clip.setFramePosition(0);
            clip.start();
        }

        public void stop() {
            clip.stop();
        }

        public void resume() {
            clip.start();
        }

        public void loop() {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }

        public void close() {
            clip.close();
        }

        public boolean isPlaying() {
            return clip.isRunning();
        }

        public boolean isStopped() {
            return !clip.isRunning();
        }

        public float getVolume() {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            return gainControl.getValue();
        }

        public void setVolume(float volume) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));
        }
    }

    public Sound sound(String sound) {
        try {
            URL url = getClass().getResource(sound);
            assert url != null;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return new Sound(sound, clip, audioInputStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Sound playSound(String sound) {
        Sound s = sound(sound);
        s.play();
        s.getClip().addLineListener(e -> {
            if (LineEvent.Type.STOP.equals(e.getType())) {
                s.close();
            } 
        });
        return s;
    }
}
