package mobapptut.com.camera2videoimage;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class Fragment2 extends Fragment {

    private ArrayList<PlantInfo> plants = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag2,container,false);

        plants.add(new PlantInfo("test1"));
        plants.add(new PlantInfo("test2"));
        plants.add(new PlantInfo("test3"));
        plants.add(new PlantInfo("test4"));

        ListView jokeList = (ListView) view.findViewById(R.id.plant_list);
        ListDataAdapter adapter = new ListDataAdapter(this.getContext(), plants);
        jokeList.setAdapter(adapter);

        return view;
    }
}
