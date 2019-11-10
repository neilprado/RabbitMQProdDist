import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println("No aguardo das mensagens, caso deseje sair, pressione CTRL + C");

        channel.basicQos(1);

        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            String mensagem = new String(message.getBody(), "UTF-8");

            System.out.println("Recebido '" + mensagem + "'");
            try {
                doWork(mensagem);
            }finally {
                System.out.println("Feito!!");
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
        });

    }

    private static void doWork(String tarefa){
        for (char c: tarefa.toCharArray()){
            if(c == '.'){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
