package com.czy.smartmedicine.test;


import com.czy.baseUtilsLib.debug.DebugEnvironment;

/**
 * @author 13225
 */
public class TestConfig {

    public static final boolean IS_FORCE_TEST = false;

    public static final boolean IS_TEST = IS_FORCE_TEST ||
            DebugEnvironment.projectEnvironment == DebugEnvironment.Environment.LOCAL ||
            DebugEnvironment.projectEnvironment == DebugEnvironment.Environment.TEST ||
            DebugEnvironment.projectEnvironment == DebugEnvironment.Environment.STAGING;

}
