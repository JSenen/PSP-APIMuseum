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
    private String title;
    private String culture;
    private String period;
    private String country;
    private String artistDisplayName;
    private String artistDisplayBio;
    private String artistNationality;
    private String objectDate;
    private String medium;
    private String dimensions;
}
