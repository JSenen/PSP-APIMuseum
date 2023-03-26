package com.juansenen.PSPApiMuseum.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Objects {
    private int total;
    private List<Integer> objectIDs;
}
