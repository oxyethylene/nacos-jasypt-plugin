package com.github.oxyethylene.nacos.plugin.jasypt;

import com.alibaba.nacos.plugin.environment.spi.CustomEnvironmentPluginService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.iv.IvGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Nacos Db password encrypt plugin service implementation.
 *
 * @author oxyethylene
 */
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class NacosDbPasswordJasyptEncryptionPluginService implements CustomEnvironmentPluginService {

    private static final Logger log = LoggerFactory.getLogger(NacosDbPasswordJasyptEncryptionPluginService.class);

    private static final String DB_PWD_KEY = "db.password.0";

    public static final String JASYPT_ENCRYPTOR_PASSWORD = "jasypt.encryptor.password";
    public static final String JASYPT_ENCRYPTOR_ALGORITHM = "jasypt.encryptor.algorithm";
    public static final String JASYPT_ENCRYPTOR_IV_GENERATOR = "jasypt.encryptor.iv-generator-classname";

    public static final String ENC_PREFIX = "ENC(";

    public static final String ENC_SUFFIX = ")";

    @Override
    public Map<String, Object> customValue(Map<String, Object> properties) {
        String pwd = (String) properties.get(DB_PWD_KEY);
        if (pwd.startsWith(ENC_PREFIX) && pwd.endsWith(ENC_SUFFIX)) {
            String realPwd = pwd.substring(ENC_PREFIX.length(), pwd.length() - ENC_SUFFIX.length());
            StandardPBEStringEncryptor encryptor = initEncryptor(properties);
            pwd = encryptor.decrypt(realPwd);
            properties.put(DB_PWD_KEY, pwd);
        }
        return properties;
    }

    private static StandardPBEStringEncryptor initEncryptor(Map<String, Object> properties) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        String jasyptEncryptorPassword = (String) properties.get(JASYPT_ENCRYPTOR_PASSWORD);
        log.info("Using Jasypt encryptor password: {}", jasyptEncryptorPassword);
        String jasyptEncryptorAlgorithm = (String) properties.get(JASYPT_ENCRYPTOR_ALGORITHM);
        log.info("Using Jasypt encryptor algorithm: {}", jasyptEncryptorAlgorithm);
        String jasyptEncryptorIvGenerator = (String) properties.get(JASYPT_ENCRYPTOR_IV_GENERATOR);
        IvGenerator ivGenerator;
        try {
            ivGenerator = Class.forName(jasyptEncryptorIvGenerator).asSubclass(IvGenerator.class).newInstance();
        } catch (Exception e) {
            throw new EncryptionInitializationException("Failed to initialize Jasypt encryptor iv generator", e);
        }
        log.info("Using Jasypt encryptor iv generator: {}", jasyptEncryptorIvGenerator);

        encryptor.setAlgorithm(jasyptEncryptorAlgorithm);
        encryptor.setPassword(jasyptEncryptorPassword);
        encryptor.setIvGenerator(ivGenerator);
        return encryptor;
    }

    @Override
    public Set<String> propertyKey() {
        Set<String> keys = new HashSet<>(1);
        keys.add(DB_PWD_KEY);
        keys.add(JASYPT_ENCRYPTOR_PASSWORD);
        keys.add(JASYPT_ENCRYPTOR_ALGORITHM);
        keys.add(JASYPT_ENCRYPTOR_IV_GENERATOR);
        return keys;
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public String pluginName() {
        return "NacosDbPasswordJasyptEncryptionPluginService";
    }
}
