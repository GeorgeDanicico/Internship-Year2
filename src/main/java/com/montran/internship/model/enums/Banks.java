package com.montran.internship.model.enums;

public enum Banks {
    BT("Banca Transilvania"),
    BRD("BRD"),
    CEC("CEC Bank");

    public final String label;

    private Banks(String label) {
        this.label = label;
    }
}
