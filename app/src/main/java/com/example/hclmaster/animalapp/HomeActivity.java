package com.example.hclmaster.animalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class HomeActivity extends AppCompatActivity {

    private EditText editHost;
    private EditText editPort;
    private EditText editTopic;

    Button loadingBtn;
    ProgressDialog progressDialog;

    static String host_ip="tcp://47.106.81.90:1883";
    static String port_num="1883";
    static String topic_name="android_show_data";

    static String username = "niygsyck";
    static String password = "73EhAOevDQBQ";

    public static MqttAndroidClient client;

    private MqttConnectOptions options;

    // 振动
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), host_ip, clientId);

        options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

    }

    // 判断是否连接
    public void Conn(View view){
        // 这里创建一个Intent是为了打开新的界面的
        final Intent intent = new Intent(this, BrokerActivity.class);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(HomeActivity.this, "Connected!", Toast.LENGTH_LONG).show();
                    setSubscription();

                    // 打开另一个界面
                    startActivity(intent);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(HomeActivity.this, "Connection Failed!", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // 发布消息来进行远程遥控
    // 是为了来控制IoT设备的相机进行拍照的
    public void ControlPublish(View view){
        String pub_topic = "control_camera_iot";
        String msg = "capturePic";
        final Intent intent = new Intent(this, BrokerActivity.class);

        // 以下是初始化操作(初始化操作写在下面的connBtn里面了)
        // 不能写在一个函数中，会报错
        // 先连接,再publish
        try {
            client.publish(pub_topic, msg.getBytes(), 0, false);
            Toast.makeText(HomeActivity.this, "Publish Successful!", Toast.LENGTH_LONG).show();

            setSubscription();
            // 打开另一个界面
            startActivity(intent);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // 取消连接
    public void disConn(View view){
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(HomeActivity.this, "Disconnected!", Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(HomeActivity.this, "Could not disconnected!", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(){
        try {
            client.subscribe(topic_name, 0);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //4.27添加，是为了进行连接操作
    public void connBtn(View view){
        // 以下是初始化操作
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(HomeActivity.this, "Connected with the Broker!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(HomeActivity.this, "Connection Failed!", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
