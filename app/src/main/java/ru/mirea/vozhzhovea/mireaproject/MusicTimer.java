package ru.mirea.vozhzhovea.mireaproject;

import static android.Manifest.permission.FOREGROUND_SERVICE;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;

import ru.mirea.vozhzhovea.mireaproject.databinding.FragmentMusicTimerBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicTimer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicTimer extends Fragment {

    private int PermissionCode = 200;
    private FragmentMusicTimerBinding binding;


    private long startTime;
    private boolean isWork;
    private static final int REQUEST_CODE_PERMISSION =200;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MusicTimer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkerPart.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicTimer newInstance(String param1, String param2) {
        MusicTimer fragment = new MusicTimer();
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
        binding = FragmentMusicTimerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        if (ContextCompat.checkSelfPermission(getContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(MainActivity.class.getSimpleName().toString(), "Разрешения получены"); }
        else {
            Log.d(MainActivity.class.getSimpleName().toString(), "Нет разрешений!");
            ActivityCompat.requestPermissions(getActivity(), new String[]{POST_NOTIFICATIONS, FOREGROUND_SERVICE}, PermissionCode);
        }
        int permission = ContextCompat.checkSelfPermission(getContext(), POST_NOTIFICATIONS);
        if (permission == PackageManager.PERMISSION_GRANTED){
            isWork = true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{POST_NOTIFICATIONS}, REQUEST_CODE_PERMISSION);
        }
        binding.buttonStop.setEnabled(false);
        binding.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    binding.buttonStart.setEnabled(false);
                    Intent serviceIntent = new Intent(getActivity(), PlayerService.class);
                    getActivity().startForegroundService(serviceIntent);
                    startTime = System.currentTimeMillis();
                    binding.buttonStop.setEnabled(true);

            }
        });
        binding.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                    binding.buttonStop.setEnabled(false);
                    binding.buttonStart.setEnabled(true);
                    getActivity().stopService(new Intent(getActivity(), PlayerService.class));
                    Log.d("dsa", Long.toString(startTime));
                    long difference = (System.currentTimeMillis() - startTime)/1000;
                    binding.textView3.setText("Вы слушали в секундах: "+ difference);



        } });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_PERMISSION:
                isWork = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

    }
}