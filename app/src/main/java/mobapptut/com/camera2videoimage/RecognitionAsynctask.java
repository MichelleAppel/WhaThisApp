package mobapptut.com.camera2videoimage;

import android.os.AsyncTask;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Standard AsyncTask class
public class RecognitionAsynctask extends AsyncTask<byte[],Integer,PlantInfo> {

    // Retrieve the information from the server on a seperate thread
    @Override
    protected PlantInfo doInBackground(byte[]... params) {
        byte[] image = params[0];
        PlantInfo result = null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();




        return result;
    }
}