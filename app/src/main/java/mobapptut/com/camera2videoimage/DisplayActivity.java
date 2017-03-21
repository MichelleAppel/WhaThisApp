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
import java.util.Random;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class DisplayActivity extends AppCompatActivity {

    private String speechResult = "";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private byte[] image;
    String base64image;
    TextView plantText;
    TextView serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent PrevScreenIntent = getIntent();
        String photo = "photo";
        image = PrevScreenIntent.getByteArrayExtra(photo);
        base64image = Base64.encodeToString(image, Base64.NO_WRAP);

        //byte[] chartData
        ImageView imgViewer = (ImageView) findViewById(R.id.chart_image);
        plantText = (TextView) findViewById(R.id.plantText);
        serverResponse = (TextView) findViewById(R.id.serverResponse);
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

                    analyseText(speechResult.toLowerCase());

                }
            }
            break;
        }
    }


    private void analyseText(String speechQuery){
        if (speechQuery.toLowerCase().contains("wat is")||
                speechQuery.contains("what is")){

                analyzePhoto(0);

        }
        else if (speechQuery.contains("kleur")||
                speechQuery.contains("color")) {
            analyzePhoto(1);

        }
        else if (speechQuery.contains("naam")||
                speechQuery.contains("name")) {
            analyzePhoto(2);
        }
        else if (speechQuery.contains("waar")||
                speechQuery.contains("where")||
                speechQuery.contains("origin")) {
            analyzePhoto(3);
        }
        else if (speechQuery.contains("bloei")||
                speechQuery.contains("bloeit")||
                speechQuery.contains("bloom")) {
            analyzePhoto(4);
        }
        else if (speechQuery.contains("groot")||
                speechQuery.contains("hoog")||
                speechQuery.contains("size")||
                speechQuery.contains("height")) {
            analyzePhoto(5);
        }
        else if (speechQuery.contains("geslacht")||
                speechQuery.contains("soort")||
                speechQuery.contains("familie")||
                speechQuery.contains("genus")||
                speechQuery.contains("species")) {
            analyzePhoto(6);
        }
        else if (speechQuery.contains("fun")||
                speechQuery.contains("feit")||
                speechQuery.contains("feitje")||
                speechQuery.contains("weetje")||
                speechQuery.contains("random")||
                speechQuery.contains("trivia")) {
            analyzePhoto(7);
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Couldn't hear what you  said", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }


    class ImageRequestBody {
        public ImageRequestBody(String image) {
            this.image = image;
        }

        String image;
    }
    class ImageResponse {
        String ancientNameMeaning;
        String origin;
        String genus;
        String bloom;
        String possibleColors;
        String size;
        String name;
        String funfact1;
        String funfact2;
        String funfact3;
    }

    public interface TestPostImageService {
        @POST("image")
        Call<ImageResponse> postImage(@Body ImageRequestBody imageRequestBody);
    }

    class AuthInterceptor implements Interceptor {
        private final String auth = BuildConfig.API_USERNAME + ":" + BuildConfig.API_PASSWORD;

        private String getAuthString() {
            return Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Basic " + getAuthString());

            Request newRequest = builder.build();

            return chain.proceed(newRequest);
        }
    }



    // Use HTTP request to get info from the image
    private void analyzePhoto(final int questionNumber) {


        HttpLoggingInterceptor logginInterceptor = new HttpLoggingInterceptor();
        AuthInterceptor authInterceptor = new AuthInterceptor();

        logginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logginInterceptor)
                .addInterceptor(authInterceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mu.yrck.nl/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        TestPostImageService service = retrofit.create(TestPostImageService.class);

        service.postImage(new ImageRequestBody(base64image)).enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if(response.body() != null){
                    // make a plant object with all retrieved data
                    plantText.setText(response.body().name);


                    switch (questionNumber) {
                        case 0:
                            //Plant info recollection
//                            serverResponse.setText(response.body().name);
                            break;
                        case 1:
                            serverResponse.setText(response.body().possibleColors);
                            break;
                        case 2:
//                            serverResponse.setText(response.body().name);
                            break;
                        case 3:
                            serverResponse.setText(response.body().origin);
                            break;
                        case 4:
                            serverResponse.setText(response.body().bloom);
                            break;
                        case 5:
                            serverResponse.setText(response.body().size);
                            break;
                        case 6:
                            serverResponse.setText(response.body().genus);
                            break;
                        case 7:
                            Random rand = new Random();
                            int choice = rand.nextInt(2)+1;
                            if ( choice == 1){
                                serverResponse.setText(response.body().funfact1);
                            }
                            if ( choice == 2){
                                serverResponse.setText(response.body().funfact2);
                            }
                            if ( choice == 3){
                                serverResponse.setText(response.body().funfact3);
                            }
                              break;


                }
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });

    }
}
