package es.educantabria.devops;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de la colección de servidores (CRUD).
 * Delega la persistencia en el ServidorRepository configurado.
 *
 * @author Adrián González Gil - 2º DAM
 */
public class ServidorService {

    private final ServidorRepository repository;

    public ServidorService() {
        this(new MySqlServidorRepository());
    }

    public ServidorService(ServidorRepository repository) {
        this.repository = repository;
    }

    public List<Servidor> listarTodos() {
        return repository.listarTodos();
    }

    public Optional<Servidor> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public boolean agregar(Servidor servidor) {
        return repository.agregar(servidor);
    }

    public boolean actualizar(int id, String nuevoNombre, String nuevoEstado) {
        return repository.actualizar(id, nuevoNombre, nuevoEstado);
    }

    public boolean eliminar(int id) {
        return repository.eliminar(id);
    }

    public int contar() {
        return repository.contar();
    }
}
