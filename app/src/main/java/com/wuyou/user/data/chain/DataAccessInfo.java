package com.wuyou.user.data.chain;

import com.google.gson.annotations.Expose;

import com.wuyou.user.data.types.TypeAccountName;
import com.wuyou.user.data.types.TypeScopeName;


/**
 * Created by swapnibble on 2018-03-20.
 */

public class DataAccessInfo {
    //public enum Type { read, write };

    @Expose
    private String type; // access type

    @Expose
    private TypeAccountName code;

    @Expose
    private TypeScopeName scope;

    @Expose
    private long   sequence; // uint64_t
}
