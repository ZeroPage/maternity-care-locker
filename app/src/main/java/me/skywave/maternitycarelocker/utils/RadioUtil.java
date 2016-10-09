package me.skywave.maternitycarelocker.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class RadioUtil {
    private static MediaPlayer mediaPlayer;
    private static String url = "http://uk3.internet-radio.com:8180";

    public static void play() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
            } catch (IOException e) {
                mediaPlayer = null;
                return;
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null;
    }
}
