package ru.mirea.vozhzhovea.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.mirea.vozhzhovea.mireaproject.databinding.FragmentCameraBinding;
import ru.mirea.vozhzhovea.mireaproject.databinding.FragmentFileWorkBinding;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileWorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileWorkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FragmentFileWorkBinding binding;

    public FileWorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FileWorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileWorkFragment newInstance(String param1, String param2) {
        FileWorkFragment fragment = new FileWorkFragment();
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
        binding = FragmentFileWorkBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.ActDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = binding.editTextText.getText().toString();
                String desr = Decrypt(text, 4);
                FileOutputStream outputStream;
                try {
                    outputStream = getActivity().openFileOutput(binding.editTextFileName.getText().toString(), Context.MODE_PRIVATE);
                    outputStream.write(desr.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.ActEncr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = getTextFromFile();
                String encr = Encryption(text, 4);
                FileOutputStream outputStream;
                try {
                    outputStream = getActivity().openFileOutput(binding.editTextFileName.getText().toString(), Context.MODE_PRIVATE);
                    outputStream.write(encr.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = getTextFromFile();
                binding.editTextTextPersonName2.setText(text);
            }
        });


        return view;
    }


    public String getTextFromFile() {
        FileInputStream fin = null;
        try {
            fin = getActivity().openFileInput(binding.editTextFileName.getText().toString());
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            Log.d(LOG_TAG, text);
            return text;
        }
        catch (IOException ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show(); }
        }
        return null;
    }

    public String Decrypt(String str, int n) {
        // TODO Auto-generated method stub
        // расшифровываем
        int k=Integer.parseInt("-"+n);
        String string="";
        for(int i=0;i<str.length();i++) {
            char c=str.charAt(i);
            if(c>='a'&&c<='z')// Если символ в строке строчный
            {
                c+=k%26;// мобильный ключ% 26 бит
                if(c<'a')
                    c+=26;// слева налево
                if(c>'z')
                    c-=26;// направо
            }else if(c>='A'&&c<='Z')// Если символ в строке в верхнем регистре
            {
                c+=k%26;// мобильный ключ% 26 бит
                if(c<'A')
                    c+=26;// слева налево
                if(c>'Z')
                    c-=26;// направо
            }
            string +=c;// Объединяем расшифрованные символы в строку
        }
        return string;
    }

    public String Encryption(String str, int k) {
        // TODO Auto-generated method stub
        // Зашифровать
        String string="";
        for(int i=0;i<str.length();i++) {
            char c=str.charAt(i);
            if(c>='a'&&c<='z')// Если символ в строке строчный
            {
                c+=k%26;// мобильный ключ% 26 бит
                if(c<'a')
                    c+=26;// слева налево
                if(c>'z')
                    c-=26;// направо
            }else if(c>='A'&&c<='Z')// Если символ в строке в верхнем регистре
            {
                c+=k%26;// мобильный ключ% 26 бит
                if(c<'A')
                    c+=26;// слева налево
                if(c>'Z')
                    c-=26;// направо
            }
            string +=c;// Объединяем расшифрованные символы в строку
        }
        return string;

    }


}