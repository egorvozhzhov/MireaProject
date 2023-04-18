package ru.mirea.vozhzhovea.mireaproject;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ru.mirea.vozhzhovea.mireaproject.databinding.FragmentSensoreBinding;



public class Sensore extends Fragment implements SensorEventListener {
    private FragmentSensoreBinding binding;
    private SensorManager sensorManager;
    private Sensor usingSensor;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Sensore() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sensore.
     */
    // TODO: Rename and change types and number of parameters
    public static Sensore newInstance(String param1, String param2) {
        Sensore fragment = new Sensore();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSensoreBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        usingSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);




        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, usingSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temper = event.values[0];
            binding.textViewTemp.setText(Float.toString(temper) + "°C");
            if (temper > 80) {
                binding.imageView5.setImageResource(R.drawable.sunhot);
                binding.textViewRec.setText("Очень жарко, прячьтесь!");
            } else if (temper > 30) {
                binding.imageView5.setImageResource(R.drawable.sun);
                binding.textViewRec.setText("Жарковато, пейте водичку!");
            } else if (temper >= 10) {
                binding.imageView5.setImageResource(R.drawable.comfort);
                binding.textViewRec.setText("Приятная погода!");
            } else if (temper < 10 && temper > -20) {
                binding.imageView5.setImageResource(R.drawable.sneg);
                binding.textViewRec.setText("Прохладно, не простудитесь");
            } else if (temper <= -20 && temper > -60) {
                binding.imageView5.setImageResource(R.drawable.holodno);
                binding.textViewRec.setText("Лучше остаться дома");
            }
             else if (temper <= -60) {
                binding.imageView5.setImageResource(R.drawable.rip);
                binding.textViewRec.setText("Вы мертвы скорее всего...");

            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



}