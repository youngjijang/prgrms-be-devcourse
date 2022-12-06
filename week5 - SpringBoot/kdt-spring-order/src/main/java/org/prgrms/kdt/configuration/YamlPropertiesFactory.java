package org.prgrms.kdt.configuration;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;


public class YamlPropertiesFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource){
        var yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(resource.getResource());

        var properties = yamlPropertiesFactoryBean.getObject();
        return new PropertiesPropertySource(resource.getResource().getFilename(), properties);

    }
}
