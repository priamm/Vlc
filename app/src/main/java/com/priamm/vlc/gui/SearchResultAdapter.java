package com.priamm.vlc.gui;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.priamm.medialibrary.media.MediaLibraryItem;
import com.priamm.vlc.databinding.SearchItemBinding;
import com.priamm.vlc.gui.helpers.SelectorViewHolder;
import com.priamm.vlc.gui.helpers.UiTools;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    MediaLibraryItem[] mDataList;
    SearchActivity.ClickHandler mClickHandler;
    private final LayoutInflater mLayoutInflater;

    SearchResultAdapter(LayoutInflater inflater) {
        super();
        mLayoutInflater = inflater;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(SearchItemBinding.inflate(mLayoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (TextUtils.isEmpty(mDataList[position].getArtworkMrl()))
            holder.binding.setCover(UiTools.getDefaultCover(mDataList[position]));
        holder.binding.setItem(mDataList[position]);
    }

    public void add(MediaLibraryItem[] newList) {
        mDataList = newList;
        notifyDataSetChanged();
    }

    void setClickHandler(SearchActivity.ClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.length;
    }

    public class ViewHolder extends SelectorViewHolder<SearchItemBinding> {

        public ViewHolder(SearchItemBinding binding) {
            super(binding);
            binding.setHolder(this);
            binding.setHandler(mClickHandler);
        }
    }
}
