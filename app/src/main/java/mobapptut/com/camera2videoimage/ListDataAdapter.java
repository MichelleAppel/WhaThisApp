package mobapptut.com.camera2videoimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sjors on 17-3-2017.
 */

public class ListDataAdapter extends ArrayAdapter<PlantInfo> {

    // constructor
    public ListDataAdapter(Context context, ArrayList<PlantInfo> plants) {
        super(context, R.layout.plant_list_item, plants);
    }

    // adapting array to fit joke_list_item layout
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.plant_list_item, parent, false);

        TextView plantName = (TextView) view.findViewById(R.id.plant_name);

        plantName.setText(getItem(position).getName());

        return view;
    }
}