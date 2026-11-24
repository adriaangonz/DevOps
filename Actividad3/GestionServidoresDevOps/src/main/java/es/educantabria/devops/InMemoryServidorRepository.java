package es.educantabria.devops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en memoria de ServidorRepository (utilizada en pruebas unitarias sin BD externa).
 */
public class InMemoryServidorRepository implements ServidorRepository {

    private final List<Servidor> servidores = new ArrayList<>();

    public InMemoryServidorRepository() {
        servidores.add(new Servidor(1, "srv-web-nginx", "192.168.10.10", "ACTIVO"));
        servidores.add(new Servidor(2, "srv-bd-postgres", "192.168.10.20", "ACTIVO"));
        servidores.add(new Servidor(3, "srv-ci-jenkins", "192.168.10.30", "MANTENIMIENTO"));
    }

    @Override
    public List<Servidor> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(servidores));
    }

    @Override
    public Optional<Servidor> buscarPorId(int id) {
        return servidores.stream().filter(s -> s.getId() == id).findFirst();
    }

    @Override
    public boolean agregar(Servidor servidor) {
        if (servidor == null || servidor.getId() <= 0) {
            return false;
        }
        if (buscarPorId(servidor.getId()).isPresent()) {
            return false;
        }
        return servidores.add(servidor);
    }

    @Override
    public boolean actualizar(int id, String nuevoNombre, String nuevoEstado) {
        Optional<Servidor> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Servidor s = opt.get();
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                s.setNombre(nuevoNombre);
            }
            if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                s.setEstado(nuevoEstado);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return servidores.removeIf(s -> s.getId() == id);
    }

    @Override
    public int contar() {
        return servidores.size();
    }
}
