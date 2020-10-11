package com.wissensalt.rnd;

import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : <a href="mailto:wissensalt@gmail.com">Achmad Fauzi</a>
 * @since : 2020-10-11
 **/
@NoArgsConstructor
public class IndonesiaCitizenIdKey {
    static final String PROVINCE = "PROVINSI";
    static final String CITY = "KOTA";
    static final String DISTRICT = "KABUPATEN";
    static final String NIK = "NIK";
    static final String NAME = "Nama";
    static final String PLACE_AND_DATE_OF_BIRTH = "Tempat/Tgl Lahir";
    static final String GENDER = "Jenis Kelamin";
    static final String BLOOD_TYPE = "Gol. Darah";
    static final String ADDRESS = "Alamat";
    static final String NEIGHBORHOOD_AND_CITIZENS_ASSOCIATION = "RT/RW";
    static final String VILLAGE = "Kel/Desa";
    static final String SUB_DISTRICT = "Kecamatan";
    static final String RELIGION = "Agama";
    static final String MARITAL_STATUS = "Status Perkawinan";
    static final String JOB = "Pekerjaan";
    static final String CITIZENSHIP = "Kewarganegaraan";
    static final String VALID_UNTIL = "Berlaku Hingga";

    public final String [] ARRAY_KEY = new String[] {
            PROVINCE, CITY, DISTRICT, NIK, NAME, PLACE_AND_DATE_OF_BIRTH, GENDER, BLOOD_TYPE, ADDRESS,
            NEIGHBORHOOD_AND_CITIZENS_ASSOCIATION, VILLAGE, SUB_DISTRICT, RELIGION, MARITAL_STATUS, JOB, CITIZENSHIP,
            VALID_UNTIL
    };

    public Map<String, String> orderMap;

    public void initOrder() {
        orderMap = new LinkedHashMap<>();
        Arrays.stream(ARRAY_KEY).forEach(key -> orderMap.put(key, null));
    }

    public boolean containsPreviousKey(String currentKey, String blockText) {
        if (currentKey.equals(blockText)) {
            return true;
        }

        int currentIndex = getCurrentIndex(currentKey);
        if (currentIndex > 0) {
            currentIndex--;
            for (int a= currentIndex; a> -1; a--) {
                if (blockText.contains(ARRAY_KEY[a])) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean previousKeyIsEmpty(String currentKey) {
        int currentIndex = getCurrentIndex(currentKey);
        if (currentIndex > 0) {
            return orderMap.get(ARRAY_KEY[currentIndex - 1]) == null;
        }

        return false;
    }

    public boolean nextKeyIsEmpty(String currentKey) {
        int currentIndex = getCurrentIndex(currentKey);
        if (currentIndex < ARRAY_KEY.length) {
            return orderMap.get(ARRAY_KEY[currentIndex + 1]) == null;
        }

        return false;
    }

    private int getCurrentIndex(String currentKey) {
        for (int a = 0; a< ARRAY_KEY.length; a++) {
            if (currentKey.equals(ARRAY_KEY[a])) {
                return a;
            }
        }

        return -1;
    }
}
