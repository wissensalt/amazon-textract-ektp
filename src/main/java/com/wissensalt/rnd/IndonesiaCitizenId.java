package com.wissensalt.rnd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : <a href="mailto:wissensalt@gmail.com">Achmad Fauzi</a>
 * @since : 2020-10-11
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndonesiaCitizenId implements Serializable {

    private String fileName;
    private String bucketName;

    private String province;
    private String cityOrDistrict;
    private String NIK;
    private String name;
    private String pob;
    private String dob;
    private String gender;
    private String bloodType;
    private String address;
    private String neighborhoodAssociation;
    private String citizensAssociation;
    private String village;
    private String subDistrict;
    private String religion;
    private String maritalStatus;
    private String job;
    private String citizenship;
    private String validUntil;
    private String createdLocation;
    private String createdDate;
}
