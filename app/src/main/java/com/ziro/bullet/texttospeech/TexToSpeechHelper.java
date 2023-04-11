package com.ziro.bullet.texttospeech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TexToSpeechHelper {
    private Context mContext;
    private TextToSpeech mTextToSpeech;

    public TexToSpeechHelper(Context mContext) {
        this.mContext = mContext;
        init();
    }

    public void init() {
        mTextToSpeech = new TextToSpeech(mContext, status -> {
            if (status != TextToSpeech.ERROR) {
                mTextToSpeech.setLanguage(Locale.US);
            }
        });
    }

//    public void createMp3File(String inputText) {
//
//        HashMap<String, String> myHashRender = new HashMap<String, String>();
//        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, inputText);
//        Log.d("TAG", "successfully created hashmap");
//
//        String destFileName = envPath + "/" + "tts_file.wav";
//
//        int sr = mTextToSpeech.synthesizeToFile(inputText, null, null,"");
//        Log.d("TAG", "synthesize returns = " + sr);
//        File fileTTS = new File(destFileName);
//
//        if (fileTTS.exists()) {
//            Log.d("TAG", "successfully created fileTTS");
//        } else {
//            Log.d("TAG", "failed while creating fileTTS");
//        }
//
//        Uri fileUri = Uri.fromFile(fileTTS);
//        Log.d("TAG", "successfully created uri link: " + fileUri.getPath());
//    }

    public void start(String text) {
        if (mTextToSpeech != null) {
            if (mTextToSpeech.isSpeaking()) {
                onPause();
            }
            int result = mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1");

            if (result != TextToSpeech.SUCCESS) {
                init();
                mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1");
            }
        }
    }

    public void changeSpeed(float speed) {
        if (mTextToSpeech != null) {
            mTextToSpeech.setSpeechRate(speed);
        }
    }


    public boolean isSpeaking() {
        return mTextToSpeech != null && mTextToSpeech.isSpeaking();
    }

    public void onPause() {
        if (mTextToSpeech != null && mTextToSpeech.isSpeaking()) {
            mTextToSpeech.stop();
        }
    }

    public void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
        }
    }
}
