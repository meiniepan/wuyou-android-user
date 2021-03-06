package com.wuyou.user;

import android.text.TextUtils;
import android.util.Log;

import com.gs.buluo.common.utils.SharePreferenceManager;
import com.wuyou.user.data.local.db.CarefreeOpenHelper;
import com.wuyou.user.data.local.db.DaoMaster;
import com.wuyou.user.data.local.db.DaoSession;
import com.wuyou.user.data.local.db.EosAccount;
import com.wuyou.user.data.local.db.EosAccountDao;
import com.wuyou.user.data.local.db.SearchHistoryBean;
import com.wuyou.user.data.local.db.SearchHistoryBeanDao;
import com.wuyou.user.data.local.db.TraceIPFSBean;
import com.wuyou.user.data.local.db.TraceIPFSBeanDao;
import com.wuyou.user.data.local.db.UserInfo;
import com.wuyou.user.data.local.db.UserInfoDao;
import com.wuyou.user.data.remote.AddressBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by hjn on 2018/3/8.
 */

public class CarefreeDaoSession {
    private static DaoSession daoSession;
    private static CarefreeDaoSession instance;
    public static String tempAvatar;
    private String currentFormName;

    private CarefreeDaoSession() {
        String formName = SharePreferenceManager.getInstance(CarefreeApplication.getInstance().getApplicationContext()).getStringValue(Constant.FORM_NAME);
        DaoMaster.OpenHelper devOpenHelper = new CarefreeOpenHelper(CarefreeApplication.getInstance().getApplicationContext(),
                TextUtils.isEmpty(formName) ? Constant.DEFAULT_DB_NAME : formName, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        currentFormName = devOpenHelper.getDatabaseName();
    }

    private CarefreeDaoSession(String form) {
        DaoMaster.OpenHelper devOpenHelper = new CarefreeOpenHelper(CarefreeApplication.getInstance().getApplicationContext(), form, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        currentFormName = devOpenHelper.getDatabaseName();
    }

    public static void setCurrentForm(String currentForm) {
        currentForm = currentForm + ".db";
        instance = new CarefreeDaoSession(currentForm);
        SharePreferenceManager.getInstance(CarefreeApplication.getInstance().getApplicationContext()).setValue(Constant.FORM_NAME, currentForm);
    }

    public String getDatabaseFormName() {
        return currentFormName;
    }

    public static synchronized CarefreeDaoSession getInstance() {
        if (null == instance) {
            instance = new CarefreeDaoSession();
        }
        return instance;
    }

    public static String getAvatar(UserInfo info) {
        return tempAvatar == null ? info.getAvatar() : tempAvatar;
    }

    private UserInfoDao getUserInfoDao() {
        return daoSession.getUserInfoDao();
    }

    public EosAccountDao getEosDao() {
        return daoSession.getEosAccountDao();
    }

    public void setUserInfo(UserInfo userInfo) {
        getUserInfoDao().insert(userInfo);
    }

    public void updateUserInfo(UserInfo userInfo) {
        getUserInfoDao().update(userInfo);
    }

    public void clearUserInfo() {
        getUserInfoDao().deleteAll();
        uid = null;
    }

    public UserInfo getUserInfo() {
        List<UserInfo> userInfos = getUserInfoDao().loadAll();
        if (userInfos == null || userInfos.size() == 0) return null;
        return userInfos.get(0);
    }

    private String uid;

    public String getUserId() {
        if (TextUtils.isEmpty(uid)) {
            List<UserInfo> userInfos = getUserInfoDao().loadAll();
            if (userInfos == null || userInfos.size() == 0) return uid;
            uid = userInfos.get(0).getUid();
            return uid;
        } else {
            return uid;
        }
    }

    public void saveDefaultAddress(AddressBean addressBean) {
        UserInfo userInfo = getUserInfo();
        userInfo.setAddress(addressBean);
        getUserInfoDao().update(userInfo);
    }

    public AddressBean getDefaultAddress() {
        return getUserInfo().getAddress();
    }


    public void addHistoryRecord(SearchHistoryBean bean) {
        SearchHistoryBeanDao searchHistoryBeanDao = daoSession.getSearchHistoryBeanDao();
        if (searchHistoryBeanDao.queryBuilder().where(SearchHistoryBeanDao.Properties.Title.eq(bean.getTitle())).list().size() == 0)
            searchHistoryBeanDao.save(bean);
    }

    public List<SearchHistoryBean> getHistoryRecords() {
        return daoSession.getSearchHistoryBeanDao().queryBuilder().orderDesc(SearchHistoryBeanDao.Properties.Mid).build().list();
    }

    public void clearSearchHistory() {
        daoSession.getSearchHistoryBeanDao().deleteAll();
    }

    public void deleteHistory(SearchHistoryBean item) {
        daoSession.getSearchHistoryBeanDao().delete(item);
    }


    /*    --------------------------------------------------------------------------------------------    */
    //eos database operate
    public void deleteAll() {
        CarefreeDaoSession.getInstance().getEosDao().deleteAll();
    }

    public void delete(String accountName) {
        CarefreeDaoSession.getInstance().getEosDao().deleteByKey(accountName);
    }

    public List<EosAccount> getAllEosAccount() {
        return CarefreeDaoSession.getInstance().getEosDao().loadAll();
    }

    public EosAccount searchName(String nameStarts) {
        return CarefreeDaoSession.getInstance().getEosDao().queryBuilder().where(EosAccountDao.Properties.Name.like("%" + nameStarts + "%")).build().unique();
    }

    public EosAccount getMainAccount() {
        return CarefreeDaoSession.getInstance().getEosDao().queryBuilder().where(EosAccountDao.Properties.Main.like("TRUE")).build().unique();
    }

    public EosAccount setMainAccount(String account) throws IllegalStateException { //remember to try/catch
        EosAccount eosAccount = searchName(account);
        if (eosAccount == null) {
            Log.e("Carefree", "Account not found in database:" + account);
            return null;
        }
        if (eosAccount.getMain()) {
            return eosAccount;
        }
        EosAccount mainAccount = getMainAccount();
        mainAccount.setMain(false);
        getEosDao().update(mainAccount);

        eosAccount.setMain(true);
        getEosDao().update(eosAccount);
        return eosAccount;
    }

    public EosAccount setMainAccount(EosAccount account) {
        EosAccount mainAccount = getMainAccount();
        if (mainAccount != null) {
            mainAccount.setMain(false);
            getEosDao().update(mainAccount);
        }
        account.setMain(true);
        getEosDao().update(account);
        return account;
    }

//    ----------------------------------------------------------------------------------

    public void addTraceRecord(TraceIPFSBean bean) {
        daoSession.getTraceIPFSBeanDao().save(bean);
    }

    public List<TraceIPFSBean> getAllUnAuthTraceRecord() {
        QueryBuilder<TraceIPFSBean> traceIPFSBeanQueryBuilder = daoSession.getTraceIPFSBeanDao().queryBuilder();
        return traceIPFSBeanQueryBuilder.where(traceIPFSBeanQueryBuilder.and(TraceIPFSBeanDao.Properties.Status.eq(-1), TraceIPFSBeanDao.Properties.Account_name.like(getMainAccount().getName()))).list();
    }

    public TraceIPFSBean findTraceBean(TraceIPFSBean bean) {
        return daoSession.getTraceIPFSBeanDao().queryBuilder().where(TraceIPFSBeanDao.Properties.Timestamp.like(bean.getTimestamp())).unique();
    }

    public List<TraceIPFSBean> getAllTraceRecord(){
        QueryBuilder<TraceIPFSBean> traceIPFSBeanQueryBuilder = daoSession.getTraceIPFSBeanDao().queryBuilder();
        return traceIPFSBeanQueryBuilder.where(TraceIPFSBeanDao.Properties.Account_name.like(getMainAccount().getName())).list();
    }


    public List<TraceIPFSBean> getAllFinishedTraceRecord() {
        QueryBuilder<TraceIPFSBean> traceIPFSBeanQueryBuilder = daoSession.getTraceIPFSBeanDao().queryBuilder();
        return traceIPFSBeanQueryBuilder.where(traceIPFSBeanQueryBuilder.and(TraceIPFSBeanDao.Properties.Status.eq(1), TraceIPFSBeanDao.Properties.Account_name.like(getMainAccount().getName()))).list();
    }

    public void updateTraceBean(TraceIPFSBean traceBean) {
        daoSession.getTraceIPFSBeanDao().update(traceBean);
    }
}
