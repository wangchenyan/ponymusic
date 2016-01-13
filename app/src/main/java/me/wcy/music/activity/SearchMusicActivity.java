package me.wcy.music.activity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import me.wcy.music.R;
import me.wcy.music.adapter.OnMoreClickListener;
import me.wcy.music.adapter.SearchMusicAdapter;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.enums.LoadStateEnum;
import me.wcy.music.model.JSearchMusic;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.Utils;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener, OnMoreClickListener {
    @Bind(R.id.lv_search_music_list)
    ListView lvSearchMusic;
    @Bind(R.id.ll_loading)
    LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    LinearLayout llLoadFail;
    private SearchMusicAdapter mAdapter;
    private List<JSearchMusic.JSong> mSearchMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mSearchMusicList = new ArrayList<>();
        mAdapter = new SearchMusicAdapter(this, mSearchMusicList);
        lvSearchMusic.setAdapter(mAdapter);
        ((TextView) llLoadFail.findViewById(R.id.tv_load_fail_text)).setText(R.string.search_empty);
    }

    @Override
    protected void setListener() {
        mAdapter.setOnMoreClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_music, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.onActionViewExpanded();
        searchView.setQueryHint(getString(R.string.search_tips));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        ImageView mGoButton = (ImageView) searchView.findViewById(R.id.search_go_btn);
        mGoButton.setImageResource(R.drawable.ic_menu_search_white);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Utils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        searchMusic(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchMusic(String keyword) {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_SEARCH_MUSIC)
                .addParams(Constants.PARAM_QUERY, keyword)
                .build()
                .execute(new JsonCallback<JSearchMusic>(JSearchMusic.class) {
                    @Override
                    public void onResponse(JSearchMusic response) {
                        if (response.getSong() == null) {
                            Utils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                            return;
                        }
                        Utils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                        Collections.addAll(mSearchMusicList, response.getSong());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        Utils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    }
                });
    }

    @Override
    public void onMoreClick(int position) {

    }
}
