/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: UpgradeParser.java
 * @Prject: BananaTvSetting
 * @Description: pull解析接口
 * @author: lijungang 
 * @date: 2014-1-24 下午1:52:45
 */
package com.sz.ead.framework.mul2launcher.settings.upgrade;

import java.io.InputStream;

public interface UpgradeParser {
    public void parse(InputStream is) throws Exception; 
    
    public String serialize() throws Exception; 
}