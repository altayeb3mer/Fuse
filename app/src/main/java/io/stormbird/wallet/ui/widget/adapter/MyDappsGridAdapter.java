package io.stormbird.wallet.ui.widget.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.URISyntaxException;
import java.util.List;

import io.stormbird.wallet.R;
import io.stormbird.wallet.entity.DApp;
import io.stormbird.wallet.ui.widget.OnDappClickListener;
import io.stormbird.wallet.util.DappBrowserUtils;
import io.stormbird.wallet.util.Utils;

public class MyDappsGridAdapter extends RecyclerView.Adapter<MyDappsGridAdapter.ViewHolder> {
    private List<DApp> data;
    private OnDappClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon, remove;
        TextView name, url;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);
        }
    }

    public MyDappsGridAdapter(List<DApp> data, OnDappClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setDapps(List<DApp> dapps) {
        this.data = dapps;
    }

    @NonNull
    @Override
    public MyDappsGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_my_dapp_grid, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDappsGridAdapter.ViewHolder viewHolder, int i) {
        DApp dApp = data.get(i);
        String visibleUrl = null;
        try {
            visibleUrl = Utils.getDomainName(dApp.getUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        viewHolder.name.setText(dApp.getName());
        viewHolder.url.setText(visibleUrl);

        String favicon;
        if (visibleUrl != null) {
            favicon = DappBrowserUtils.getIconUrl(visibleUrl);
            Glide.with(viewHolder.icon.getContext())
                    .load(favicon)
                    .apply(new RequestOptions().circleCrop())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_logo))
                    .into(viewHolder.icon);

            viewHolder.icon.setOnClickListener(v -> {
                listener.onDappClick(dApp);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
