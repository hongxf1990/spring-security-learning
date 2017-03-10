package com.petter;

import com.petter.config.AppConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * 相当于web.xml文件
 * @author hongxf
 * @since 2017-03-08 10:17
 */
public class SpringMVCInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * 应用程序上下文配置文件，可以是多个，即相当于多个xml文件配置
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{AppConfig.class};
    }

    /**
     * 获取应用程序上下文配置文件
     * 如果所有配置已经在AppConfig中配置，则可以设为null
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    /**
     * 指定拦截路径
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
