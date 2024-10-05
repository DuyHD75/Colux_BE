package com.dcode.product_service.enumeration;

public enum CategoryType {
    PAINT("paint"),
    WALLPAPER("wallpaper"),
    FLOOR("floor");

    private String name;

    CategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
