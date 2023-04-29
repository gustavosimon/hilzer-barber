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
    private final Thread b1 = new Thread(new Barber("Gilson"));
    /** Barbeiro número 2 */
    private final Thread b2 = new Thread(new Barber("Marcos"));
    /** Barbeiro número 3 */
    private final Thread b3 = new Thread(new Barber("Barba Negra"));

    /** Fila com os clientes que estão esperando no sofá (deve armazenar no máximo 4 pessoas) */
    private final Queue<Customer> couch = new ConcurrentLinkedQueue<Customer>();
    /** Fila com os clientes que estão esperando em pé */
    private final Queue<Customer> standingsCustomers = new ConcurrentLinkedQueue<Customer>();
    /** Fila com o clientes que estão esperando para fazer o pagamento */
    private final Queue<Customer> customerPaying = new ConcurrentLinkedQueue<Customer>();

    public static void main(String[] args) {
        new BarbeariaHilzer().run();
    }

    /**
     * Inicia a execução do problema da barbearia de Hilzer.
     */
    public void run() {
        // Inicia o trabalho dos barbeiros
        b1.start();
        b2.start();
        b3.start();
        // Inicia a thread de geração de clientes que serão atendidos pela barbearia
        customerGenerator.start();
    }

    /**
     * Classe para gerar os clientes que serão atendidos pela barbearia.
     */
    private class CustomerGenerator implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(random.nextLong(5000));
                    Customer customer = new Customer(CUSTOMERS_NAMES[random.nextInt(CUSTOMERS_NAMES.length)]);
                    // System.out.println("Cliente " + customer.getName() + " chegou a barbearia!");
                    customer.run();
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
         * <p>
         * Obs: Esse atributo não possui método setter 
         * para garantir a imutabilidade do objeto.
         * 
         * @return {@code String} nome do cliente
         */
        public String getName() {
            return this.name;
        }

        @Override
        public void run() {
            // Notifica os barbeiros de que um cliente no entrou na barberia
            // b1.notifyAll();
            // b2.notifyAll();
            // b3.notifyAll();
            // Sincroniza o acesso a fila do sofá 
            synchronized (couch) {
                // Se o sofá tem espaço livre, o cliente se senta no sofá
                if (couch.size() < 4) {
                    System.out.println("O cliente " + this.getName() + " sentou no sofá para aguardar!");
                    couch.add(this);
                    return;
                };
            }
            // Se o sofá está cheio, o cliente fica em pé
            standingsCustomers.add(this);
            System.out.println("O cliente " + this.getName() + " está aguardando em pé!");
        }
    }


    /**
     * Classe de implementação de um barbeiro.
     */
    private class Barber implements Runnable {

        private final String barberName;
        
        public Barber(String barberName) {
            this.barberName = barberName;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    // Cliente que será atendido
                    Customer currentCustomer = null;
                    // Se o sofá está vazio, significa que não há clientes para atender
                    // Nesse caso, o barbeiro dorme
                    // synchronized (couch) {
                    //     if (couch.isEmpty()) {
                    //         synchronized (this) {
                    //             this.wait();
                    //         }
                    //     }
                    // Busca o próximo cliente a ser atendido
                    while (currentCustomer == null) {
                        currentCustomer = couch.poll();
                    }
                    
                    // Como um cliente que estava sentado no sofá está sendo atendido, 
                    // o que estava a mais tempo em pé deve se sentar
                    var nextCustomer = standingsCustomers.poll();
                    if (nextCustomer != null) {
                        couch.add(nextCustomer);
                    }
                    // }
    
                    System.out.println("O barber " + this.barberName + " está atendendo o cliente " + currentCustomer.getName());
    
                    // Randomiza o tempo de atendimento pois cada cliente vai demorar um tempo diferente devido 
                    // ao serviço que vai ser realizado
                    Thread.sleep(random.nextLong(20000));
    
                    // Como o cliente já foi atendido, ele deve ir para a lista de clientes a pagar
                    customerPaying.add(currentCustomer);
    
                    // O barbeiro que foi para a estande de pagamento faz o atendimento a todos os clientes que estão na fila de espera
                    synchronized (customerPaying) {
                        while (!customerPaying.isEmpty()) {
                            var customer = customerPaying.poll();
                            Thread.sleep(random.nextLong(5000));
                            System.out.println("O cliente " + customer.getName() + " realizou o pagamento com o barber " + this.barberName + "!");
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }

        }
        
    }

    
}
