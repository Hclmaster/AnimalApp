package com.example.hclmaster.animalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;

public class BrokerActivity extends AppCompatActivity {

    private TextView resultTxt;
    private ImageView imageView;
    private int flag;
    private int whichAnimal;

    private MqttAndroidClient client;

    // 振动
    private Vibrator vibrator;
    // 响铃
    private Ringtone myRingtone;
    // 等待进度框
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker);

        flag = 0;
        whichAnimal = -1;

        resultTxt = (TextView)findViewById(R.id.resultText);
        imageView = (ImageView)findViewById(R.id.captureImg);

        client = HomeActivity.client;

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        /////////////////////////////////////////////////////////////
        progressDialog = new ProgressDialog(BrokerActivity.this);
        progressDialog.setTitle("Connect");
        progressDialog.setMessage("Loading...Plz wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        /* 开启一个新线程，在新线程里执行耗时的方法 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                spandTimeMethod();// 耗时的方法
                handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
            }

        }).start();
        /////////////////////////////////////////////////////////////

        // 下面那个是设置回调函数
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            // 接收到消息
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                flag = 1;
                whichAnimal = -1;
                // 接收到的消息
                String msg = new String(message.getPayload());
                System.out.println("看看收到的消息是什么? "+msg);

                if (msg == null || msg.isEmpty()){
                    resultTxt.setText("预测结果为: 未知...");
                }else{
                    JSONObject obj = new JSONObject(msg);
                    DecimalFormat df = new DecimalFormat("0.0");
                    double tiger = obj.getDouble("tiger");
                    double dog = obj.getDouble("dog");
                    double cat = obj.getDouble("cat");
                    double bird = obj.getDouble("bird");
                    double fish = obj.getDouble("fish");
                    String img_url = obj.getString("img_url");

                    double[] li = new double[5];
                    li[0] = tiger;
                    li[1] = dog;
                    li[2] = cat;
                    li[3] = bird;
                    li[4] = fish;
                    Arrays.sort(li);
                    double third_prob=li[2], second_prob=li[3], first_prob=li[4];
                    String first_key = "预测结果为: 未知...";

                    if(first_prob == tiger) {
                        first_key = "预测结果为: 虎: "+df.format(first_prob*100.0)+"%";
                        whichAnimal = 0;
                    }
                    if(first_prob == dog) {
                        first_key = "预测结果为: 狗: "+df.format(first_prob*100.0)+"%";
                        whichAnimal = 1;
                    }
                    if(first_prob == cat) {
                        first_key = "预测结果为: 猫: "+df.format(first_prob*100.0)+"%";
                        whichAnimal = 2;
                    }
                    if(first_prob == bird) {
                        first_key = "预测结果为: 鸟: "+df.format(first_prob*100.0)+"%";
                        whichAnimal = 3;
                    }
                    if(first_prob == fish) {
                        first_key = "预测结果为: 鱼: "+df.format(first_prob*100.0)+"%";
                        whichAnimal = 4;
                    }

                    resultTxt.setText(first_key);

                    Picasso.get().load(img_url).into(imageView);
                }
                vibrator.vibrate(500);
                myRingtone.play();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void spandTimeMethod(){
        /*try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        while(flag == 0){
            if(flag == 1) break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            progressDialog.dismiss();// 关闭ProgressDialog
        }
    };

    // 查看详细信息的onClick事件
    public void viewDetail(View view){
        if(whichAnimal == 0){
            Intent intent = new Intent(this, TigersActivity.class);
            startActivity(intent);
        }else if(whichAnimal == 1){
            Intent intent = new Intent(this, DogsActivity.class);
            startActivity(intent);
        }else if(whichAnimal == 2){
            Intent intent = new Intent(this, CatsActivity.class);
            startActivity(intent);
        }else if(whichAnimal == 3){
            Intent intent = new Intent(this, BirdsActivity.class);
            startActivity(intent);
        }else if(whichAnimal == 4){
            Intent intent = new Intent(this, FishesActivity.class);
            startActivity(intent);
        }
    }

}
