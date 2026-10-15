package com.pedidos360.rabbitadmin;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;
@Service public class RabbitAdminService {
 private final RabbitAdmin admin;
 public RabbitAdminService(org.springframework.amqp.rabbit.connection.ConnectionFactory cf){admin=new RabbitAdmin(cf);}
 public void createQueue(String name){admin.declareQueue(QueueBuilder.durable(name).build());}
 public void deleteQueue(String name){admin.deleteQueue(name);}
 public void createExchange(String name){admin.declareExchange(new TopicExchange(name,true,false));}
 public void deleteExchange(String name){admin.deleteExchange(name);}
 public void createBinding(String queue,String exchange,String key){
  admin.declareBinding(new Binding(queue,Binding.DestinationType.QUEUE,exchange,key,null));
 }
 public void deleteBinding(String queue,String exchange,String key){
  admin.removeBinding(new Binding(queue,Binding.DestinationType.QUEUE,exchange,key,null));
 }
}
