package com.example.posbackendspring.service.impl;

import com.example.posbackendspring.customStatusCode.ErrorStatus;
import com.example.posbackendspring.dao.OrderDao;
import com.example.posbackendspring.dto.OrderStatus;
import com.example.posbackendspring.dto.impl.OrderDTO;
import com.example.posbackendspring.dto.impl.OrderDetailDTO;
import com.example.posbackendspring.entity.impl.OrderEntity;
import com.example.posbackendspring.exception.CustomerNotFoundException;
import com.example.posbackendspring.exception.DataPersistException;
import com.example.posbackendspring.service.OrderDetailService;
import com.example.posbackendspring.service.OrderService;
import com.example.posbackendspring.util.AppUtil;
import com.example.posbackendspring.util.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.posbackendspring.service.impl.CustomerServiceImpl.logger;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private Mapping mapping;
    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    public void saveOrder(OrderDTO orderDTO) {
        logger.info("Saved order", orderDTO.getOrderID());
        OrderEntity order = orderDao.save(mapping.toOrderEntity(orderDTO));
        if (order==null){
            logger.error("Order could not be saved", orderDTO.getOrderID());
            throw new DataPersistException("Order Note Saved");
        }else {
            logger.info("Order has been saved successfully", orderDTO.getOrderID());
            for (OrderDetailDTO orderDetailDTO:orderDTO.getOrderDetailDTO()){
                orderDetailDTO.setId(AppUtil.generateOrderDetailId());
                orderDetailDTO.setOrder(orderDTO);
                orderDetailService.saveOrderDetail(new OrderDetailDTO(
                        orderDetailDTO.getId(),
                        orderDetailDTO.getDate(),
                        orderDetailDTO.getCustomerId(),
                        orderDetailDTO.getCustomerName(),
                        orderDetailDTO.getCustomerCity(),
                        orderDetailDTO.getCustomerTel(),
                        orderDetailDTO.getItemName(),
                        orderDetailDTO.getOrderQTY(),
                        orderDetailDTO.getUnitPrice(),
                        orderDetailDTO.getItem(),
                        orderDetailDTO.getOrder()
                ));
                logger.info("Order detail has been saved successfully", orderDTO.getOrderID());
            }
        }
    }

    @Override
    public void updateOrder(String orderId, OrderDTO orderDTO) {
        logger.info("Updated order", orderId);
        Optional<OrderEntity> tmpOrder = orderDao.findById(orderId);
        if (tmpOrder.isPresent()){
            tmpOrder.get().setDate(orderDTO.getDate());
            tmpOrder.get().setDiscountRate(orderDTO.getDiscountRate());
            tmpOrder.get().setDiscount(orderDTO.getDiscount());
            tmpOrder.get().setSubTotal(orderDTO.getSubTotal());
            tmpOrder.get().setBalance(orderDTO.getBalance());
            tmpOrder.get().setCustomer(mapping.toCustomerEntity(orderDTO.getCustomerId()));
            tmpOrder.get().setOrderDetailsList(mapping.toOrderEntityList(orderDTO.getOrderDetailDTO()));
            logger.info("Order has been updated successfully", orderId);
        }else {
            logger.warn("Order not found for update", orderId);
        }
    }

    @Override
    public void deleteOrder(String orderId) {
        logger.info("Deleted order", orderId);
        Optional<OrderEntity> tmpOrder = orderDao.findById(orderId);
        if (!tmpOrder.isPresent()){
            logger.error("Order not found for deletion", orderId);
            throw new CustomerNotFoundException("OrderId with " + orderId + "Not Found!");
        }else {
            orderDao.deleteById(orderId);
            logger.info("Order has been deleted successfully", orderId);
        }
    }

    @Override
    public OrderStatus getOrder(String orderId) {
        logger.info("Fetching order with Id", orderId);
        if (orderDao.existsById(orderId)){
            logger.info("Order found", orderId);
            return (OrderStatus) mapping.toOrderDTO(orderDao.getReferenceById(orderId));
        }else {
            logger.warn("Order not found", orderId);
            return (OrderStatus) new ErrorStatus(2,"Selected order not found");
        }
    }
    @Override
    public List<OrderDTO> getAllOrder() {
        logger.info("Fetching all orders");
        List<OrderDTO> orders = mapping.toOrderList(orderDao.findAll());
        if (orders.isEmpty()) {
            logger.warn("No orders found ");
        } else {
            logger.info("Number of orders found ", orders.size());
        }
        return orders;
    }
}
