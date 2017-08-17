package kcrason.com.permissiongranted;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.kcrason.permissionlibrary.PermissionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityForResult(new Intent(this, PermissionActivity.class).putExtra(PermissionActivity.KEY_PERMISSIONS_ARRAY,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}), PermissionActivity.CALL_BACK_PERMISSION_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionActivity.CALL_BACK_PERMISSION_REQUEST_CODE){
            switch (resultCode){
                case PermissionActivity.CALL_BACK_RESULT_CODE_SUCCESS:
                    Toast.makeText(this,"权限申请成功！",Toast.LENGTH_SHORT).show();
                    break;
                case PermissionActivity.CALL_BACK_RESULE_CODE_FAILURE:
                    Toast.makeText(this,"权限申请失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
