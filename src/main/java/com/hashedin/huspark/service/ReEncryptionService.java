package com.hashedin.huspark.service;

import com.hashedin.huspark.constants.ApplicationConstants;
import com.hashedin.huspark.entity.Secret;
import com.hashedin.huspark.repository.SecretRepo;
import com.hashedin.huspark.utils.AESEncryptionDecryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReEncryptionService {

    @Scheduled(cron = "1 0 0 * * ?")
    public void reEncryptSecrets() {
        List<Secret> secrets = fetchSecrets();
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);

        for (Secret secret : secrets) {
            if (secret.getLastModified().isBefore(tenDaysAgo)) {
                reEncrypt(secret);
            }
        }
    }

    @Autowired
    private SecretRepo secretRepo;
    private List<Secret> fetchSecrets() {
        List<Secret> secrets=secretRepo.findAll();
        return secrets;
    }

    @Autowired
    private AESEncryptionDecryption aesEncryptionDecryption;
    private void reEncrypt(Secret secret) {
        String decreptedData= aesEncryptionDecryption.decrypt(secret.getEncryptedData(), ApplicationConstants.SECRET_KEY);
        secret.setEncryptedData(aesEncryptionDecryption.encrypt(decreptedData,ApplicationConstants.SECRET_KEY));
    }
}
