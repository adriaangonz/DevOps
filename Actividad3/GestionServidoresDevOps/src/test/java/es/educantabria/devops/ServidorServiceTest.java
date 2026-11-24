package es.educantabria.devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas unitarias con JUnit 5 para validar el CRUD completo
 * y la integridad de la colección de servidores.
 *
 * @author Adrián González Gil - 2º DAM
 */
class ServidorServiceTest {

    private ServidorService service;

    @BeforeEach
    void setUp() {
        service = new ServidorService(new InMemoryServidorRepository());
    }

    @Test
    @DisplayName("1. Verificar que se cargan los 3 elementos precargados iniciales")
    void testElementosPrecargados() {
        List<Servidor> lista = service.listarTodos();
        assertNotNull(lista, "La lista de servidores no debe ser nula");
        assertEquals(3, lista.size(), "Deben existir exactamente 3 elementos precargados al inicio");

        assertTrue(service.buscarPorId(1).isPresent(), "Debe existir el servidor con ID 1");
        assertTrue(service.buscarPorId(2).isPresent(), "Debe existir el servidor con ID 2");
        assertTrue(service.buscarPorId(3).isPresent(), "Debe existir el servidor con ID 3");
    }

    @Test
    @DisplayName("2. CRUD - CREATE: Añadir un nuevo servidor exitosamente")
    void testAgregarServidorExitoso() {
        Servidor nuevo = new Servidor(4, "srv-k8s-master", "192.168.10.40", "ACTIVO");
        boolean resultado = service.agregar(nuevo);

        assertTrue(resultado, "El servidor debe agregarse correctamente");
        assertEquals(4, service.contar(), "La colección debe contener ahora 4 servidores");

        Optional<Servidor> buscado = service.buscarPorId(4);
        assertTrue(buscado.isPresent(), "El servidor con ID 4 debe poder encontrarse");
        assertEquals("srv-k8s-master", buscado.get().getNombre(), "El nombre debe coincidir");
        assertEquals("192.168.10.40", buscado.get().getIp(), "La IP debe coincidir");
        assertEquals("ACTIVO", buscado.get().getEstado(), "El estado debe coincidir");
    }

    @Test
    @DisplayName("3. CRUD - CREATE: Rechazar añadir servidor con ID duplicado")
    void testAgregarServidorIdDuplicado() {
        Servidor duplicado = new Servidor(1, "srv-duplicado", "192.168.10.99", "INACTIVO");
        boolean resultado = service.agregar(duplicado);

        assertFalse(resultado, "No se debe permitir agregar un servidor con ID ya existente");
        assertEquals(3, service.contar(), "El tamaño de la colección no debe cambiar");
    }

    @Test
    @DisplayName("4. CRUD - READ: Búsqueda por ID existente e inexistente")
    void testBuscarPorId() {
        Optional<Servidor> existente = service.buscarPorId(2);
        assertTrue(existente.isPresent(), "El servidor con ID 2 debe existir");
        assertEquals("srv-bd-postgres", existente.get().getNombre());

        Optional<Servidor> inexistente = service.buscarPorId(999);
        assertTrue(inexistente.isEmpty(), "Un ID que no existe debe retornar un Optional vacío");
    }

    @Test
    @DisplayName("5. CRUD - UPDATE: Modificar datos de un servidor existente")
    void testActualizarServidor() {
        boolean resultado = service.actualizar(1, "srv-web-nginx-v2", "MANTENIMIENTO");
        assertTrue(resultado, "La actualización del servidor 1 debe ser exitosa");

        Optional<Servidor> modificado = service.buscarPorId(1);
        assertTrue(modificado.isPresent());
        assertEquals("srv-web-nginx-v2", modificado.get().getNombre(), "El nombre debe haberse actualizado");
        assertEquals("MANTENIMIENTO", modificado.get().getEstado(), "El estado debe haberse actualizado");
        assertEquals("192.168.10.10", modificado.get().getIp(), "La IP debe permanecer inalterada");
    }

    @Test
    @DisplayName("6. CRUD - UPDATE: Fallo al actualizar servidor con ID inexistente")
    void testActualizarServidorInexistente() {
        boolean resultado = service.actualizar(999, "nombre-fantasma", "INACTIVO");
        assertFalse(resultado, "No se puede actualizar un servidor que no existe");
    }

    @Test
    @DisplayName("7. CRUD - DELETE: Eliminar un servidor de la colección")
    void testEliminarServidor() {
        boolean resultado = service.eliminar(3);
        assertTrue(resultado, "La eliminación del servidor con ID 3 debe retornar true");
        assertEquals(2, service.contar(), "La colección debe quedar con 2 elementos");

        Optional<Servidor> buscado = service.buscarPorId(3);
        assertTrue(buscado.isEmpty(), "El servidor eliminado ya no debe encontrarse en la colección");
    }

    @Test
    @DisplayName("8. CRUD - DELETE: Fallo al eliminar un servidor inexistente")
    void testEliminarServidorInexistente() {
        boolean resultado = service.eliminar(500);
        assertFalse(resultado, "Eliminar un ID inexistente debe retornar false");
        assertEquals(3, service.contar(), "La colección no debe verse alterada");
    }

    @Test
    @DisplayName("9. CRUD COMPLETO: Ciclo de vida completo (Create -> Read -> Update -> Delete)")
    void testCrudCompletoCicloDeVida() {
        int idPrueba = 10;
        Servidor s = new Servidor(idPrueba, "srv-test-ciclo", "10.0.0.1", "ACTIVO");
        assertTrue(service.agregar(s));
        assertEquals(4, service.contar());

        Optional<Servidor> opt = service.buscarPorId(idPrueba);
        assertTrue(opt.isPresent());
        assertEquals("srv-test-ciclo", opt.get().getNombre());

        assertTrue(service.actualizar(idPrueba, "srv-test-actualizado", "INACTIVO"));
        assertEquals("srv-test-actualizado", service.buscarPorId(idPrueba).get().getNombre());
        assertEquals("INACTIVO", service.buscarPorId(idPrueba).get().getEstado());

        assertTrue(service.eliminar(idPrueba));
        assertTrue(service.buscarPorId(idPrueba).isEmpty());
        assertEquals(3, service.contar());
    }
}
