package dev.nachwahl.bteginstaller;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class InstallUtil {
    ArrayList<OptionalMod> optionalMods = new ArrayList<OptionalMod>();
    JFrame frame;


    public InstallUtil(JFrame frame) {
        this.frame = frame;
        System.out.println("Init InstallUtil");
    }



    public void removeOptionalMod(OptionalMod optionalMod) {
        if(optionalMods.contains(optionalMod)) {
            optionalMods.remove(optionalMod);
        }
    }

    public void addOptionalMod(OptionalMod optionalMod) {
        if(!optionalMods.contains(optionalMod)) {
            optionalMods.add(optionalMod);
        }
    }

    public void setOptionalMod(OptionalMod optionalMod, boolean active){
        if(active){
            addOptionalMod(optionalMod);
        }else{
            removeOptionalMod(optionalMod);
        }
    }

    public boolean isOptionalModEnabled(OptionalMod optionalMod){
        return optionalMods.contains(optionalMod);
    }

    public static synchronized void playSound(final String name) {
        new Thread(() -> {
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                InputStream is = classloader.getResourceAsStream(name);
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(is);
                clip.open(inputStream);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-30.0f);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }


}

