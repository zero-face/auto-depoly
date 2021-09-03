package com.zero.publish.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author Zero
 * @Date 2021/9/1 13:45
 * @Since 1.8
 * @Description
 **/

public class PropertyUtils {
    /**
     * 主配置文件
     */
    private Properties properties;
    /**
     * 启用配置文件
     */
    private Properties propertiesCustom;

    private static PropertyUtils propertiesUtils = new PropertyUtils();

    /**
     * 私有构造，禁止直接创建
     */
    private PropertyUtils() {
        // 读取配置启用的配置文件名
        properties = new Properties();
        propertiesCustom = new Properties();
        InputStream in = PropertyUtils.class.getClassLoader().getResourceAsStream("custom.properties");
        try {
            properties.load(in);
            // 加载启用的配置
            String property = properties.getProperty("profiles.active");
            if (!StringUtils.isBlank(property)) {
                InputStream cin = PropertyUtils.class.getClassLoader().getResourceAsStream("custom-" + property + ".properties");
                propertiesCustom.load(cin);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单例
     *
     * @return PropertiesUtils
     */
    public static PropertyUtils getInstance() {
        if (propertiesUtils == null) {
            propertiesUtils = new PropertyUtils();
        }
        return propertiesUtils;
    }

    /**
     * 根据属性名读取值
     * 先去主配置查询，如果查询不到，就去启用配置查询
     *
     * @param name 名称
     */
    public String getProperty(String name) {
        String val = properties.getProperty(name);
        if (StringUtils.isBlank(val)) {
            val = propertiesCustom.getProperty(name);
        }
        return val;
    }

    public static void main(String[] args) {
        PropertyUtils pro = PropertyUtils.getInstance();
        String mainProperty = pro.getProperty("custom.properties.main");
        System.out.println(mainProperty);
        System.out.println("================");
        String profileProperty = pro.getProperty("dev.name");
        System.out.println(profileProperty);
    }
}