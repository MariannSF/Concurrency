package com.example.Concurrency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.pp.R;
import com.example.pp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ExecutorService threadPool;
    ActivityMainBinding binding;
    TextView textViewComplexity;
    SeekBar seekBar;
    double heavyWork;
    int complexityNumber;
    double displayData;
    ArrayList<Double> arrayList;
    ArrayAdapter adapter;
    ListView listView;
    int counter = 0;
    Double Average;
    ProgressBar progressBar;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Main Activity");

        progressBar = binding.progressBar2;
        handler = new Handler();
        arrayList = new ArrayList<>();
        listView = binding.listView;
        threadPool= Executors.newFixedThreadPool(2);
        textViewComplexity = binding.complexity;
        seekBar = binding.seekBar;
        //binding.listViewLayout2.setVisibility(View.INVISIBLE);


       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               textViewComplexity.setText(String.valueOf(i)+ " Times");
               complexityNumber = i;
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });


        binding.buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter=0;
                Average=0.0;
               // adapter.notifyDataSetChanged();
                arrayList.clear();

                listView.setAdapter(adapter);
                progressBar.setMax(complexityNumber);

                threadPool.execute(new DoWork(complexityNumber));


            }
        });


        adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1, android.R.id.text1, arrayList);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Log.d("demo", "Message Received: ");
                if(message.what == DoWork.PROGRESS){
                    binding.textViewProgress.setText(counter+"/"+complexityNumber);


                    progressBar.setProgress(counter);
                    displayData = message.getData().getDouble(DoWork.DATA);
                    arrayList.add(displayData);
                   // Average = (displayData+Average)/arrayList.size();


                    double total = 0.0;
                    double avg = 0.0;

                    for(int i = 0; i< arrayList.size(); i++){
                        total += arrayList.get(i);
                        avg = total/arrayList.size();
                        Log.d("demo", "avgADD: "+avg);

                    }
                    binding.textViewAverage.setText("Average: "+avg);
                    adapter.notifyDataSetChanged();

                }

                return false;
            }
        });

    }
    class DoWork implements Runnable{
        int number;
        static final String DATA = "DATA";
        static final int PROGRESS=3;

        DoWork (int complexity){
            number = complexity;

        }


        @Override
        public void run() {

           for(int i = 0; i< number; i++){
                heavyWork = HeavyWork.getNumber();
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putDouble(DATA, (Double) heavyWork);
                message.what = PROGRESS;
                message.setData(bundle);
                Log.d("demo", "run: " + heavyWork);
                handler.sendMessage(message);
                counter++;
            }

        }
    }
}