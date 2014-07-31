package it.governoedits.util;

public final class RequiredPropertyNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public RequiredPropertyNotFoundException(String msg) {
        super(msg);
    }

}
