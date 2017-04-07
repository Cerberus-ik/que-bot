package net.cerberus.queueBot.io;


import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class SoundManager {

    private Sound alertSound;

    public SoundManager() {
    }

    public void initialize(){
        TinySound.init();
        TinySound.setGlobalVolume(0.2);

        alertSound = TinySound.loadSound(new ResourceLoader().getResourceFile("sounds/alert.wav"));;
    }

    public void alert(){
        alertSound.play();
    }

    public void shutdown(){
        TinySound.shutdown();
    }
}
