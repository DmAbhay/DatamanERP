package dataman.erp.auth.enums;

public enum LoginMode {
    MOBILE(1),
    EMAIL(2),
    USERNAME(3);

    private final int code;

    LoginMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static String getByCode(int code) {
        for (LoginMode type : values()) {
            if (type.code == code) {
                return type.name();
            }
        }
        throw new IllegalArgumentException("No identifier found for code: " + code);
    }
}

