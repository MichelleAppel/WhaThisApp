package mobapptut.com.camera2videoimage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;

public class DisplayActivity extends AppCompatActivity {

    private String speechResult = "";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private byte[] image;
    String base64image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent PrevScreenIntent = getIntent();
        String photo = "photo";
        image = PrevScreenIntent.getByteArrayExtra(photo);
        base64image = Base64.encodeToString(image, Base64.DEFAULT);

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

                    // Check if the users speech matches one of the predetermined sentences
                    if (speechResult.toLowerCase().contains("wat is")){
                        try {
                            analyzePhoto(0);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (speechResult.toLowerCase().contains("kleur")) {
                        try {
                            analyzePhoto(1);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
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

    // Check if the users speech matches one of the predetermined sentences
    private void analyzePhoto(int query) throws MalformedURLException {

        TextView message = (TextView) findViewById(R.id.textBychart);
        switch (query) {
            case 0:
                message.setText("Dit is een foto!");
                recollectData();
                break;
            case 1:
                message.setText("De kleur is nog niet bepaald!");
                recollectData();
                break;
        }

    }
    private class imageObject{
        public imageObject(String image) {
            this.image = image;
        }

        public String image;
    }

    private class imageResponse {
        public String description;
    }


    public interface imageInterface{
        @POST("image")
        Call<imageResponse> getImage(imageObject imageObject);

    }

    // Use HTTP request to get info from the image
    private void recollectData(){

        imageObject imageObject = new imageObject(base64image);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mu.yrck.nl/")
                .build();

       imageInterface service = retrofit.create(imageInterface.class);

        Call<imageResponse> image = service.getImage(imageObject);
        image.enqueue(new Callback<imageResponse>() {
            @Override
            public void onResponse(Call<imageResponse> call, Response<imageResponse> response) {
                response.body();
            }
            @Override
            public void onFailure(Call<imageResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Couldn retrieve data from plant!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
