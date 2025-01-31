package com.example.ordermanagementservice.utils;

import com.example.ordermanagementservice.clients.KafkaProducerClient;
import com.example.ordermanagementservice.clients.UserManagementServiceClient;
import com.example.ordermanagementservice.dtos.*;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class KafkaEventGenerator {

    @Autowired
    KafkaProducerClient kafkaProducerClient;

    @Autowired
    UserManagementServiceClient userManagementServiceClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${welcomeEmail.sender}")
    private String senderEmail;

    private Boolean sendEmail(EmailDto emailDto, String topic) {
        try {
            kafkaProducerClient.sendMessage(topic, objectMapper.writeValueAsString(emailDto));
            log.info("Sent email to topic: {}", topic);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return true;
    }

    public void sendPaymentEmail(OrderResponsePaymentLinkDto order) {
        String subject = "Action Required: Payment Pending for Order – Complete Now! ⏳";
        String body = """
            Hi %s,
            
            Your order (#%s) has been received, but payment is still pending. Complete your payment 
            now to ensure your items are reserved and processed for shipping.
        
            What’s Next?
            - 💳 Complete Payment: Click here (%s) to finalize your payment securely.
            - 📦 Order Summary: View your order details here (#) for reference.
            - ⏳ Hurry! Your items are reserved for 24 hours – complete payment to avoid cancellation.
        
            Need Help with Payment?
            - Facing issues? Reply to this email or contact us at support@shop.ecommerce.
            - Payment methods accepted: Credit/Debit Cards, PayPal, and Wallet.
        
            Why Shop With Us?
            - 🔒 100%% Secure Payment Processing
            - 🚚 Fast Shipping Guarantee
            - 📞 24/7 Customer Support
        
            Thank you for choosing Shop.Ecommerce!
        
            Warm regards,
            The Shop.Ecommerce Team
            shop.ecommerce | Order Tracking | Contact Us
        
            P.S. Once payment is confirmed, you’ll receive a shipping update within 1–2 business days.
        """;

        UserResponseDto user = userManagementServiceClient.getUserById(order.getUserId());
        body = String.format(
                body,
                user.getFirstName(),    // %s → First name
                order.getOrderId(),     // %s → Order ID
                order.getPaymentUrl()   // %s → Payment URL
        );

        EmailDto emailDto = new EmailDto();
        emailDto.setFromEmail(senderEmail);
        emailDto.setToEmail(user.getEmail());
        emailDto.setSubject(subject);
        emailDto.setBody(body);

        sendEmail(emailDto, "payment-email");
    }

    public void sendConfirmOrderEmail(Order order) {
        String subject = "Order Confirmed! Your Order is Being Processed \uD83C\uDF89 ";
        String body = """  
            Hi %s,  

            Thank you for shopping with Shop.Ecommerce! 🛍️  

            Your order has been confirmed, and we’re already preparing it for shipment.  

            **Order Details:**  
            - **Order Number**: #%s  
            - **Order Date**: %s  
            - **Items Purchased**:  
              %s
            - **Total Amount**: %s  

            **What’s Next?**  
            - 📦 We’ll notify you as soon as your order ships.  
            - 🔍 Track your order status anytime [here](#).  
            - ❓ Questions? Reply to this email or contact support@shop.ecommerce.  

            **Shipping Information:**  
            - **Delivery Address**:  
              %s
            - **Estimated Delivery**: %s  

            **Why You’ll Love Shopping With Us:**  
            - ✅ 100%% Quality Guarantee  
            - 🔒 Secure Payment Processing  
            - 🚚 Fast & Reliable Shipping  

            Thank you for trusting Shop.Ecommerce! We’re excited for you to receive your order.  

            Warm regards,  
            The Shop.Ecommerce Team  
            shop.ecommerce | Track Order | Contact Us  

            P.S. Follow us on [Social Media](#) for exclusive offers and updates!  
        """;

        UserResponseDto user = userManagementServiceClient.getUserById(order.getUserId());
        body = String.format(
                body,
                user.getFirstName(),
                order.getOrderId(),
                order.getOrderDate(),
                order.getOrderItems().stream()
                        .map(orderItem -> "\n" + orderItem.getProductSnapshot() + "\n" + orderItem.getQuantity())
                        .collect(Collectors.toList()),
                order.getTotalAmount(),
                order.getDeliverySnapshot(),
                order.getExpectedDeliveryDate()
        );

        EmailDto emailDto = new EmailDto();
        emailDto.setFromEmail(senderEmail);
        emailDto.setToEmail(user.getEmail());
        emailDto.setSubject(subject);
        emailDto.setBody(body);

        sendEmail(emailDto, "confirm-order-email");
    }

    public void sendPaymentFailureEmail(Order order) {
        String subject = "Payment Failed – Your Order Was Not Processed ❌";
        String body = """  
            Hi %s,  

            We’re sorry – your recent order attempt with Shop.Ecommerce **could not be completed** due to a payment failure.  

            **What Happened?**  
            - The payment method you provided was declined.  
            - **No order was placed**, and your cart items have not been reserved.  

            **How to Proceed:**  
            1. 🔄 **Try Again**: Click [here](#) to restart your order with a valid payment method.  
            2. 💳 **Update Payment Details**: Ensure your card has sufficient funds and correct billing information.  
            3. 📞 **Need Help?** Contact us at support@shop.ecommerce for assistance.  

            **Common Payment Issues:**  
            - Expired card or insufficient funds.  
            - Incorrect CVV or billing address.  
            - Bank authorization delays.  

            **Why Retry with Us?**  
            - 🔒 Secure checkout with encryption.  
            - 🚚 Same fast shipping on successful orders.  
            - 💬 24/7 support to guide you.  

            **Your Cart Items:**  
            %s

            We’d love to fulfill your order – please try again or reply to this email for help!  

            Warm regards,  
            The Shop.Ecommerce Team  
            shop.ecommerce | FAQs | Contact Us  

            P.S. Your payment was **not charged** – no funds were deducted from your account.  
            """;

        UserResponseDto user = userManagementServiceClient.getUserById(order.getUserId());
        body = String.format(
                body,
                user.getFirstName(),
                order.getOrderItems().stream()
                        .map(orderItem -> "\n" + orderItem.getProductSnapshot() + "\n" + orderItem.getQuantity())
                        .collect(Collectors.toList())
            );

        EmailDto emailDto = new EmailDto();
        emailDto.setFromEmail(senderEmail);
        emailDto.setToEmail(user.getEmail());
        emailDto.setSubject(subject);
        emailDto.setBody(body);

        sendEmail(emailDto, "payment-failure-email");
    }
}
