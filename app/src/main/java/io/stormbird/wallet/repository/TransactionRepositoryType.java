package io.stormbird.wallet.repository;

import io.stormbird.wallet.entity.*;

import java.math.BigInteger;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface TransactionRepositoryType {
	Observable<Transaction[]> fetchCachedTransactions(Wallet wallet, int maxTransactions);
	Observable<Transaction[]> fetchNetworkTransaction(NetworkInfo network, String tokenAddress, long lastBlock, String userAddress);
	Single<String> createTransaction(Wallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, byte[] data, int chainId);
	Single<String> createTransaction(Wallet from, BigInteger gasPrice, BigInteger gasLimit, String data, int chainId);
	Single<TransactionData> createTransactionWithSig(Wallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, byte[] data, int chainId);
	Single<TransactionData> createTransactionWithSig(Wallet from, BigInteger gasPrice, BigInteger gasLimit, String data, int chainId);
	Single<byte[]> getSignature(Wallet wallet, byte[] message, int chainId);
	Single<byte[]> getSignatureFast(Wallet wallet, String password, byte[] message, int chainId);
	Single<Transaction[]> storeTransactions(Wallet wallet, Transaction[] txList);
	Single<Transaction[]> fetchTransactionsFromStorage(Wallet wallet, Token token, int count);

    Single<ContractType> queryInterfaceSpec(String address, TokenInfo tokenInfo);

    Transaction fetchCachedTransaction(String walletAddr, String hash);
}
