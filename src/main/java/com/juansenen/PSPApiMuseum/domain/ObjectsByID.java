package com.juansenen.PSPApiMuseum.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectsByID {
    private int objectID;
    private String accessionNumber;
    private String accessionYear;
    private boolean isPublicDomain;
    private String primaryImage;
    private List<String> additionalImages;
    private String department;
    private String objectName;
    private String title;
    private String culture;
    private String period;
    private String artistDisplayName;
    private String artistBeginDate;
    private String artistEndDate;
    private String medium;
    private String country;
}
