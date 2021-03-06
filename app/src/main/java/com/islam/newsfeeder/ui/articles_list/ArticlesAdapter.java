package com.islam.newsfeeder.ui.articles_list;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.islam.newsfeeder.pojo.Article;
import com.islam.newsfeeder.pojo.network.NetworkState;
import com.islam.newsfeeder.R;
import com.islam.newsfeeder.databinding.ItemArticleBinding;
import com.islam.newsfeeder.util.CallBacks;
import com.islam.newsfeeder.util.other.RoundedCornersTransformation;

public class ArticlesAdapter extends PagedListAdapter<Article, ArticlesAdapter.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    //this used to set radius to the image
    private final RoundedCornersTransformation cornersTransformation =
            new RoundedCornersTransformation(16, 0);
    private NetworkState networkState;


    protected ArticlesAdapter() {
        super(Article.diffUtil);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_PROGRESS:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_progress, parent, false);
                return new ViewHolder(view);
            default:
                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_article, parent, false);
                return new ViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        if (getItemViewType(i) == TYPE_ITEM) {
            Article article = getItem(i);
            if (article != null) {
                holder.bind(article);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }


    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState.getStatus() == NetworkState.STATUS_LOADING;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemArticleBinding binding;

        ViewHolder(ViewDataBinding itemView) {
            super(itemView.getRoot());
            this.binding = (ItemArticleBinding) itemView;
        }

        ViewHolder(View view) {
            super(view);
        }

        void bind(Article item) {
            binding.setArticle(item);
            binding.setRoundedCorner(cornersTransformation);
            binding.executePendingBindings();
        }
    }
}
