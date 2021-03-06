package io.stormbird.wallet.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import io.stormbird.token.tools.Numeric;
import io.stormbird.wallet.R;
import io.stormbird.wallet.ui.widget.OnSetWatchWalletListener;
import io.stormbird.wallet.util.Utils;
import io.stormbird.wallet.widget.PasswordInputView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by James on 26/07/2019.
 * Stormbird in Sydney
 */
public class SetWatchWalletFragment extends Fragment implements View.OnClickListener, TextWatcher
{
    private static final OnSetWatchWalletListener dummyWatchWalletListener = key -> {
    };
    private static final String validator = "[^x^a-f^A-F^0-9]";

    private PasswordInputView watchAddress;
    private Button importButton;
    private OnSetWatchWalletListener onSetWatchWalletListener = dummyWatchWalletListener;
    private Pattern pattern;

    public static SetWatchWalletFragment create()
    {
        return new SetWatchWalletFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_watch_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        watchAddress = view.findViewById(R.id.input_watch_address);
        importButton = view.findViewById(R.id.import_action);
        importButton.setOnClickListener(this);
        watchAddress.getEditText().addTextChangedListener(this);
        updateButtonState(false);
        pattern = Pattern.compile(validator, Pattern.MULTILINE);
    }

    @Override
    public void onClick(View view)
    {
        watchAddress.setError(null);
        String value = watchAddress.getText().toString();

        if (!TextUtils.isEmpty(value))
        {
            if (Utils.isAddressValid(value))
            {
                onSetWatchWalletListener.onWatchWallet(value);
                return;
            }
            else
            {
                watchAddress.setError(getString(R.string.ethereum_address_hint));
                return;
            }
        }

        watchAddress.setError(getString(R.string.error_field_required));
    }

    private void updateButtonState(boolean enabled)
    {
        importButton.setActivated(enabled);
        importButton.setClickable(enabled);
        int colorId = enabled ? R.color.nasty_green : R.color.inactive_green;
        if (getContext() != null)
            importButton.setBackgroundColor(getContext().getColor(colorId));
    }

    public void setOnSetWatchWalletListener(OnSetWatchWalletListener onSetWatchWalletListener)
    {
        this.onSetWatchWalletListener = onSetWatchWalletListener == null
                ? dummyWatchWalletListener
                : onSetWatchWalletListener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        if (watchAddress.isErrorState())
            watchAddress.setError(null);
        String value = watchAddress.getText().toString();
        final Matcher matcher = pattern.matcher(value);
        if (matcher.find())
        {
            updateButtonState(false);
            watchAddress.setError(getString(R.string.ethereum_address_hint));
        }
        else if (Utils.isAddressValid(value))
        {
            updateButtonState(true);
        }
        else
        {
            updateButtonState(false);
        }
    }

    public void setAddress(String address)
    {
        watchAddress.getEditText().setText(address);
    }
}
