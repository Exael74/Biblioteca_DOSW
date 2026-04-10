package edu.eci.dosw.tdd.core.util;

public final class ApiMessages {

    private ApiMessages() {
    }

    // Book messages
    public static final String BOOK_NOT_FOUND = "El libro no fue encontrado";
    public static final String BOOK_NOT_AVAILABLE = "El libro no está disponible, no hay copias disponibles";
    public static final String BOOK_ALREADY_EXISTS = "Ya existe un libro con ese ID";
    public static final String BOOK_INVALID_STOCK = "El stock total debe ser mayor a 0";
    public static final String BOOK_INVALID_COPIES = "Las copias disponibles no pueden ser negativas ni superar el stock total";
    public static final String BOOK_RETURN_EXCEEDS_STOCK = "No se puede devolver, las copias disponibles ya igualan el stock total";
    public static final String BOOK_HAS_ACTIVE_LOANS = "No se puede eliminar el libro porque tiene préstamos activos";

    // User messages
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String USER_ALREADY_EXISTS = "Ya existe un usuario con ese ID";
    public static final String USER_HAS_ACTIVE_LOANS = "No se puede eliminar el usuario porque tiene préstamos activos";

    // Auth messages
    public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String USERNAME_ALREADY_EXISTS = "El nombre de usuario ya está registrado";

    // Loan messages
    public static final String LOAN_NOT_FOUND = "Préstamo no encontrado";
    public static final String LOAN_ALREADY_RETURNED = "El préstamo ya fue devuelto";
    public static final String LOAN_CANNOT_DELETE_ACTIVE = "No se puede eliminar un préstamo activo, debe devolverse primero";
}
