package com.example.se2einzelphase;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.se2einzelphase.databinding.FragmentFirstBinding;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class MainFragment extends Fragment {

    private FragmentFirstBinding binding;
    private EditText matrikelnummerInput;
    private Button oKButton;
    private TextView serverAntwortField;
    private TextView convertedMatrikelnummerField;

    private String tmpServerResponse;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        oKButton = binding.OKButton;
        serverAntwortField = binding.Serverantwort;
        convertedMatrikelnummerField = binding.MatrikelnummerConverted;
        matrikelnummerInput = binding.MatrikelnummerEingabe;
        oKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Thread serverInteractionThread = new Thread(new Runnable(){
                    @Override
                    public void run(){
                        try{
                            Socket clientSocket = new Socket("se2-isys.aau.at", 53212);

                            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            outputStream.writeBytes(matrikelnummerInput.getText().toString() + '\n');

                            tmpServerResponse = bufferedReader.readLine();

                            clientSocket.close();
                            bufferedReader.close();
                            outputStream.close();
                        }catch (IOException e){

                        }
                    }
                });
                serverInteractionThread.start();

                try{
                    serverInteractionThread.join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                serverAntwortField.setText(tmpServerResponse);

                char matrikelNummerConverted[] = matrikelnummerInput.getText().toString().toCharArray();
                for(int i = 1; i < matrikelNummerConverted.length;i=i+2){
                    matrikelNummerConverted[i] = (char)(matrikelNummerConverted[i] +48);
                }

                convertedMatrikelnummerField.setText(String.valueOf(matrikelNummerConverted));
            }
        });

        return binding.getRoot();

    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}