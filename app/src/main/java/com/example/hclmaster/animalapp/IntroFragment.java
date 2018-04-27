package com.example.hclmaster.animalapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends Fragment {

    int[] IMAGES = {R.drawable.bird, R.drawable.dog, R.drawable.cat,
                    R.drawable.tiger, R.drawable.clown_fish};

    String[] NAMES = {"鸟", "狗", "猫", "虎", "鱼"};

    String[] engNames = {"Birds", "Dogs", "Cats", "Tigers", "Fishes"};

    public static IntroFragment newInstance(String info){
        Bundle args = new Bundle();
        IntroFragment fragment = new IntroFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    public IntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listview, container, false);

        ListView listView = view.findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        // 4-14加
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent myIntent = new Intent(view.getContext(), BirdsActivity.class);
                    startActivityForResult(myIntent, 0);
                }
                if(position==1){
                    Intent myIntent = new Intent(view.getContext(), DogsActivity.class);
                    startActivityForResult(myIntent, 1);
                }
                if(position==2){
                    Intent myIntent = new Intent(view.getContext(), CatsActivity.class);
                    startActivityForResult(myIntent, 2);
                }
                if(position==3){
                    Intent myIntent = new Intent(view.getContext(), TigersActivity.class);
                    startActivityForResult(myIntent, 3);
                }
                if(position==4){
                    Intent myIntent = new Intent(view.getContext(), FishesActivity.class);
                    startActivityForResult(myIntent, 4);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView= getLayoutInflater().inflate(R.layout.customlayout, null);
            ImageView imageView = convertView.findViewById(R.id.imageView2);
            TextView txt_name = convertView.findViewById(R.id.textView_name);
            TextView txt_engName = convertView.findViewById(R.id.textView_engName);

            imageView.setImageResource(IMAGES[position]);
            txt_name.setText(NAMES[position]);
            txt_engName.setText(engNames[position]);

            return convertView;
        }
    }

}
