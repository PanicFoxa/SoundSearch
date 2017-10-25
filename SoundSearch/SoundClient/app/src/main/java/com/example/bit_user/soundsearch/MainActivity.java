package com.example.bit_user.soundsearch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String result="호랑이";
    String mdata;
    Button btn;
    TextView list;
    int size;
    Thread thr;
    Thread thr2;
    Thread thr3;
    private Socket socket;
    PrintWriter socket_out;
    InputStream socket_in;
    private VoiceRecognition voiceRecognition;

    public class VoiceRecognition {
        private PackageManager pm;
        public final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
        public VoiceRecognition(Context ctx) {
            this.pm = ctx.getPackageManager();
        }
        // 음성 인식을 지원하는지 확인
        public boolean recognitionAvailable() {
            List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (activities.size() != 0) {
                return true;
            } else {
                return false;
            }
        }
        // 구글 음성 인식 intent 생성
        public Intent getVoiceRecognitionIntent(String message) {
            Intent intent = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, message);
            return intent;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        list = (TextView) findViewById(R.id.list);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thr.start();
//                startVoiceRecognition();
            }
        });
        thr= new Thread() {
            public void run(){
                try{
                    socket=new Socket("192.168.1.93",5678);
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    socket_out.println(result);
                    thr2.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        thr2= new Thread() {
            public void run(){
                try{
                    socket_in = socket.getInputStream();
                    byte [] musicdata=new byte[1024];
                    size=socket_in.read(musicdata);
                    Intent intent = new Intent(getApplicationContext(), Searchlist.class);
                    if(size!=-1){
                        mdata=new String(musicdata,0,size,"UTF-8");
                    }
                    intent.putExtra("data",mdata);
                    intent.putExtra("result",result);
                    startActivityForResult(intent, 100);
                    socket_in.close();
                    socket_out.close();
                    socket.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };



    }

    // 음성 인식 시작
    private void startVoiceRecognition() {
        voiceRecognition = new VoiceRecognition(MainActivity.this);
        if (voiceRecognition.recognitionAvailable()) {
            Intent intent = voiceRecognition
                    .getVoiceRecognitionIntent("Speak now");
            startActivityForResult(intent,
                    voiceRecognition.VOICE_RECOGNITION_REQUEST_CODE);
        } else {
            Toast toast = Toast.makeText(MainActivity.this,
                    "Voice recognition is not available.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // 음성 인식 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==
                voiceRecognition.VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == -1) {
            result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS).get(0);
            // 이 부분에서 result 를 가지고 검색을 하거나, 명령을 실행 하면 됨
//            thr.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
