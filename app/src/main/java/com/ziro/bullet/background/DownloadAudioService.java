package com.ziro.bullet.background;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;

import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.Func;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.FileDownloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

public class DownloadAudioService {
//    private static final String TAG = DownloadAudioService.class.getSimpleName();
    private Context context;
    private Fetch fetch;
    private TextToSpeech mTextToSpeech;

    public DownloadAudioService(Context context) {
        this.context = context;
        init();
    }

    /**
     * Generates an audio file from the stream. The file must be a WAV file.
     *
     * @param data       the byte array
     * @param outputFile the file in which to write the audio data could not be
     *                   written onto the file
     */
    public static void generateFile(byte[] data, File outputFile) {
        try {
            FileOutputStream os = new FileOutputStream(outputFile, true);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("CheckFile", "generateFile EXP = " + e.getMessage());
        }
    }

    private void init() {
        fetch = FileDownloader.getInstance(context).fetch();

        mTextToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                mTextToSpeech.setLanguage(Locale.US);
            }
        });
    }

    public void createFileForBoth_URL_TEXT(String id, int index, String url, String speech) {
//        File directory = new File(context.getFilesDir() + File.separator + id);
//        if (!directory.exists()) {
//            directory.mkdir();
//        }else {
//            //delete dir
//        }
//        new DownloadTask(context, directory, id, index, url, speech).execute();
    }

    public void startDownload(String id, int index, String url, String speech) {
        File directory = new File(context.getFilesDir() + File.separator + id);

        if (!directory.exists()) {
            directory.mkdir();
        }

        File newFile = makeFile(directory, index, "1_0");
        if (!newFile.exists()) {
            String filepath = newFile.getPath();
//            new DownloadTask(context, url, filepath).execute();
        }

        float speed_1_5 = 1.5f;
        float speed_2_0 = 2.0f;

////        file with 1.5 speed
//        createFile(directory, index, speech, speed_1_5, "1_5");
//        //file with 2.0 speed
//        createFile(directory, index, speech, speed_2_0, "2_0");
    }

    public void startCreatingFile(String id, int index, String speech) {
        File directory = new File(context.getFilesDir() + File.separator + id);

        if (!directory.exists()) {
            directory.mkdir();
        }

        float speed_1_0 = 1.0f;
        float speed_1_5 = 1.5f;
        float speed_2_0 = 2.0f;

//        //normal file with 1.0 speed rate
//        createFile(directory, index, speech, speed_1_0, "1_0");
//        //file with 1.5 speed rate
//        createFile(directory, index, speech, speed_1_5, "1_5");
//        //file with 2.0 speed rate
//        createFile(directory, index, speech, speed_2_0, "2_0");
    }

    private File makeFile(File directory, int index, String sp) {
        return new File(directory, sp + "_" + index + "." + Constants.file_format);
    }

    private void createFile(File directory, int index, String speech, float speed, String sp) {
        if (mTextToSpeech != null) {
            File newFile = makeFile(directory, index, sp);
            if (!newFile.exists()) {
                mTextToSpeech.setSpeechRate(speed);
                try {
                    Bundle bundle = new Bundle();
                    String mUtteranceId = "tts1" + System.currentTimeMillis();
                    bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speech);
                    mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
//                        Log.v("CheckFile", "onStart = " + utteranceId);
                        }

                        @Override
                        public void onDone(String utteranceId) {
//                        Log.v("CheckFile", "onDone = " + utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
//                        Log.v("CheckFile", "onError = " + utteranceId);
                        }

                        @Override
                        public void onAudioAvailable(String utteranceId, byte[] audio) {
                            super.onAudioAvailable(utteranceId, audio);
//                        Log.v("CheckFile", "onAudioAvailable = " + utteranceId);
                        }
                    });
                    int sr = mTextToSpeech.synthesizeToFile(speech, bundle, newFile, mUtteranceId);
                } catch (Exception e) {
                    Log.v("CheckFile", "EXP = " + e.getMessage());
                }
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        Context context;
        String url;
        String id;
        String speech;
        int index;
        File directory;

        public DownloadTask(Context context, File directory, String id, int index, String url, String speech) {
            this.context = context;
            this.directory = directory;
            this.url = url;
            this.id = id;
            this.speech = speech;
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... songDetails) {

            if (!TextUtils.isEmpty(url)) {
                //normal file with 1.0 speed rate
                File newFile = makeFile(directory, index, "1_0");

                if (!newFile.exists()) {
                    String filepath = newFile.getPath();
                    Request request = new Request(url, filepath);
                    request.setPriority(Priority.HIGH);
                    request.setNetworkType(NetworkType.ALL);
                    if (fetch != null) {
                        fetch.enqueue(request, new Func<Request>() {
                            @Override
                            public void call(@NotNull Request updatedRequest) {
                            }
                        }, new Func<Error>() {
                            @Override
                            public void call(@NotNull Error error) {
                            }
                        });
                    }
                }
//                if (audioPlayerHelper != null) {
//                    audioPlayerHelper.setAudioStreamId(newFile.getPath());
//                }
//                float speed_1_5 = 1.5f;
//                float speed_2_0 = 2.0f;
//
//                //file with 1.5 speed
//                createFile(directory, index, speech, speed_1_5, "1_5");
//                //file with 2.0 speed
//                createFile(directory, index, speech, speed_2_0, "2_0");
            }
//            else {
//
//                float speed_1_0 = 1.0f;
//                float speed_1_5 = 1.5f;
//                float speed_2_0 = 2.0f;
//
//                //normal file with 1.0 speed rate
//                createFile(directory, index, speech, speed_1_0, "1_0");
//                //file with 1.5 speed rate
//                createFile(directory, index, speech, speed_1_5, "1_5");
//                //file with 2.0 speed rate
//                createFile(directory, index, speech, speed_2_0, "2_0");
//            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
