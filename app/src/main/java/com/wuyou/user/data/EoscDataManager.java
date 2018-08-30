/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.wuyou.user.data;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.gs.buluo.common.network.ApiException;
import com.gs.buluo.common.utils.TribeDateUtils;
import com.wuyou.user.CarefreeDaoSession;
import com.wuyou.user.Constant;
import com.wuyou.user.crypto.ec.EosPrivateKey;
import com.wuyou.user.crypto.ec.EosPublicKey;
import com.wuyou.user.data.local.db.EosAccount;
import com.wuyou.user.data.remote.NodeosApi;
import com.wuyou.user.data.remote.model.abi.EosAbiMain;
import com.wuyou.user.data.remote.model.api.AccountInfoRequest;
import com.wuyou.user.data.remote.model.api.EosChainInfo;
import com.wuyou.user.data.remote.model.api.GetBalanceRequest;
import com.wuyou.user.data.remote.model.api.GetCodeRequest;
import com.wuyou.user.data.remote.model.api.GetCodeResponse;
import com.wuyou.user.data.remote.model.api.GetRequestForCurrency;
import com.wuyou.user.data.remote.model.api.GetRequiredKeys;
import com.wuyou.user.data.remote.model.api.GetTableRequest;
import com.wuyou.user.data.remote.model.api.JsonToBinRequest;
import com.wuyou.user.data.remote.model.chain.Action;
import com.wuyou.user.data.remote.model.chain.PackedTransaction;
import com.wuyou.user.data.remote.model.chain.SignedTransaction;
import com.wuyou.user.data.remote.model.types.EosDailyRewards;
import com.wuyou.user.data.remote.model.types.EosNewAccount;
import com.wuyou.user.data.remote.model.types.EosTransfer;
import com.wuyou.user.data.remote.model.types.TypeAsset;
import com.wuyou.user.data.remote.model.types.TypeChainId;
import com.wuyou.user.data.util.Utils;
import com.wuyou.user.network.ChainRetrofit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;


/**
 * Created by swapnibble on 2017-11-03.
 */
public class EoscDataManager {
    private static EoscDataManager eoscDataManager;

    public synchronized static EoscDataManager getIns() {
        if (eoscDataManager == null) {
            eoscDataManager = new EoscDataManager();
        }
        return eoscDataManager;
    }

    public Observable<JsonObject> createAccount(String phone, String account, String publicKey) {
        EosNewAccount eosAccount = new EosNewAccount(phone, account, publicKey, new TypeAsset(2));
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).jsonToBin(new JsonToBinRequest(Constant.EOSIO_SYSTEM_ACCOUNT, eosAccount.getActionName(), Utils.prettyPrintJson(eosAccount)))
                .flatMap(jsonToBinResp -> getChainInfo()
                        .map(info -> createTransaction(Constant.EOSIO_SYSTEM_ACCOUNT, eosAccount.getActionName(), jsonToBinResp.getBinargs(), getActivePermission("justforapply"), info)))
                .flatMap(signedTransaction -> {
                    final SignedTransaction stxn = new SignedTransaction(signedTransaction);
                    stxn.sign(new EosPrivateKey(chainPrivateKey), new TypeChainId(currentBlockInfo.getChain_id()));
                    return ChainRetrofit.getInstance().createApi(NodeosApi.class).pushTransactionRetJson(new PackedTransaction(stxn));
                });
    }

    private EosAccount currentOperateAccount;

    public Observable<JsonObject> getDailyRewords() {
        currentOperateAccount = CarefreeDaoSession.getInstance().getMainAccount();
        EosDailyRewards dailyRewards = new EosDailyRewards(currentOperateAccount.getName(), TribeDateUtils.dateFormat5(new Date(System.currentTimeMillis())), "hjn", new TypeAsset(1));
        return pushActionRetJson(Constant.EOSIO_DAILAY_REWARDS, dailyRewards.getActionName(), Utils.prettyPrintJson(dailyRewards), getActivePermission(currentOperateAccount.getName())); //transfer.getAsHex()
    }


    public Observable<JsonObject> transfer(String from, String to, long amount, String memo) {
        EosAccount account = CarefreeDaoSession.getInstance().searchName(from);
        if (account == null) {
            return Observable.error(new ApiException(600, "账户名称错误 或者 钱包中无该账户", "ApiException"));
        } else {
            currentOperateAccount = account;
        }
        EosTransfer transfer = new EosTransfer(from, to, amount, memo);
        return pushActionRetJson(Constant.EOSIO_TOKEN_CONTRACT, transfer.getActionName(), Utils.prettyPrintJson(transfer), getActivePermission(from)); //transfer.getAsHex()
    }


    public Observable<EosChainInfo> getChainInfo() {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).readInfo("get_info");
    }

    public Observable<String> getTable(String accountName, String code, String table,
                                       String tableKey, String lowerBound, String upperBound, int limit) {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getTable(
                new GetTableRequest(accountName, code, table, tableKey, lowerBound, upperBound, limit))
                .map(tableResult -> Utils.prettyPrintJson(tableResult));
    }

    public Observable<EosPrivateKey[]> createKey(int count) {
        return Observable.fromCallable(() -> {
            EosPrivateKey[] retKeys = new EosPrivateKey[count];
            for (int i = 0; i < count; i++) {
                retKeys[i] = new EosPrivateKey();
            }

            return retKeys;
        });
    }

    private SignedTransaction createTransaction(String contract, String actionName, String dataAsHex, String[] permissions, EosChainInfo chainInfo) {
        currentBlockInfo = chainInfo;
        Action action = new Action(contract, actionName);
        action.setAuthorization(permissions);
        action.setData(dataAsHex);

        SignedTransaction txn = new SignedTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());
        if (null != chainInfo) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(Constant.TX_EXPIRATION_IN_MILSEC));
        }
        return txn;
    }

    private Observable<PackedTransaction> signAndPackTransaction(SignedTransaction txnBeforeSign) {
        ArrayList<String> pkList = new ArrayList<>();
        pkList.add(currentOperateAccount.getPublicKey());
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getRequiredKeys(new GetRequiredKeys(txnBeforeSign, pkList))
                .map(keys -> {
                    final SignedTransaction stxn;
                    stxn = signTransaction(txnBeforeSign, keys.getKeys(), new TypeChainId(currentBlockInfo.getChain_id()));
                    return new PackedTransaction(stxn);
                });
    }

    public SignedTransaction signTransaction(final SignedTransaction txn, final List<EosPublicKey> keys, final TypeChainId id) throws IllegalStateException {
        SignedTransaction stxn = new SignedTransaction(txn);
        boolean found = false;
        for (EosPublicKey pubKey : keys) {
            if (TextUtils.equals(pubKey.toString(), currentOperateAccount.getPublicKey())) {
                stxn.sign(new EosPrivateKey(currentOperateAccount.getPrivateKey()), id);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalStateException("Public key not found in unlocked wallets " + currentOperateAccount.getPublicKey());
        }
        return stxn;
    }

    private String[] getActivePermission(String accountName) {
        return new String[]{accountName + "@active"};
    }

    public Observable<JsonObject> readAccountInfo(String accountName) {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getAccountInfo(new AccountInfoRequest(accountName));
    }

    public Observable<JsonObject> getTransactions(String accountName) {
        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty(NodeosApi.GET_TRANSACTIONS_KEY, accountName);
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getAccountHistory(NodeosApi.ACCOUNT_HISTORY_GET_TRANSACTIONS, gsonObject);
    }

    public Observable<JsonObject> getServants(String accountName) {
        JsonObject gsonObject = new JsonObject();
        gsonObject.addProperty(NodeosApi.GET_SERVANTS_KEY, accountName);
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getAccountHistory(NodeosApi.ACCOUNT_HISTORY_GET_SERVANTS, gsonObject);
    }

    private EosChainInfo currentBlockInfo;

    void setInfo(EosChainInfo info) {
        currentBlockInfo = info;
    }

    private final String chainPublicKey = "EOS5kkMsEbNCR2VA2cvr4szm9RJxRD996bAGs5LbHQYp1uoo2mAzx";
    private final String chainPrivateKey = "5JcN6R1fTwSUvL3jHpnpG6KsJB3nEPBSkL63dbht4ySPrXTgXyL";

    public Observable<JsonObject> pushActionRetJson(String contract, String action, String data, String[] permissions) {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).jsonToBin(new JsonToBinRequest(contract, action, data))
                .flatMap(jsonToBinResp -> getChainInfo()
                        .map(info -> createTransaction(contract, action, jsonToBinResp.getBinargs(), permissions, info)))
                .flatMap(this::signAndPackTransaction)
                .flatMap(ChainRetrofit.getInstance().createApi(NodeosApi.class)::pushTransactionRetJson);
    }

//    public Observable<PushTxnResponse> pushAction(String contract, String action, String data, String[] permissions) {
//        return ChainRetrofit.getInstance().createApi(NodeosApi.class).jsonToBin(new JsonToBinRequest(contract, action, data))
//                .flatMap(jsonToBinResp -> getChainInfo()
//                        .map(info -> createTransaction(contract, action, jsonToBinResp.getBinargs(), permissions, info)))
//                .flatMap(this::signAndPackTransaction)
//                .flatMap(ChainRetrofit.getInstance().createApi(NodeosApi.class)::pushTransaction);
//    }

    public Observable<EosAbiMain> getCodeAbi(String contract) {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getCode(new GetCodeRequest(contract))
                .filter(GetCodeResponse::isValidCode)
                .map(result -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                        .create().fromJson(result.getAbi(), EosAbiMain.class));
    }

    public Observable<EosAbiMain> getAbiMainFromJson(String jsonStr) {
        return Observable.just(new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().fromJson(jsonStr, EosAbiMain.class));
    }

    public Observable<String> getCurrencyBalance(String contract, String account, String symbol) {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getCurrencyBalance(new GetBalanceRequest(contract, account, symbol))
                .map(result -> Utils.prettyPrintJson(result));
    }

    public Observable<String> getCurrencyStats(String contract, String symbol) {
        return ChainRetrofit.getInstance().createApi(NodeosApi.class).getCurrencyStats(new GetRequestForCurrency(contract, symbol))
                .map(result -> Utils.prettyPrintJson(result));
    }


}