package me.wcy.music.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.music.R;

public class AboutActivity extends BaseActivity {
    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.tv_declaration)
    TextView tvDeclaration;
    @Bind(R.id.tv_baidu_declaration)
    TextView tvBaiduDeclaration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvVersion.setText(getVersion());
        tvDeclaration.setText(Html.fromHtml(getString(R.string.declaration, getString(R.string.app_name))));
        tvDeclaration.setMovementMethod(LinkMovementMethod.getInstance());
        tvBaiduDeclaration.setText(Html.fromHtml(getString(R.string.baidu_declaration)));
        tvBaiduDeclaration.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void setListener() {
    }

    private String getVersion() {
        String version = "1.0.0";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return getString(R.string.version, version);
    }
}
