package hn.uth.proyectopm1;


import java.util.List;


import hn.uth.proyectopm1.Producto;

public class Pedido {
    private String correoUsuario;
    private List<Producto> productos;
    private double gastoTotal;
    private String latitud;
    private String longitud;
    private String estado;
    private String celular;
    private float calificacion;

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    private String id;

    private String correoRepartidor;

    public String getCorreoRepartidor() {
        return correoRepartidor;
    }

    public void setCorreoRepartidor(String correoRepartidor) {
        this.correoRepartidor = correoRepartidor;
    }

    public Pedido() {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Pedido(String correoUsuario, List<Producto> productos, double gastoTotal, String latitud, String longitud, String estado, String celular, String id,String correoRepartidor,float calificacion) {
        this.correoUsuario = correoUsuario;
        this.productos = productos;
        this.gastoTotal = gastoTotal;
        this.latitud=latitud;
        this.longitud=longitud;
        this.estado=estado;
        this.celular=celular;
        this.id=id;
        this.correoRepartidor=correoRepartidor;
        this.calificacion=calificacion;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public double getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }
}