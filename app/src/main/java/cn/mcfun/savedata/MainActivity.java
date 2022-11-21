package cn.mcfun.savedata;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    final String path = "/Android/data/com.aniplex.fategrandorder/files/data/54cc790bf952ea710ed7e8be08049531";
    final String del = "/Android/data/com.aniplex.fategrandorder/files/data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            lblTitle.setText("未找到存档文件！");
        }
        Button button1 = findViewById(R.id.buttonCopy1);
        button1.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           TextView lblTitle = findViewById(R.id.editText1);
                                           ClipboardManager cbm= (ClipboardManager) MainActivity.this
                                                   .getSystemService(Context.CLIPBOARD_SERVICE);
                                           cbm.setText(lblTitle.getText());
                                           Toast.makeText(MainActivity.this, "存档复制成功！",Toast.LENGTH_LONG).show();
                                       }
                                   }
        );
        Button button2 = findViewById(R.id.buttonImport1);
        button2.setOnClickListener(new View.OnClickListener(){
                                       public void onClick(View v) {
                                           TextView lblTitle = findViewById(R.id.editText1);
                                           try {
                                               FileUtil.deletefile(del);
                                               String data = FileUtil.decrypt(lblTitle.getText().toString());
                                               FileUtil.saveFile(FileUtil.encrypt(data),path);
                                               Toast.makeText(MainActivity.this, "存档导入成功！",Toast.LENGTH_LONG).show();
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
                                               Toast.makeText(MainActivity.this, "存档解密失败！",Toast.LENGTH_LONG).show();
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
                                               Toast.makeText(MainActivity.this, "存档加密失败！",Toast.LENGTH_LONG).show();
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
}