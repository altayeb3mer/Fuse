package io.stormbird.wallet.ui.widget.holder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import io.stormbird.wallet.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TotalBalanceHolder extends BinderViewHolder<BigDecimal> {

    public static final int VIEW_TYPE = 1006;

    private final TextView title;

    public TotalBalanceHolder(int resId, ViewGroup parent) {
        super(resId, parent);
        title = findViewById(R.id.title);
    }

    @Override
    public void bind(@Nullable BigDecimal data, @NonNull Bundle addition) {
        title.setText(data == null
            ? "--"
            : "$" + data.setScale(2, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString());
    }
}
