package com.islam.newsfeeder.ui.articles_list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.islam.newsfeeder.POJO.Article;
import com.islam.newsfeeder.POJO.Resource;
import com.islam.newsfeeder.R;
import com.islam.newsfeeder.base.BaseFragmentList;
import com.islam.newsfeeder.ui.article_details.ArticleDetailsActivity;
import com.islam.newsfeeder.ui.providers_filter.ProvidersFilterActivity;
import com.islam.newsfeeder.util.CallBacks;
import com.islam.newsfeeder.util.PreferenceUtils;
import com.islam.newsfeeder.util.other.ViewModelFactory;

import java.util.List;
import java.util.Map;

import static com.islam.newsfeeder.util.Constants.BUNDLE_ARTICLE;

public class ArticlesListFragment extends BaseFragmentList implements SwipeRefreshLayout.OnRefreshListener,
        CallBacks.AdapterCallBack<Article>, SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String TAG = "ArticlesListFragment";
    ArticlesViewModel mViewModel;
    ProvidersAdapter mAdapter;

    @Override
    public void onCreateView(View view, Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity(), ViewModelFactory.getInstance()).get(ArticlesViewModel.class);
        mAdapter = new ProvidersAdapter(this);
        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        setHasOptionsMenu(true);
    }

    @Override
    protected void setUpObservers() {
        mViewModel.getArticles().observe(getViewLifecycleOwner(), new Observer<Resource<Map<String, List<Article>>>>() {
            @Override
            public void onChanged(@Nullable Resource<Map<String, List<Article>>> listResource) {
                updateScreenStatus(getScreenStatus(listResource));
                if (listResource.getData() != null && listResource.getData().size() > 0) {
                    mAdapter.setData(listResource.getData());
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_article_list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                launchFilterActivity();
                return true;
        }
        return false;
    }

    private void launchFilterActivity() {
        startActivity(new Intent(getContext(), ProvidersFilterActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        //listen for any changes to the shared preference
        PreferenceUtils.getProviderSharedPreference(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        //remove the listener
        PreferenceUtils.getProviderSharedPreference(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        mViewModel.reload();
    }

    @Override
    public void onItemClicked(Article item) {
        Intent intent = new Intent(getContext(), ArticleDetailsActivity.class);

        intent.putExtra(BUNDLE_ARTICLE, item);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        mViewModel.reload();
    }
}
