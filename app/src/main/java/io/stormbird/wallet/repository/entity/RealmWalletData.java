package io.stormbird.wallet.repository.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.stormbird.wallet.entity.WalletType;
import io.stormbird.wallet.service.KeyService;

/**
 * Created by James on 8/11/2018.
 * Stormbird in Singapore
 */
public class RealmWalletData extends RealmObject
{
    private static int DISMISS_WARNING_IN_SETTINGS_MASK = 0xFFFFFFFE;

    @PrimaryKey
    private String address;
    private String ENSName;
    private String balance;
    private String name;
    private long lastWarning;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getENSName()
    {
        return ENSName;
    }

    public void setENSName(String ENSName)
    {
        this.ENSName = ENSName;
    }

    public String getBalance()
    {
        return balance;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastWarning()
    {
        return lastWarning;
    }
    public void setLastWarning(long lastWarning)
    {
        this.lastWarning = lastWarning & DISMISS_WARNING_IN_SETTINGS_MASK;
    }
    public boolean getIsDismissedInSettings() { return (lastWarning & 0x1) == 1; }
    public void setIsDismissedInSettings(boolean isDismissed) { lastWarning = (lastWarning&DISMISS_WARNING_IN_SETTINGS_MASK) + (isDismissed ? 0x1 : 0x0); }
}
