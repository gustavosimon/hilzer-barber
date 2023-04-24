package barbearia.hilzer;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static barbearia.hilzer.customer.CustomersConstants.*;

/**
 * Classe principal da solução do problema da barbearia de Hilzer.
 * 
 * @author Gustavo Simon
 */
public class BarbeariaHilzer {

    /** Thread responsável por fazer a geração de clientes que serão atendidos pela barbearia */
    private final Thread customerGenerator = new Thread(new CustomerGenerator());
    /** Objeto para geração de numéricos randômicos */
    private final Random random = new Random();

    /** Barbeiro número 1 */
    private final Thread b1 = new Thread(new Barber());
    /** Barbeiro número 2 */
    private final Thread b2 = new Thread(new Barber());
    /** Barbeiro número 3 */
    private final Thread b3 = new Thread(new Barber());

    /** Fila com os clientes que estão esperando no sofá (deve armazenar no máximo 4 pessoas) */
    private final Queue<Customer> couch = new ConcurrentLinkedQueue<Customer>();
    // /** Fila com os clientes que estão esperando em pé */
    // private final Queue<Customer> standingsCustomers = new ConcurrentLinkedQueue<Customer>();
    // /** Fila com o clientes que estão esperando para fazer o pagamento */
    // private final Queue<Customer> customerPaying = new ConcurrentLinkedQueue<Customer>();

    public static void main(String[] args) {
        new BarbeariaHilzer().run();
    }

    /**
     * Inicia a execução do problema da barbearia de Hilzer.
     */
    public void run() {
        // Inicia a thread de geração de clientes que serão atendidos pela barbearia
        customerGenerator.run();
        // Inicia o trabalho dos barbeiros
        b1.run();
        b2.run();
        b3.run();

    
    // requisitos:
    //   4. o número total de clientes permitidos na barbearia é 20;
    //   5. nenhum cliente entrará se a capacidade do local estiver satisfeita;

    //   6. se o cliente entrou e tiver lugar no sofá ele se senta, caso contrário ele espera em pé;
    //   7. quando um barbeiro está livre para atender, o cliente que está a mais tempo no sofá é atendido e o que está a mais tempo em pé se senta;
    //   8. qualquer barbeiro pode aceitar pagamento, mas somente um cliente pode pagar por vez, porque só há uma maquina de cartão (POS / TEF);
    //   9. os barbeiros dividem o seu tempo entre cortar cabelo, receber pagamento e dormir enquanto esperam por um cliente.       
    //
    //  
    //
    //
    //
    //
    //
    //
    //

    }

    /**
     * Classe para gerar os clientes que serão atendidos pela barbearia.
     */
    private class CustomerGenerator implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(random.nextLong(10000));
                    Customer customer = new Customer(CUSTOMERS_NAMES[random.nextInt(CUSTOMERS_NAMES.length)]);
                    customer.run();
                    System.out.println("Cliente " + customer.getName() + " chegou a barbearia!");
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }

    }

    /**
     * Classe de implementação de um cliente.
     */
    private class Customer implements Runnable {

        /** Nome do cliente */
        private final String name;

        public Customer(String name) {
            this.name = name;
        }

        /**
         * Retorna o nome do cliente.
         * 
         * @return {@code String} nome do cliente
         */
        public String getName() {
            return this.name;
        }

        @Override
        public void run() {
            // Sincroniza o acesso a fila do sofá, para que não aconteça buscar um 
            synchronized (couch) {
                // Se o sofá tem espaço livre, o cliente se senta no sofá
                if (couch.size() < 4) {
                    couch.add(this);
                    return;
                };

            }
            // Se o sofá está cheio, verifica se o cliente pode ficar na barbearia
            // todo: Nesse caso, precisa verificar se a ocupação total da barberia supera 20 clientes
            // if () {

            // }
        }
    }


    /**
     * Classe de implementação de um barbeiro.
     */
    private class Barber implements Runnable {

        public Barber() {}

        @Override
        public void run() {
            try {
                // Se o sofá está vazio, significa que não há clientes para atender
                // Nesse caso, o barbeiro dorme
                if (couch.isEmpty()) {
                    this.wait();
                }
                
            } catch (InterruptedException e) {
                // TODO: handle exception
            }


        }
        
    }

    
}
