package es.educantabria.devops;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Clase principal con menú interactivo por consola para la gestión de la colección en MySQL.
 *
 * @author Adrián González Gil - 2º DAM
 */
public class Main {

    public static void main(String[] args) {
        ServidorService service = new ServidorService();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("==========================================================");
        System.out.println("    SISTEMA DE GESTION DE SERVIDORES DEVOPS (JAVA + MYSQL)");
        System.out.println("   Actividad 3: Despliegue Automatizado con Docker Compose");
        System.out.println("   Alumno: Adrian Gonzalez Gil | Centro: IES Miguel Herrero");
        System.out.println("==========================================================");

        while (!salir) {
            mostrarMenu();
            System.out.print("Seleccione una opcion [1-5]: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String opcionStr = scanner.nextLine().trim();

            switch (opcionStr) {
                case "1":
                    listarServidores(service);
                    break;
                case "2":
                    agregarServidor(service, scanner);
                    break;
                case "3":
                    modificarServidor(service, scanner);
                    break;
                case "4":
                    eliminarServidor(service, scanner);
                    break;
                case "5":
                    System.out.println("\n[+] Saliendo de la aplicacion. ¡Hasta pronto!");
                    salir = true;
                    break;
                default:
                    System.out.println("\n[!] Opcion no valida. Por favor, introduzca un numero del 1 al 5.");
                    break;
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n----------------- MENU PRINCIPAL -----------------");
        System.out.println(" 1. Consultar listado de servidores (MySQL)");
        System.out.println(" 2. Anadir nuevo servidor");
        System.out.println(" 3. Modificar datos de un servidor");
        System.out.println(" 4. Borrar servidor de la coleccion");
        System.out.println(" 5. Salir");
        System.out.println("--------------------------------------------------");
    }

    private static void listarServidores(ServidorService service) {
        System.out.println("\n--- LISTADO DE SERVIDORES EN MYSQL (" + service.contar() + " servidores) ---");
        List<Servidor> lista = service.listarTodos();
        if (lista.isEmpty()) {
            System.out.println(" (La coleccion esta vacia)");
        } else {
            for (Servidor s : lista) {
                System.out.println("  " + s);
            }
        }
    }

    private static void agregarServidor(ServidorService service, Scanner scanner) {
        System.out.println("\n--- ANADIR NUEVO SERVIDOR ---");
        try {
            System.out.print("Introduzca ID numerico: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (service.buscarPorId(id).isPresent()) {
                System.out.println("[ERROR] Ya existe un servidor registrado con el ID " + id);
                return;
            }

            System.out.print("Nombre del servidor (ej: srv-mon-grafana): ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Direccion IP (ej: 192.168.10.45): ");
            String ip = scanner.nextLine().trim();

            System.out.print("Estado [ACTIVO / MANTENIMIENTO / INACTIVO]: ");
            String estado = scanner.nextLine().trim().toUpperCase();

            Servidor nuevo = new Servidor(id, nombre, ip, estado);
            if (service.agregar(nuevo)) {
                System.out.println("[OK] Servidor '" + nombre + "' guardado en MySQL con exito.");
            } else {
                System.out.println("[ERROR] No se pudo anadir el servidor.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El ID debe ser un numero entero valido.");
        }
    }

    private static void modificarServidor(ServidorService service, Scanner scanner) {
        System.out.println("\n--- MODIFICAR SERVIDOR ---");
        try {
            System.out.print("Introduzca ID del servidor a modificar: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Optional<Servidor> opt = service.buscarPorId(id);
            if (opt.isEmpty()) {
                System.out.println("[ERROR] No existe ningun servidor con el ID " + id);
                return;
            }

            Servidor actual = opt.get();
            System.out.println("Servidor actual: " + actual);

            System.out.print("Nuevo nombre (dejar vacio para mantener '" + actual.getNombre() + "'): ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Nuevo estado [ACTIVO / MANTENIMIENTO / INACTIVO] (vacio para mantener '" + actual.getEstado() + "'): ");
            String estado = scanner.nextLine().trim().toUpperCase();

            if (service.actualizar(id, nombre, estado)) {
                System.out.println("[OK] Servidor modificado en MySQL: " + service.buscarPorId(id).get());
            } else {
                System.out.println("[ERROR] Fallo al actualizar el servidor.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El ID debe ser un numero entero valido.");
        }
    }

    private static void eliminarServidor(ServidorService service, Scanner scanner) {
        System.out.println("\n--- ELIMINAR SERVIDOR ---");
        try {
            System.out.print("Introduzca ID del servidor a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (service.eliminar(id)) {
                System.out.println("[OK] Servidor con ID " + id + " eliminado correctamente de MySQL.");
            } else {
                System.out.println("[ERROR] No existe ningun servidor con el ID " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El ID debe ser un numero entero valido.");
        }
    }
}
