package com.czy.easysocial.test;


import com.czy.baseUtilsLib.debug.DebugMyUtil;

/**
 * @author 13225
 */
public class TestConfig {

    public static final boolean IS_FORCE_TEST = false;

    public static final boolean IS_TEST = IS_FORCE_TEST ||
            DebugMyUtil.projectEnvironment == DebugMyUtil.Environment.LOCAL ||
            DebugMyUtil.projectEnvironment == DebugMyUtil.Environment.TEST ||
            DebugMyUtil.projectEnvironment == DebugMyUtil.Environment.STAGING;

}
