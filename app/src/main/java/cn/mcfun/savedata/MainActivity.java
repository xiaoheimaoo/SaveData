package cn.mcfun.savedata;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    final String path = "/Android/data/com.aniplex.fategrandorder/files/data/54cc790bf952ea710ed7e8be08049531";
    final String del = "/Android/data/com.aniplex.fategrandorder/files/data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        while(!checkPermission()){
            try {
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView lblTitle = findViewById(R.id.editText1);
        String data = null;
        try {
            data = FileUtil.getFile(path);
/*            JSONObject jsonobj = new JSONObject(FileUtil.decrypt(data));
            data = "{\"userId\":"+jsonobj.getString("userId")+",\"authKey\":\""+jsonobj.getString("authKey")+"\",\"secretKey\":\""+jsonobj.getString("authKey")+"\"}";
            data = FileUtil.encrypt(data);*/
            lblTitle.setText(data);
        } catch (Exception e) {
            lblTitle.setText("权限不足或未找到存档码文件！");
        }
        Button button1 = findViewById(R.id.buttonCopy1);
        button1.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           TextView lblTitle = findViewById(R.id.editText1);
                                           ClipboardManager cbm= (ClipboardManager) MainActivity.this
                                                   .getSystemService(Context.CLIPBOARD_SERVICE);
                                           cbm.setText(lblTitle.getText());
                                           Toast.makeText(MainActivity.this, "存档码复制成功！",Toast.LENGTH_LONG).show();
                                       }
                                   }
        );
        Button button2 = findViewById(R.id.buttonImport1);
        button2.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           TextView lblTitle = findViewById(R.id.editText1);
                                           try {
                                               String data = FileUtil.decrypt(lblTitle.getText().toString());
                                               if(data == null || data.equals("") || data.trim().equals("")){
                                                   Toast.makeText(MainActivity.this, "存档码格式错误！",Toast.LENGTH_LONG).show();
                                               }else{
                                                   FileUtil.deletefile(del);
                                                   JSONObject json = new JSONObject(data);
                                                   if(json.getString("userId").length() <= 7){
                                                       FileUtil.saveFile("7AE=",FileUtil.encrypt(data),path);
                                                   }else{
                                                       FileUtil.saveFile("+AE=",FileUtil.encrypt(data),path);
                                                   }
                                                   Toast.makeText(MainActivity.this, "存档码导入成功！",Toast.LENGTH_LONG).show();
                                               }
                                           } catch (Exception e) {
                                               Toast.makeText(MainActivity.this, "存档导入失败！",Toast.LENGTH_LONG).show();
                                           }
                                       }
                                   }
        );
        Button button3 = findViewById(R.id.buttonCopy2);
        button3.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           TextView lblTitle = findViewById(R.id.editText2);
                                           try {
                                               String str = lblTitle.getText().toString();
                                               if(str == null || str.equals("") || str.trim().equals("")){
                                                   Toast.makeText(MainActivity.this, "内容不能为空！",Toast.LENGTH_LONG).show();
                                               }else{
                                                   str = FileUtil.decrypt(lblTitle.getText().toString());
                                                   lblTitle.setText(str);
                                               }
                                           } catch (Exception e) {
                                               Toast.makeText(MainActivity.this, "存档码解密失败！",Toast.LENGTH_LONG).show();
                                           }
                                       }
                                   }
        );
        Button button4 = findViewById(R.id.buttonImport2);
        button4.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           TextView lblTitle = findViewById(R.id.editText2);
                                           try {
                                               String str = lblTitle.getText().toString();
                                               if(str == null || str.equals("") || str.trim().equals("")){
                                                   Toast.makeText(MainActivity.this, "内容不能为空！",Toast.LENGTH_LONG).show();
                                               }else{
                                                   str = FileUtil.encrypt(lblTitle.getText().toString());
                                                   lblTitle.setText(str);
                                               }
                                           } catch (Exception e) {
                                               Toast.makeText(MainActivity.this, "存档码加密失败！",Toast.LENGTH_LONG).show();
                                           }
                                       }
                                   }
        );
        Button button5 = findViewById(R.id.button);
        button5.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           Intent intent = new Intent();
                                           intent.setAction("android.intent.action.VIEW");
                                           Uri content_url = Uri.parse("https://jq.qq.com/?_wv=1027&k=zQAHDnZO");
                                           intent.setData(content_url);
                                           startActivity(intent);
                                       }
                                   }
        );
    }
    public Boolean checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
        }
        return isGranted;
    }
}