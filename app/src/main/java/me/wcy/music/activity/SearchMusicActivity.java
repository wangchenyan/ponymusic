package me.wcy.music.activity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import butterknife.Bind;
import me.wcy.music.R;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    @Bind(R.id.lv_search_music_list)
    ListView lvSearchMusic;
    @Bind(R.id.ll_loading)
    LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    LinearLayout llLoadFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
    }

    @Override
    protected void setListener() {
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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
