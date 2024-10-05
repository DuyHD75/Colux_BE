package com.dcode.product_service.enumeration;

public enum ImageType {
    THUMBNAIL("Thumbnail"),
    GALLERY("Gallery"),
    PRIMARY("Primary");

    private final String displayName;

    ImageType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
