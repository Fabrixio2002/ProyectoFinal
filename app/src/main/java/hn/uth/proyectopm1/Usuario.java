package hn.uth.proyectopm1;

public class Usuario {
    private String email;
    private String descripcion;
    private String fotoUrl;
    private String celular;
    private String latitud;
    private String longitud;
    private String rol;

    // Constructor vacío (obligatorio para Firebase)
    public Usuario() {
    }

    public Usuario(String email, String descripcion, String fotoUrl, String celular, String latitud, String longitud, String rol) {
        this.email = email;
        this.descripcion = descripcion;
        this.fotoUrl = fotoUrl;
        this.celular = celular;
        this.latitud = latitud;
        this.longitud = longitud;
        this.rol = rol;
    }

    // Métodos getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
