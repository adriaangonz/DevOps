package es.educantabria.devops;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia para la colección de servidores.
 * Permite trabajar con MySQL en ejecución y con almacenamiento en memoria en tests unitarios.
 */
public interface ServidorRepository {
    List<Servidor> listarTodos();
    Optional<Servidor> buscarPorId(int id);
    boolean agregar(Servidor servidor);
    boolean actualizar(int id, String nuevoNombre, String nuevoEstado);
    boolean eliminar(int id);
    int contar();
}
