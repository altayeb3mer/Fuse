package io.stormbird.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import io.stormbird.token.entity.SigReturnType;
import io.stormbird.token.entity.XMLDsigDescriptor;
import io.stormbird.wallet.entity.*;
import io.stormbird.wallet.interact.FetchTokensInteract;
import io.stormbird.wallet.interact.FindDefaultNetworkInteract;
import io.stormbird.wallet.interact.GenericWalletInteract;
import io.stormbird.wallet.interact.SignatureGenerateInteract;
import io.stormbird.wallet.router.MyAddressRouter;
import io.stormbird.wallet.router.RedeemAssetSelectRouter;
import io.stormbird.wallet.router.SellTicketRouter;
import io.stormbird.wallet.router.TransferTicketRouter;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.token.entity.TicketRange;
import io.stormbird.wallet.service.AssetDefinitionService;
import io.stormbird.wallet.service.OpenseaService;
import io.stormbird.wallet.ui.RedeemAssetSelectActivity;
import io.stormbird.wallet.ui.SellDetailActivity;
import io.stormbird.wallet.ui.TransferTicketDetailActivity;
import io.stormbird.wallet.ui.widget.entity.TicketRangeParcel;

import static io.stormbird.wallet.C.*;
import static io.stormbird.wallet.C.Key.*;
import static io.stormbird.wallet.ui.TransferTicketDetailActivity.TRANSFER_TO_ADDRESS;

/**
 * Created by James on 22/01/2018.
 */

public class AssetDisplayViewModel extends BaseViewModel
{
    private static final long CHECK_BALANCE_INTERVAL = 10;
    private static final String TAG = "ADVM";
    private final FindDefaultNetworkInteract findDefaultNetworkInteract;
    private final FetchTokensInteract fetchTokensInteract;
    private final GenericWalletInteract genericWalletInteract;
    private final TransferTicketRouter transferTicketRouter;
    private final RedeemAssetSelectRouter redeemAssetSelectRouter;
    private final SellTicketRouter sellTicketRouter;
    private final MyAddressRouter myAddressRouter;
    private final AssetDefinitionService assetDefinitionService;
    private final OpenseaService openseaService;

    private Token refreshToken;

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<Token> ticket = new MutableLiveData<>();
    private final MutableLiveData<XMLDsigDescriptor> sig = new MutableLiveData<>();

    @Nullable
    private Disposable getBalanceDisposable;

    AssetDisplayViewModel(
            FetchTokensInteract fetchTokensInteract,
            GenericWalletInteract genericWalletInteract,
            SignatureGenerateInteract signatureGenerateInteract,
            TransferTicketRouter transferTicketRouter,
            RedeemAssetSelectRouter redeemAssetSelectRouter,
            FindDefaultNetworkInteract findDefaultNetworkInteract,
            SellTicketRouter sellTicketRouter,
            MyAddressRouter myAddressRouter,
            AssetDefinitionService assetDefinitionService,
            OpenseaService openseaService) {
        this.fetchTokensInteract = fetchTokensInteract;
        this.genericWalletInteract = genericWalletInteract;
        this.findDefaultNetworkInteract = findDefaultNetworkInteract;
        this.redeemAssetSelectRouter = redeemAssetSelectRouter;
        this.transferTicketRouter = transferTicketRouter;
        this.sellTicketRouter = sellTicketRouter;
        this.myAddressRouter = myAddressRouter;
        this.assetDefinitionService = assetDefinitionService;
        this.openseaService = openseaService;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (getBalanceDisposable != null) {
            getBalanceDisposable.dispose();
        }
    }

    public LiveData<Wallet> defaultWallet() {
        return defaultWallet;
    }
    public LiveData<Token> ticket() {
        return ticket;
    }
    public LiveData<XMLDsigDescriptor> sig() { return sig; }

    public void selectAssetIdsToRedeem(Context context, Token token) {
        if (getBalanceDisposable != null) {
            getBalanceDisposable.dispose();
        }
        redeemAssetSelectRouter.open(context, token);
    }

    public OpenseaService getOpenseaService()
    {
        return openseaService;
    }

    private void fetchCurrentTicketBalance() {
        if (getBalanceDisposable != null) getBalanceDisposable.dispose();
        if (ticket().getValue() != null && !ticket().getValue().independentUpdate())
        {
            getBalanceDisposable = Observable.interval(CHECK_BALANCE_INTERVAL, CHECK_BALANCE_INTERVAL, TimeUnit.SECONDS)
                    .doOnNext(l -> fetchTokensInteract
                            .fetchSingle(defaultWallet.getValue(), ticket().getValue())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::onToken, this::onError, this::finishTokenFetch)).subscribe();
        }
    }

    private void finishTokenFetch()
    {
        Log.d(TAG, "refreshToken: " + refreshToken.tokenInfo.name );
        ticket.postValue(refreshToken);
    }

    public void prepare(Token t) {
        ticket.setValue(t);
        disposable = findDefaultNetworkInteract
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);
    }

    public AssetDefinitionService getAssetDefinitionService()
    {
        return assetDefinitionService;
    }

    private void onToken(Token t)
    {
        refreshToken = t;
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = genericWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    public void showTransferToken(Context context, Token ticket) {
        if (getBalanceDisposable != null) {
            getBalanceDisposable.dispose();
        }
        transferTicketRouter.open(context, ticket);
    }

    public void showContractInfo(Context ctx, Token token)
    {
        myAddressRouter.open(ctx, defaultWallet.getValue(), token);
    }

    public void sellTicketRouter(Context ctx, Token token) {
        sellTicketRouter.open(ctx, token);
    }

    public void sellTicketRouter(Context context, Token token, String tokenIds) {
        Intent intent = new Intent(context, SellDetailActivity.class);
        intent.putExtra(WALLET, defaultWallet.getValue());
        intent.putExtra(TICKET, token);
        intent.putExtra(EXTRA_TOKENID_LIST, tokenIds);
        intent.putExtra(EXTRA_STATE, SellDetailActivity.SET_A_PRICE);
        intent.putExtra(EXTRA_PRICE, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    private void onDefaultWallet(Wallet wallet) {
        //TODO: switch on 'use' button
        progress.postValue(false);
        defaultWallet.setValue(wallet);
        fetchCurrentTicketBalance();
    }

    public void selectRedeemTokens(Context ctx, Token token, List<BigInteger> idList)
    {
        TicketRangeParcel parcel = new TicketRangeParcel(new TicketRange(idList, token.getAddress(), true));
        Intent intent = new Intent(ctx, RedeemAssetSelectActivity.class);
        intent.putExtra(TICKET, token);
        intent.putExtra(TICKET_RANGE, parcel);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ctx.startActivity(intent);
    }

    public void showTransferToken(Context ctx, Token token, List<BigInteger> selection)
    {
        Intent intent = new Intent(ctx, TransferTicketDetailActivity.class);
        intent.putExtra(WALLET, defaultWallet.getValue());
        intent.putExtra(TICKET, token);

        if (token instanceof ERC721Token)
        {
            intent.putExtra(EXTRA_TOKENID_LIST, selection.iterator().next().toString(10));
            intent.putExtra(EXTRA_STATE, TRANSFER_TO_ADDRESS);
        }
        else
        {
            intent.putExtra(EXTRA_TOKENID_LIST, token.intArrayToString(selection, false));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ctx.startActivity(intent);
    }

    private void openTransferDirectDialog(Intent intent, Token token, String tokenId)
    {

    }

    public void checkTokenScriptValidity(Token token)
    {
        disposable = assetDefinitionService.getSignatureData(token.tokenInfo.chainId, token.tokenInfo.address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sig::postValue, this::onSigCheckError);
    }

    private void onSigCheckError(Throwable throwable)
    {
        XMLDsigDescriptor failSig = new XMLDsigDescriptor();
        failSig.result = "fail";
        failSig.type = SigReturnType.NO_TOKENSCRIPT;
        failSig.subject = throwable.getMessage();
        sig.postValue(failSig);
    }
}
