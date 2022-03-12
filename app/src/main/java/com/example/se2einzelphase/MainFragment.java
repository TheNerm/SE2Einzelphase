package com.example.se2einzelphase;

import android.os.AsyncTask;
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
    private Button calcButton;
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
        calcButton = binding.BerechnenButton;

        oKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMatrikelNRfromServer();
            }
        });

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCalculation();
            }
        });

        return binding.getRoot();
    }

    private void getMatrikelNRfromServer(){
        Thread serverInteractionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    Socket clientSocket = new Socket("se2-isys.aau.at", 53212);

                    DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outputStream.writeBytes(matrikelnummerInput.getText().toString() + '\n');

                    tmpServerResponse = inputStream.readLine();

                    clientSocket.close();
                    inputStream.close();
                    outputStream.close();

                }catch (IOException e){
                    e.printStackTrace();
                    tmpServerResponse = "Error occurred. Please try again";
                }
            }
        });
        serverInteractionThread.start();

        try{
            serverInteractionThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
            tmpServerResponse = "Error occurred. Please try again";
        }

        serverAntwortField.setText(tmpServerResponse);
    }

    private void doCalculation(){
        char matrikelNummerConverted[] = matrikelnummerInput.getText().toString().toCharArray();

        for(int i = 1; i < matrikelNummerConverted.length;i=i+2){
            //Note: when converting 0 with this method, the character ' is the result
            //Since the exercise sheet didn't specify anything regarding this, it was assumed that this is the intended approach
            matrikelNummerConverted[i] = (char)(matrikelNummerConverted[i] +48);
        }
        convertedMatrikelnummerField.setText(String.valueOf(matrikelNummerConverted));
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