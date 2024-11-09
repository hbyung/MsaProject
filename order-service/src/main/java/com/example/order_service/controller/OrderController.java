package com.example.order_service.controller;

import com.example.order_service.dto.OrderDto;
import com.example.order_service.jpa.OrderEntity;
import com.example.order_service.mesaagequeue.KafkaProducer;
import com.example.order_service.mesaagequeue.OrderProducer;
import com.example.order_service.service.OrderService;
import com.example.order_service.vo.RequestOrder;
import com.example.order_service.vo.ResponseOrder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/order-service")
@Slf4j
public class OrderController {

    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;

    OrderProducer orderProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer,  OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Order on Port %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        /* jpa */
//        OrderDto createOrder = orderService.createOrder(orderDto);
//        ResponseOrder responseOrder = mapper.map(createOrder, ResponseOrder.class);

        /* kafka */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        /* send this order th the kafka*/
        kafkaProducer.send("example-catalog-topic", orderDto);

//        //중복된 ProductId가 존재할 경우 수량만 변경해서 저장
//        if (orderService.getOrderByProductId(orderDetails.getProductId()) != null) {
//            orderService.ChangeQty(orderDto, orderDto.getQty());
//            ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
//        }

        orderProducer.send("orders", orderDto);
        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);

    }

    @GetMapping("/testSend")
    public ResponseEntity TestSend() {
        long beforeTime = System.currentTimeMillis();
        log.info("beforeTime:" +beforeTime);
        int count = 100000;
        for (int i = 10000; i < count; i++) {
            kafkaProducer.TestSend("example-test-topic", "test"+i);
        }
        long afterTime = System.currentTimeMillis();
        long time = (afterTime - beforeTime)/1000;
        log.info("total time :" + time);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orderList = orderService.getOrderByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v ->{
            result.add(new ModelMapper().map(v, ResponseOrder.class));
                });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
