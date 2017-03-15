package mobapptut.com.camera2videoimage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.app.Activity.RESULT_OK;

public class Fragment3 extends Fragment {
    private String speechResult = "";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private byte[] image;
    String base64image;
    TextView plant_description;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.activity_display);
        View frag3view = inflater.inflate(R.layout.frag1, container, false);
        Intent PrevScreenIntent = getActivity().getIntent();
        String photo = "photo";
        image = PrevScreenIntent.getByteArrayExtra(photo);
        base64image = Base64.encodeToString(image, Base64.NO_WRAP);

        //byte[] chartData
        ImageView imgViewer = (ImageView) getActivity().findViewById(R.id.chart_image);
        Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        imgViewer.setMinimumHeight(dm.heightPixels);
        imgViewer.setMinimumWidth(dm.widthPixels);
        imgViewer.setImageBitmap(bm);
        promptSpeechInput();
        return frag3view;

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
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        Toast.makeText(getActivity(),
                                "Couldn't hear what you  said", Toast.LENGTH_SHORT).show();
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                }
            }
            break;
        }
    }

    // Check if the users speech matches one of the predetermined sentences
    private void analyzePhoto(int query) throws MalformedURLException {

        TextView message = (TextView) getActivity().findViewById(R.id.textBychart);
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


    class ImageRequestBody {
        public ImageRequestBody(String image) {
            this.image = image;
        }

        String image;
    }
    class ImageResponse {
        String name;
        String description;
    }

    public interface TestPostImageService {
        @POST("image")
        Call<Fragment3.ImageResponse> postImage(@Body Fragment3.ImageRequestBody imageRequestBody);
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
    private void recollectData() {
        HttpLoggingInterceptor logginInterceptor = new HttpLoggingInterceptor();
        Fragment3.AuthInterceptor authInterceptor = new Fragment3.AuthInterceptor();

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

        Fragment3.TestPostImageService service = retrofit.create(Fragment3.TestPostImageService.class);

        service.postImage(new Fragment3.ImageRequestBody(base64image)).enqueue(new Callback<Fragment3.ImageResponse>() {
            @Override
            public void onResponse(Call<Fragment3.ImageResponse> call, Response<Fragment3.ImageResponse> response) {
                Log.d("image RESPONSE: ", response.body().description);
                if(response.body().description != null){
                    plant_description.setText(response.body().description);
                }
            }

            @Override
            public void onFailure(Call<Fragment3.ImageResponse> call, Throwable t) {

            }
        });


    }
}
