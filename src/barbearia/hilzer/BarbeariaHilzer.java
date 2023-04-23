package barbearia.hilzer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import barbearia.hilzer.barber.Barber;
import barbearia.hilzer.customer.Customer;
import static barbearia.hilzer.customer.CustomersConstants.*;

/**
 * Classe principal da solução do problema da barbearia de Hilzer.
 * 
 * @author Gustavo Simon
 */
public class BarbeariaHilzer {

    /** Thread responsável por fazer a geração de clientes que serão atendidos pela barbearia */
    private final Thread customerGenerator = new Thread(new CustomerGenerator());

    /** Barbeiro número 1 */
    private final Thread b1 = new Thread(new Barber());
    /** Barbeiro número 2 */
    private final Thread b2 = new Thread(new Barber());
    /** Barbeiro número 3 */
    private final Thread b3 = new Thread(new Barber());

    /** Fila com os clientes que estão esperando no sofá */
    private final Queue<Customer> couch = new LinkedList<Customer>();
    /** Fila com os clientes que estão esperando em pé */
    private final Queue<Customer> standingsCustomers = new LinkedList<Customer>();

    public static void main(String[] args) {
        new BarbeariaHilzer().run();
    
    // requisitos:
    //  só cliente e barbeiros devem ser threads

    //   1. três cadeiras; não são threads
    //    2. três barbeiros; ok
    //   3. uma sala de espera com um sofá de quatro lugares;
    //   4. o número total de clientes permitidos na barbearia é 20;
    //   5. nenhum cliente entrará se a capacidade do local estiver satisfeita;
    //   6. se o cliente entrou e tiver lugar no sofá ele se senta, caso contrário ele espera em pé;
    //   7. quando um barbeiro está livre para atender, o cliente que está a mais tempo no sofá é atendido e o que está a mais tempo em pé se senta;
    //   8. qualquer barbeiro pode aceitar pagamento, mas somente um cliente pode pagar por vez, porque só há uma maquina de cartão (POS / TEF);
    //   9. os barbeiros dividem o seu tempo entre cortar cabelo, receber pagamento e dormir enquanto esperam por um cliente.       
    }

    /**
     * Inicia a execução do problema da barbearia de Hilzer.
     */
    public void run() {
        // Inicia a thread de geração de clientes que serão atendidos pela barbearia
        customerGenerator.run();


    }

    /**
     * Classe para gerar os clientes que serão atendidos pela barbearia.
     */
    private class CustomerGenerator implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    var random = new Random();
                    Thread.sleep(random.nextLong(10000));
                    System.out.println("Cliente " + CUSTOMERS_NAMES[random.nextInt(CUSTOMERS_NAMES.length)] + " chegou a barbearia!");
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }

    }
    
}
