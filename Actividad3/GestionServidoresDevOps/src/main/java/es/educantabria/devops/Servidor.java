package es.educantabria.devops;

import java.util.Objects;

/**
 * Entidad modelo que representa un Servidor dentro de la infraestructura DevOps.
 * @author Adrián González Gil - 2º DAM
 */
public class Servidor {
    private int id;
    private String nombre;
    private String ip;
    private String estado; // ACTIVO, MANTENIMIENTO, INACTIVO

    public Servidor(int id, String nombre, String ip, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.ip = ip;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servidor servidor = (Servidor) o;
        return id == servidor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[ID: %02d] %-18s | IP: %-15s | Estado: %s", id, nombre, ip, estado);
    }
}
