package cn.mcfun.savedata;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private String userCreateServer = "/sdcard/Android/data/com.aniplex.fategrandorder";
    private String path = userCreateServer + "/files/data/54cc790bf952ea710ed7e8be08049531";
    private String del = userCreateServer + "/files/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查 Root 权限
        if (!isDeviceRooted()) {
            promptForRoot();
            // 选择是否继续应用逻辑，这里我们选择退出
            return;
        }

        // 检查存储权限
        while (!checkPermission()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_main);

        // 初始化 UI 元素
        final TextView lblTitle = findViewById(R.id.editText1);
        final Button changeServerButton = findViewById(R.id.buttonChangeServer);
        final TextView directoryTextView = findViewById(R.id.textView3);
        final Button button1 = findViewById(R.id.buttonCopy1);
        final Button button2 = findViewById(R.id.buttonImport1);
        final Button button3 = findViewById(R.id.buttonCopy2);
        final Button button4 = findViewById(R.id.buttonImport2);

        // 加载数据并更新 UI
        loadDataAndUpdateUI(lblTitle);

        // 切换服务器按钮点击事件
        changeServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 切换 userCreateServer 的值
                if (userCreateServer.equals("/sdcard/Android/data/com.aniplex.fategrandorder")) {
                    userCreateServer = "/sdcard/Android/data/com.aniplex.fategrandorder.en";
                } else {
                    userCreateServer = "/sdcard/Android/data/com.aniplex.fategrandorder";
                }

                // 更新相关路径
                path = userCreateServer + "/files/data/54cc790bf952ea710ed7e8be08049531";
                del = userCreateServer + "/files/data";

                // 重新加载数据并更新 UI
                loadDataAndUpdateUI(lblTitle);

                // 更新 TextView 的内容
                if (userCreateServer.endsWith(".en")) {
                    directoryTextView.setText("美服目录");
                } else {
                    directoryTextView.setText("日服目录");
                }
            }
        });

        // 复制存档码按钮点击事件
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView lblTitle = findViewById(R.id.editText1);
                ClipboardManager cbm = (ClipboardManager) MainActivity.this
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cbm.setText(lblTitle.getText());
                Toast.makeText(MainActivity.this, "存档码复制成功！", Toast.LENGTH_SHORT).show();
            }
        });

        // 导入存档码按钮点击事件
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    TextView lblTitle = findViewById(R.id.editText1);
                    String data = FileUtil.decrypt(lblTitle.getText().toString());
                    if (data == null || data.trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "存档码格式错误！", Toast.LENGTH_SHORT).show();
                    } else {
                        deleteFileWithRoot(del);
                        JSONObject json = new JSONObject(data);
                        if (json.getString("userId").length() <= 7) {
                            FileUtil.saveFile("7AE=", FileUtil.encrypt(data), path);
                        } else {
                            FileUtil.saveFile("+AE=", FileUtil.encrypt(data), path);
                        }
                        Toast.makeText(MainActivity.this, "存档码导入成功！", Toast.LENGTH_SHORT).show();
                        loadDataAndUpdateUI(lblTitle);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "存档导入失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 解密存档码按钮点击事件
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    TextView lblTitle = findViewById(R.id.editText2);
                    String str = FileUtil.decrypt(lblTitle.getText().toString());
                    if (str == null || str.trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        lblTitle.setText(str);
                        Toast.makeText(MainActivity.this, "存档码解密成功！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "存档码解密失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 加密存档码按钮点击事件
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    TextView lblTitle = findViewById(R.id.editText2);
                    String str = lblTitle.getText().toString();
                    if (str == null || str.trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "内容不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        str = FileUtil.encrypt(str);
                        lblTitle.setText(str);
                        Toast.makeText(MainActivity.this, "存档码加密成功！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "存档码加密失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 检查设备是否已获取 Root 权限
     */
    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };
        for (String path : paths) {
            if (fileExistsWithRoot(path)) return true;
        }
        return false;
    }

    private boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    /**
     * 提示用户获取 Root 权限
     */
    private void promptForRoot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Root 权限未启用");
        builder.setMessage("此应用需要 root 权限才能正常工作。请确保您的设备已 root 并安装了 Magisk。");
        builder.setPositiveButton("安装 Magisk", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 引导用户到 Magisk 官方网站或应用商店
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://magisk.me/"));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 检查存储权限
     */
    public Boolean checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        102
                );
            }
        }
        return isGranted;
    }

    /**
     * 加载数据并更新 UI
     */
    private void loadDataAndUpdateUI(final TextView textView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String data = getFileWithRoot(path).substring(getFileWithRoot(path).indexOf("ZSv"));
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(data);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("权限不足或未找到存档码文件！");
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 检查文件是否存在（使用 Root 权限）
     */
    private boolean fileExistsWithRoot(String path) {
        String cmd = "ls " + path;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = in.readLine();
            process.waitFor();
            if (line != null && !line.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            if (process != null) process.destroy();
        }
        return false;
    }

    /**
     * 读取文件内容（使用 Root 权限）
     */
    private String getFileWithRoot(String path) throws Exception {
        String cmd = "cat " + path;
        Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        process.waitFor();
        return sb.toString();
    }

    /**
     * 删除文件或目录（使用 Root 权限）
     */
    private void deleteFileWithRoot(String path) throws Exception {
        // 使用 find 命令查找所有文件并删除，而不删除子目录
        String cmd = "find " + path + " -type f -exec rm -f {} +";
        Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
        process.waitFor();
    }

}
