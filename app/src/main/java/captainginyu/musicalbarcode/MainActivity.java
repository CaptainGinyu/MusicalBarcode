package captainginyu.musicalbarcode;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button scanButton = (Button) findViewById(R.id.button);

        final IntentIntegrator intentIntegrator = new IntentIntegrator(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setPrompt("Scan barcode for music!");
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();
            }
        });
    }

    public void playAudio(final LinkedList<String> audioFileNames) {
        try {
            String currAudioFileName = audioFileNames.pop();
            Log.i("Played file", currAudioFileName);
            AssetFileDescriptor assetFileDescriptor = getAssets()
                    .openFd(currAudioFileName);
            long start = assetFileDescriptor.getStartOffset();
            long end = assetFileDescriptor.getLength();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    start, end);
            assetFileDescriptor.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (audioFileNames.size() > 0) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playAudio(audioFileNames);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator
                .parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                String barcode = intentResult.getContents();
                Toast.makeText(MainActivity.this,
                        intentResult.getContents(), Toast.LENGTH_LONG).show();

                LinkedList<String> audioFileNamesReversed = new LinkedList<String>();
                for (int i = 0; i < barcode.length(); i++) {
                    final char currChar = barcode.charAt(i);
                    if (Character.isDigit(currChar)) {
                        int currInt = Integer.parseInt(Character.toString(currChar));
                        String audioFileName = "";

                        if (currInt == 0) {
                            audioFileName = "a1.mp3";
                        } else if (currInt == 1) {
                            audioFileName = "a1s.mp3";
                        } else if (currInt == 2) {
                            audioFileName = "b1.mp3";
                        } else if (currInt == 3) {
                            audioFileName = "c1.mp3";
                        } else if (currInt == 4) {
                            audioFileName = "c1s.mp3";
                        } else if (currInt == 5) {
                            audioFileName = "d1.mp3";
                        } else if (currInt == 6) {
                            audioFileName = "d1s.mp3";
                        } else if (currInt == 7) {
                            audioFileName = "e1.mp3";
                        } else if (currInt == 8) {
                            audioFileName = "f1.mp3";
                        } else if (currInt == 9) {
                            audioFileName = "f1s.mp3";
                        }

                        audioFileNamesReversed.push(audioFileName);
                    }
                }

                LinkedList<String> audioFileNames = new LinkedList<String>();

                while (audioFileNamesReversed.size() > 0) {
                    audioFileNames.push(audioFileNamesReversed.pop());
                }

                playAudio(audioFileNames);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
