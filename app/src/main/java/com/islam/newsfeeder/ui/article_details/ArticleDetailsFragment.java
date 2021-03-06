package com.islam.newsfeeder.ui.article_details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.islam.newsfeeder.MyApplication;
import com.islam.newsfeeder.R;
import com.islam.newsfeeder.databinding.FragmentArticleDetailsBinding;
import com.islam.newsfeeder.pojo.Article;
import com.islam.newsfeeder.ui.MainActivity;
import com.islam.newsfeeder.util.ActivityUtils;
import com.islam.newsfeeder.util.PreferenceUtils;
import com.islam.newsfeeder.util.other.ViewModelFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.islam.newsfeeder.util.Constants.BUNDLE_ARTICLE;
import static com.islam.newsfeeder.util.Constants.BUNDLE_OPEN_REAL_LATER_FRAGMENT;
import static com.islam.newsfeeder.util.Constants.KEY_ACCESS_TOKEN;

public class ArticleDetailsFragment extends Fragment {

    ArticlesDetailsViewModel mViewModel;
    private int mMutedColor = 0xFF333333;

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap != null) {
                Palette palette = Palette.from(bitmap).generate();
                mMutedColor = palette.getDarkMutedColor(0xFF333333);
                ((ImageView) getActivity().findViewById(R.id.article_details_image)).setImageBitmap(bitmap);
                ( (CollapsingToolbarLayout)getActivity().findViewById(R.id.collapsingToolbar)).setContentScrimColor(mMutedColor);
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(0);
                window.setStatusBarColor(mMutedColor);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            ((ImageView) getActivity().findViewById(R.id.article_details_image))
                    .setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public ArticleDetailsFragment() {
    }

    public static ArticleDetailsFragment getInstance(Article article) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_ARTICLE, article);

        ArticleDetailsFragment fragment = new ArticleDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_details, container, false);

        //init view model
        ViewModelFactory viewModelFactory = MyApplication.getInstance().getRepositoryComponent().getViewModelFactory();
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(ArticlesDetailsViewModel.class);
        mViewModel.init((Article) getArguments().getSerializable(BUNDLE_ARTICLE));

        //init data binding
        bindViews(view);

        enableBackButtonActionBar(view);

        setUpObservers();

        setHasOptionsMenu(true);

        return view;
    }

    public void bindViews(View view) {
        FragmentArticleDetailsBinding binding = DataBindingUtil.bind(view);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(mViewModel);
        binding.setFragment(this);
        binding.setPicassoTarget(target);
    }

    private void enableBackButtonActionBar(View view) {
        ArticleDetailsActivity activity = ((ArticleDetailsActivity) getActivity());
        activity.setSupportActionBar(view.findViewById(R.id.toolbar));
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void setUpObservers() {
        mViewModel.getShowToastNoConnection().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                ActivityUtils.showToast(getContext(), R.string.no_network_connection);
            }
        });
    }

    public void openArticleUrlInCustomTap() {
        ActivityUtils.openCustomTab(getContext(), mMutedColor,
                mViewModel.articleData.getValue().getArticleUrl());
    }

    public void onReadLaterClicked() {
        if (PreferenceUtils.getPocketData(getContext(), KEY_ACCESS_TOKEN) != null) {
            mViewModel.addArticleToReadLater();
            ActivityUtils.showToast(getContext(), R.string.added_to_real_later);
        } else {
            ActivityUtils.showToast(getContext(), R.string.you_must_sign_in_frist);
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra(BUNDLE_OPEN_REAL_LATER_FRAGMENT, true);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_read_later:
                onReadLaterClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
