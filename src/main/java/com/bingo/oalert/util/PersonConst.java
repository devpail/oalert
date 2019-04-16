package com.bingo.oalert.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class PersonConst {

    public static List<String> PERSOPN_LIST = Lists.newArrayList();

    public static Map<String, String> PHONE_MAP = Maps.newHashMap();

    public static final String ZHANGZHB_EMPID = "716e7770-1470-4561-8e61-6cbff4e297d3";
    public static final String GUOXX_EMPID = "c97a9b96-db7f-43e5-a9c3-464972c65e83";
    public static final String WANGLX_EMPID = "c24a2636-9fbc-498c-8ee4-9eb00e9cbb44";
    public static final String GAOTY_EMPID = "2769fa48-87ba-41e1-83e9-c41350f62e6d";

    static{
        PERSOPN_LIST.add(ZHANGZHB_EMPID);//张兆冰
        PHONE_MAP.put(ZHANGZHB_EMPID, "15098929019");
        PERSOPN_LIST.add(GUOXX_EMPID);//郭鑫鑫
        PHONE_MAP.put(GUOXX_EMPID, "15589779987");
        PERSOPN_LIST.add(WANGLX_EMPID);//王雷鑫
        PHONE_MAP.put(WANGLX_EMPID, "18765891312");
        PERSOPN_LIST.add(GAOTY_EMPID);//高天宇
        PHONE_MAP.put(GAOTY_EMPID, "15953155131");
    }
}
