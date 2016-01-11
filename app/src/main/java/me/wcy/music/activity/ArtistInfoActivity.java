package me.wcy.music.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import me.wcy.music.R;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.enums.LoadStateEnum;
import me.wcy.music.model.JArtistInfo;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.Extras;
import me.wcy.music.utils.Utils;

public class ArtistInfoActivity extends BaseActivity {
    @Bind(R.id.sv_artist_info)
    ScrollView svArtistInfo;
    @Bind(R.id.ll_artist_info_container)
    LinearLayout llArtistInfoContainer;
    @Bind(R.id.ll_loading)
    LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    LinearLayout llLoadFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        String tingUid = getIntent().getStringExtra(Extras.TING_UID);
        getArtistInfo(tingUid);
        Utils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOADING);
    }

    @Override
    protected void setListener() {
    }

    private void getArtistInfo(String tingUid) {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_ARTIST_INFO)
                .addParams(Constants.PARAM_TING_UID, tingUid)
                .build()
                .execute(new JsonCallback<JArtistInfo>(JArtistInfo.class) {
                    @Override
                    public void onResponse(JArtistInfo response) {
                        Utils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                        onSuccess(response);
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        Utils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    }
                });
    }

    private void onSuccess(JArtistInfo jArtistInfo) {
        int textColor = getResources().getColor(R.color.black);
        float textSize = 16.0f;

        String name = jArtistInfo.getName();
        String avatarUri = jArtistInfo.getAvatar_s500();
        String country = jArtistInfo.getCountry();
        String constellation = jArtistInfo.getConstellation();
        float stature = jArtistInfo.getStature();
        float weight = jArtistInfo.getWeight();
        String birth = jArtistInfo.getBirth();
        String intro = jArtistInfo.getIntro();
        String url = jArtistInfo.getUrl();
        if (!TextUtils.isEmpty(avatarUri)) {
            ImageView ivAvatar = new ImageView(this);
            ivAvatar.setImageResource(R.drawable.ic_default_artist);
            ivAvatar.setScaleType(ImageView.ScaleType.FIT_START);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.ic_default_artist)
                    .showImageForEmptyUri(R.drawable.ic_default_artist)
                    .showImageOnFail(R.drawable.ic_default_artist)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .build();
            ImageLoader.getInstance().displayImage(avatarUri, ivAvatar, options);
            llArtistInfoContainer.addView(ivAvatar, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
            TextView tvName = new TextView(this);
            tvName.setText(getString(R.string.artist_info_name, name));
            tvName.setTextColor(textColor);
            tvName.setTextSize(textSize);
            tvName.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvName, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(country)) {
            TextView tvCountry = new TextView(this);
            tvCountry.setText(getString(R.string.artist_info_country, country));
            tvCountry.setTextColor(textColor);
            tvCountry.setTextSize(textSize);
            tvCountry.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvCountry, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(constellation)) {
            TextView tvConstellation = new TextView(this);
            tvConstellation.setText(getString(R.string.artist_info_constellation, constellation));
            tvConstellation.setTextColor(textColor);
            tvConstellation.setTextSize(textSize);
            tvConstellation.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvConstellation, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (stature != 0f) {
            TextView tvStature = new TextView(this);
            tvStature.setText(getString(R.string.artist_info_stature, stature));
            tvStature.setTextColor(textColor);
            tvStature.setTextSize(textSize);
            tvStature.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvStature, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (weight != 0f) {
            TextView tvWeight = new TextView(this);
            tvWeight.setText(getString(R.string.artist_info_weight, weight));
            tvWeight.setTextColor(textColor);
            tvWeight.setTextSize(textSize);
            tvWeight.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvWeight, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(birth)) {
            TextView tvBirth = new TextView(this);
            tvBirth.setText(getString(R.string.artist_info_birth, birth));
            tvBirth.setTextColor(textColor);
            tvBirth.setTextSize(textSize);
            tvBirth.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvBirth, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(intro)) {
            TextView tvIntro = new TextView(this);
            tvIntro.setText(getString(R.string.artist_info_intro, intro));
            tvIntro.setTextColor(textColor);
            tvIntro.setTextSize(textSize);
            tvIntro.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            llArtistInfoContainer.addView(tvIntro, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(url)) {
            TextView tvUrl = new TextView(this);
            tvUrl.setText(Html.fromHtml(getString(R.string.artist_info_url, url)));
            tvUrl.setMovementMethod(LinkMovementMethod.getInstance());
            tvUrl.setTextSize(textSize);
            tvUrl.setPadding(0, Utils.dp2px(this, 10), 0, 0);
            tvUrl.setGravity(Gravity.CENTER);
            llArtistInfoContainer.addView(tvUrl, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }
}
