package io.stormbird.wallet.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import io.stormbird.wallet.R;
import io.stormbird.wallet.entity.DApp;
import io.stormbird.wallet.ui.widget.OnDappClickListener;
import io.stormbird.wallet.ui.widget.OnDappHomeNavClickListener;
import io.stormbird.wallet.ui.widget.adapter.MyDappsGridAdapter;
import io.stormbird.wallet.util.DappBrowserUtils;


public class DappHomeFragment extends Fragment {
    @NonNull
    private OnDappClickListener onDappClickListener;
    @NonNull
    private OnDappHomeNavClickListener onDappHomeNavClickListener;

    void setCallbacks(OnDappClickListener l1, OnDappHomeNavClickListener l2) {
        onDappClickListener = l1;
        onDappHomeNavClickListener = l2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dapp_home, container, false);
        LinearLayout myDappsLayout = view.findViewById(R.id.my_dapps);
        myDappsLayout.setOnClickListener(v -> onDappHomeNavClickListener.onDappHomeNavClick(0));

        LinearLayout discoverDappsLayout = view.findViewById(R.id.discover_dapps);
        discoverDappsLayout.setOnClickListener(v -> onDappHomeNavClickListener.onDappHomeNavClick(1));

        LinearLayout historyLayout = view.findViewById(R.id.history);
        historyLayout.setOnClickListener(v -> onDappHomeNavClickListener.onDappHomeNavClick(2));

        RecyclerView grid = view.findViewById(R.id.my_dapps_grid);
        MyDappsGridAdapter adapter = new MyDappsGridAdapter(getData(), onDappClickListener);
        grid.setNestedScrollingEnabled(false);
        grid.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        grid.setAdapter(adapter);
        return view;
    }

    private List<DApp> getData() {
        return DappBrowserUtils.getMyDapps(getContext());
    }
}
