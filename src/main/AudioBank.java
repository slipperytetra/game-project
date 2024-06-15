package main;

import level.Level;

import java.util.HashMap;

public class AudioBank {

    private Game game;
    private HashMap<SoundType, GameEngine.AudioClip> audioClips;

    public AudioBank(Game game) {
        this.game = game;
        this.audioClips = new HashMap<>();

        init();
    }

    public void init() {
        for (SoundType type : SoundType.values()) {
            addClip(type, game.loadAudio(type.getFilePath()));
        }
    }

    public HashMap<SoundType, GameEngine.AudioClip> getClips() {
        return audioClips;
    }

    public GameEngine.AudioClip getClip(SoundType type) {
        return audioClips.get(type);
    }

    public void addClip(SoundType type, GameEngine.AudioClip clip) {
        this.audioClips.put(type, clip);
    }

    public void removeClip(SoundType type) {
        this.audioClips.remove(type);
    }

    public void playSound(SoundType type) {
        game.playAudio(game.getAudioBank().getClip(type), type.getVolume());
    }

    public void playSound(SoundType type, boolean loop) {
        if (!loop) {
            game.playAudio(game.getAudioBank().getClip(type), type.getVolume());
        } else {
            game.startAudioLoop(game.getAudioBank().getClip(type), type.getVolume());
        }
    }

    public void playSound(SoundType type, boolean loop, float volume) {
        if (!loop) {
            game.playAudio(game.getAudioBank().getClip(type), volume);
        } else {
            game.startAudioLoop(game.getAudioBank().getClip(type), volume);
        }
    }

    public void stopSoundLoop(SoundType type) {
        game.stopAudioLoop(game.getAudioBank().getClip(type));
    }

    public static void playSound(SoundType type, Level level) {
        if (level == null) {
            return;
        }

        Game game = level.getManager().getEngine();
        level.getManager().getEngine().playAudio(game.getAudioBank().getClip(type), type.getVolume());
    }

    public static void playSound(SoundType type, Level level, float volume) {
        if (level == null) {
            return;
        }

        Game game = level.getManager().getEngine();
        level.getManager().getEngine().playAudio(game.getAudioBank().getClip(type), volume);
    }
}
