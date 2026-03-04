package com.servientrega.locker.enums;

import java.math.BigDecimal;

public enum CompartmentSize {
    S_SMALL(new BigDecimal("33"), new BigDecimal("8"), new BigDecimal("45")),
    M_SMALL(new BigDecimal("33"), new BigDecimal("12"), new BigDecimal("45")),
    MEDIUM(new BigDecimal("33"), new BigDecimal("23"), new BigDecimal("45")),
    LARGE(new BigDecimal("33"), new BigDecimal("30"), new BigDecimal("45"));

    private final BigDecimal width;
    private final BigDecimal height;
    private final BigDecimal depth;

    CompartmentSize(BigDecimal width, BigDecimal height, BigDecimal depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public boolean canFit(BigDecimal packageWidth, BigDecimal packageHeight, BigDecimal packageDepth) {
        return packageWidth.compareTo(width) <= 0 
            && packageHeight.compareTo(height) <= 0 
            && packageDepth.compareTo(depth) <= 0;
    }
}
