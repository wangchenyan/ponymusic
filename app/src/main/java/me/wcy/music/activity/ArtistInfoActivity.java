package me.wcy.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.wcy.music.R;
import me.wcy.music.constants.Extras;
import me.wcy.music.enums.LoadStateEnum;
import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.ArtistInfo;
import me.wcy.music.utils.ViewUtils;
import me.wcy.music.utils.binding.Bind;

public class ArtistInfoActivity extends BaseActivity {
    @Bind(R.id.sv_artist_info)
    private ScrollView svArtistInfo;
    @Bind(R.id.ll_artist_info_container)
    private LinearLayout llArtistInfoContainer;
    @Bind(R.id.ll_loading)
    private LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    private LinearLayout llLoadFail;

    public static void start(Context context, String tingUid) {
        Intent intent = new Intent(context, ArtistInfoActivity.class);
        intent.putExtra(Extras.TING_UID, tingUid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        if (!checkServiceAlive()) {
            return;
        }

        String tingUid = getIntent().getStringExtra(Extras.TING_UID);
        getArtistInfo(tingUid);
        ViewUtils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOADING);
    }

    @Override
    protected void setListener() {
    }

    private void getArtistInfo(String tingUid) {
        HttpClient.getArtistInfo(tingUid, new HttpCallback<ArtistInfo>() {
            @Override
            public void onSuccess(ArtistInfo response) {
                if (response == null) {
                    onFail(null);
                    return;
                }
                ViewUtils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                setData(response);
            }

            @Override
            public void onFail(Exception e) {
                ViewUtils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            }
        });
    }

    private void setData(ArtistInfo artistInfo) {
        String name = artistInfo.getName();
        String avatarUri = artistInfo.getAvatar_s1000();
        String country = artistInfo.getCountry();
        String constellation = artistInfo.getConstellation();
        float stature = artistInfo.getStature();
        float weight = artistInfo.getWeight();
        String birth = artistInfo.getBirth();
        String intro = artistInfo.getIntro();
        String url = artistInfo.getUrl();
        if (!TextUtils.isEmpty(avatarUri)) {
            ImageView ivAvatar = new ImageView(this);
            ivAvatar.setScaleType(ImageView.ScaleType.FIT_START);
            Glide.with(this)
                    .load(avatarUri)
                    .placeholder(R.drawable.default_artist)
                    .error(R.drawable.default_artist)
                    .into(ivAvatar);
            llArtistInfoContainer.addView(ivAvatar);
        }
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
            TextView tvName = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvName.setText(getString(R.string.artist_info_name, name));
            llArtistInfoContainer.addView(tvName);
        }
        if (!TextUtils.isEmpty(country)) {
            TextView tvCountry = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvCountry.setText(getString(R.string.artist_info_country, country));
            llArtistInfoContainer.addView(tvCountry);
        }
        if (!TextUtils.isEmpty(constellation) && !TextUtils.equals(constellation, "未知")) {
            TextView tvConstellation = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvConstellation.setText(getString(R.string.artist_info_constellation, constellation));
            llArtistInfoContainer.addView(tvConstellation);
        }
        if (stature != 0f) {
            TextView tvStature = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvStature.setText(getString(R.string.artist_info_stature, String.valueOf(stature)));
            llArtistInfoContainer.addView(tvStature);
        }
        if (weight != 0f) {
            TextView tvWeight = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvWeight.setText(getString(R.string.artist_info_weight, String.valueOf(weight)));
            llArtistInfoContainer.addView(tvWeight);
        }
        if (!TextUtils.isEmpty(birth) && !TextUtils.equals(birth, "0000-00-00")) {
            TextView tvBirth = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvBirth.setText(getString(R.string.artist_info_birth, birth));
            llArtistInfoContainer.addView(tvBirth);
        }
        if (!TextUtils.isEmpty(intro)) {
            TextView tvIntro = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvIntro.setText(getString(R.string.artist_info_intro, intro));
            llArtistInfoContainer.addView(tvIntro);
        }
        if (!TextUtils.isEmpty(url)) {
            TextView tvUrl = (TextView) LayoutInflater.from(this).inflate(R.layout.item_artist_info, llArtistInfoContainer, false);
            tvUrl.setLinkTextColor(ContextCompat.getColor(this, R.color.blue));
            tvUrl.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString spannableString = new SpannableString("查看更多信息");
            spannableString.setSpan(new URLSpan(url), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvUrl.setText(spannableString);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            tvUrl.setLayoutParams(layoutParams);
            llArtistInfoContainer.addView(tvUrl);
        }

        if (llArtistInfoContainer.getChildCount() == 0) {
            ViewUtils.changeViewState(svArtistInfo, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            ((TextView) llLoadFail.findViewById(R.id.tv_load_fail_text)).setText(R.string.artist_info_empty);
        }
    }
}
