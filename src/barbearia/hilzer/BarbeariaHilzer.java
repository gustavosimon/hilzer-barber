package barbearia.hilzer;

import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static barbearia.hilzer.customer.CustomersConstants.*;

/**
 * Classe principal da solução do problema da barbearia de Hilzer.
 * 
 * William Stallings apresenta uma versão mais complicada do problema da barbearia, 
 * que ele atribui a Ralph Hilzer da Universidade da Califórnia. 
 * 
 * O problema consiste em uma barbearia com três barbeiros e três cadeiras próprias de barbeiros, 
 * também existe uma série de lugares para que os clientes possam esperar. 
 * 
 * Tanto os barbeiros quanto os clientes devem ser implementados como Threads.
 * 
 * [✓] - três cadeiras;
 * [✓] - três barbeiros; 
 * [✓] - uma sala de espera com um sofá de quatro lugares;
 * [✓] - o número total de clientes permitidos na barbearia é 20;
 * [✓] - nenhum cliente entrará se a capacidade do local estiver satisfeita;
 * [✓] - se o cliente entrou e tiver lugar no sofá ele se senta, caso contrário ele espera em pé;
 * [✓] - quando um barbeiro está livre para atender, o cliente que está a mais tempo no sofá é atendido e o que está a mais tempo em pé se senta;
 * [✓] - qualquer barbeiro pode aceitar pagamento, mas somente um cliente pode pagar por vez, porque só há uma maquina de cartão (POS / TEF);
 * [✓] - os barbeiros dividem o seu tempo entre cortar cabelo, receber pagamento e dormir enquanto esperam por um cliente.
 * 
 * @author Gustavo Simon
 */
public class BarbeariaHilzer {

    /** Capacidade máxima de clientes da barbearia */
    private static final int MAXIMUM_CAPACITY = 20;

    /** Thread responsável por fazer a geração de clientes que serão atendidos pela barbearia */
    private final Thread customerGenerator = new Thread(new CustomerGenerator());
    /** Objeto para geração de numéricos randômicos */
    private final Random random = new Random();

    /** Barbeiro número 1 */
    private final Barber b1 = new Barber("Gilson");
    /** Barbeiro número 2 */
    private final Barber b2 = new Barber("Marcos");
    /** Barbeiro número 3 */
    private final Barber b3 = new Barber("Barba Negra");

    /** Thread do barbeiro número 1 */
    private final Thread tb1 = new Thread(b1);
    /** Thread do barbeiro número 2 */
    private final Thread tb2 = new Thread(b2);
    /** Thread do barbeiro número 3 */
    private final Thread tb3 = new Thread(b3);

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
        tb1.start();
        tb2.start();
        tb3.start();
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
                    Thread.sleep(random.nextLong(2000));
                    new Customer(CUSTOMERS_NAMES[random.nextInt(CUSTOMERS_NAMES.length)]).run();
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

            // Se a barbearia estiver com a capacidade máxima atingida, não vai ser possível aguardar
            if (isBarberShopCrowded()) {
                System.out.println("O cliente " + this.getName() + " está saindo da barbearia pois o limite de cliente foi atingido!");
                return;
            }

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

        /**
         * Verifica se atingiu a lotação máxima para a barbearia.
         * 
         * @return {@code true} se e somente se a barbearia já 
         *                      possui o limite de {@code MAXIMUM_CAPACITY} clientes
         */
        private boolean isBarberShopCrowded() {
            
            // Inicializa o número de clientes na barbearia
            int numberOfCustomers = 0;

            // Conta os clientes que estão no sofá da barbearia
            synchronized (couch) {
                numberOfCustomers += couch.size();
            }

            // Conta os clientes que estão em pé na barbearia
            synchronized (standingsCustomers) {
                numberOfCustomers += standingsCustomers.size();
            }

            // Conta os clientes que estão realizando o pagamento
            synchronized (customerPaying) {
                numberOfCustomers += customerPaying.size();
            }

            // Conta os clientes que estão sendo atendidos
            numberOfCustomers += b1.isChairBusy() ? 1 : 0;
            numberOfCustomers += b2.isChairBusy() ? 1 : 0;
            numberOfCustomers += b3.isChairBusy() ? 1 : 0;

            return numberOfCustomers >= MAXIMUM_CAPACITY;
        }

    }

    /**
     * Classe de implementação de um barbeiro.
     */
    private class Barber implements Runnable {

        /** Nome do barbeiro */
        private final String barberName;
        /** Cadeira que o barbeiro está utilizando para atender ao cliente */
        private Optional<Customer> chair = Optional.empty();
        
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
                    // Nesse caso, o barbeiro deve dorme
                    while (true) {
                        boolean couchEmpty;
                        synchronized (couch) {
                            couchEmpty = couch.isEmpty();
                        }
                        if (!couchEmpty) {
                            break;
                        }
                        Thread.sleep(1000);
                    }
                    
                    // Busca o próximo cliente a ser atendido
                    while (currentCustomer == null) {
                        synchronized (couch) {
                            currentCustomer = couch.poll();
                            // Como um cliente que estava sentado no sofá está sendo atendido, 
                            // o que estava a mais tempo em pé deve se sentar
                            var nextCustomer = standingsCustomers.poll();
                            if (nextCustomer != null) {
                                couch.add(nextCustomer);
                            }
                        }
                    }
    
                    System.out.println("O barber " + this.barberName + " está atendendo o cliente " + currentCustomer.getName());

                    // Pede para o cliente se sentar
                    chair = Optional.of(currentCustomer);
    
                    // Randomiza o tempo de atendimento pois cada cliente vai demorar um tempo diferente devido 
                    // ao serviço que vai ser realizado
                    Thread.sleep(random.nextLong(40000));

                    // Instrui o cliente a se levantar e proceder com o pagamento
                    chair = Optional.empty();
    
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
                    System.out.println(e);
                }
            }

        }

        /**
         * Retorna se a cadeira do barbeiro está ocupada.
         * 
         * <p>
         * Caso esteja ocupada, significa que um cliente 
         * está sendo atendido nesse momento.
         * 
         * @return {@code true} se e somente se a cadeira 
         *                      está ocupada por um cliente 
         *                      que está sendo atendido pelo 
         *                      barbeiro
         */
        public boolean isChairBusy() {
            return chair.isPresent();
        }
        
    }
    
}
