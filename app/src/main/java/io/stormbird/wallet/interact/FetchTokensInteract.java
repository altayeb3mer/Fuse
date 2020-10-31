package io.stormbird.wallet.interact;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.stormbird.token.entity.FunctionDefinition;
import io.stormbird.token.entity.MagicLinkData;
import io.stormbird.token.tools.TokenDefinition;
import io.stormbird.wallet.entity.*;
import io.stormbird.wallet.repository.TokenRepositoryType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class FetchTokensInteract {

    private final TokenRepositoryType tokenRepository;

    public FetchTokensInteract(TokenRepositoryType tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Observable<TokenInfo> getTokenInfo(String address, int chainId) {
        return tokenRepository.update(address, chainId);
    }

    public Observable<Token[]> fetchStored(Wallet wallet) {
        return tokenRepository.fetchActiveStored(wallet.address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Token> fetchStoredToken(NetworkInfo network, Wallet wallet, String tokenAddress) {
        return tokenRepository.fetchCachedSingleToken(network, wallet.address, tokenAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Token[]> fetchStoredWithEth(Wallet wallet) {
        return tokenRepository.fetchActiveStoredPlusEth(wallet.address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Token> fetchSingle(Wallet wallet, Token token) {
        return tokenRepository.fetchActiveSingle(wallet.address, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Token> fetchEth(NetworkInfo network, Wallet wallet)
    {
        return tokenRepository.getEthBalance(network, wallet).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<BigInteger> getLatestBlock(int chainId)
    {
        return tokenRepository.fetchLatestBlockNumber(chainId);
    }

    public Observable<ContractResult> getContractResponse(String address, int chainId, String method)
    {
        return tokenRepository.getTokenResponse(address, chainId, method).toObservable();
    }

    public Observable<Token> updateDefaultBalance(Token token, Wallet wallet)
    {
        return tokenRepository.fetchActiveTokenBalance(wallet.address, token)
                .subscribeOn(Schedulers.io());
    }

    public Observable<OrderContractAddressPair> updateBalancePair(Token token, MagicLinkData order)
    {
        return tokenRepository.fetchActiveTokenBalance(order.ownerAddress, token)
                .map(updateToken -> mapToPair(updateToken, order))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Token> updateBalance(String address, Token token)
    {
        if (token == null) return Observable.fromCallable(() -> {
            return new Token(null, BigDecimal.ZERO, 0, "", ContractType.NOT_SET);
        });
        return tokenRepository.fetchActiveTokenBalance(address, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Ticker> getEthereumTicker(int chainId)
    {
        return tokenRepository.getEthTicker(chainId);
    }

    private OrderContractAddressPair mapToPair(Token token, MagicLinkData so)
    {
        OrderContractAddressPair pair = new OrderContractAddressPair();
        pair.order = so;
        pair.balance = token.getArrayBalance();
        return pair;
    }
}
