package mobapptut.com.camera2videoimage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity {

    private String speechResult = "";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent PrevScreenIntent = getIntent();
        String photo = "photo";
        byte[] image = PrevScreenIntent.getByteArrayExtra(photo);


        //byte[] chartData
        ImageView imgViewer = (ImageView) findViewById(R.id.chart_image);
        Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imgViewer.setMinimumHeight(dm.heightPixels);
        imgViewer.setMinimumWidth(dm.widthPixels);
        imgViewer.setImageBitmap(bm);

        promptSpeechInput();

    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechResult = result.get(0);
                    if (speechResult.toLowerCase().contains("wat is")){
                        analyzePhoto(0);   }
                    else if (speechResult.toLowerCase().contains("kleur")) {
                        analyzePhoto(1);  }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't hear what you  said", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }
            }
            break;
        }
    }

    private void analyzePhoto(int query){
        // Data recollection

        TextView message = (TextView) findViewById(R.id.textBychart);
        switch (query) {
            case 0:
                message.setText("Dit is een foto!");
                break;
            case 1:
                message.setText("De kleur is nog niet bepaald!");
                break;


        }

    }

}
