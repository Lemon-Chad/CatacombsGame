package com.lemon.catacombs.engine;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AudioHandler {
    private int loopCount = 0;
    private final Map<Integer, Sound> loops = new HashMap<>();
    private final Set<Integer> toStop = new HashSet<>();

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

    public int playSound(String sound, float volume, boolean loop) {
        int id = loopCount++;
        new Thread(() -> {
            Sound s = sound(sound);
            s.setVolume(volume);
            if (loop) {
                s.loop();
                loops.put(id, s);
                if (toStop.contains(id)) {
                    s.stop();
                    s.close();
                    toStop.remove(id);
                    loops.remove(id);
                    return;
                }
            }
            s.play();
            s.getClip().addLineListener(e -> {
                if (LineEvent.Type.STOP.equals(e.getType())) {
                    s.close();
                }
            });
        }).start();
        return id;
    }

    public int playSound(String sound, float volume) {
        return playSound(sound, volume, false);
    }

    public int playSound(String sound) {
        return playSound(sound, 1f);
    }

    public void stopSound(int id) {
        if (loops.containsKey(id)) {
            loops.get(id).stop();
        } else {
            toStop.add(id);
        }
    }
}
