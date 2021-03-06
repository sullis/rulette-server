package com.github.kislayverma.rulette.rest.api.provider;

import com.github.kislayverma.rulette.core.data.IDataProvider;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.rest.config.RuleSystemConfigList;
import com.github.kislayverma.rulette.rest.exception.BadClientException;
import com.github.kislayverma.rulette.rest.exception.ProviderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service loads the rule system configuration from the input config file and provides access to data provider
 * objects
 */
@Component
public class DataProviderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProviderService.class);

    @Autowired
    private DataProviderFactory providerFactory;

    private final Map<String, IDataProvider> providerNameProviderMap = new HashMap<>();

    @PostConstruct
    public final void init() {
        providerFactory.getAllProviderConfigs()
                .stream()
                .forEach(providerConfig -> {
                    IDataProvider provider = providerFactory.getProvider(providerConfig.getName());
                    providerNameProviderMap.put(providerConfig.getName(), provider);
                });
    }

    public List<DataProviderConfig> getAllProviderConfigs() {
        return providerFactory.getAllProviderConfigs();
    }

    public DataProviderConfig getProviderConfig(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            throw new BadClientException("No provider name given");
        }

        return providerFactory.getProviderConfig(providerName);
    }

    public void insertRuleSystemMetadata(String providerName, RuleSystemMetaData ruleSystemMetaData) {
        IDataProvider provider = getProvider(providerName);
        if (provider == null) {
            throw new ProviderNotFoundException("Invalid provider name");
        }
        provider.createRuleSystem(ruleSystemMetaData);
    }

    public void deleteRuleSystemMetadata(String providerName, String ruleSystemName) {
        IDataProvider provider = getProvider(providerName);
        if (provider == null) {
            throw new ProviderNotFoundException("Invalid provider name");
        }
        provider.deleteRuleSystem(ruleSystemName);
    }

    public IDataProvider getProvider(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            throw new BadClientException("No provider name given to load provider");
        }

        return providerNameProviderMap.get(providerName);
    }
}
