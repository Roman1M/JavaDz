package org.example;

import org.example.entities.ClientEntity;
import org.example.entities.OrderEntity;
import org.example.entities.OrderItemEntity;
import org.example.entities.OrderStatusEntity;
import org.example.entities.ServiceEntity;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random();
    private static final com.github.javafaker.Faker faker = new com.github.javafaker.Faker();

    public static void main(String[] args) {
        // Викликаємо метод для вставки даних
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            insertServices(session); // Вставка послуг
            insertClients(session); // Вставка клієнтів
            insertOrderStatuses(session); // Вставка статусів замовлень
            insertOrders(session); // Вставка замовлень
        }

        // Викликаємо метод для виведення списку замовлень
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            printOrdersList(session);  // Виведення списку замовлень
        }
    }

    private static void insertServices(Session session) {
        Transaction transaction = session.beginTransaction();
        String[] servicesList = {
                "Консультація по ремонту",
                "Чистка двигуна",
                "Заміна масла",
                "Ремонт трансмісії",
                "Заміна коліс"
        };
        for (String serviceName : servicesList) {
            ServiceEntity service = new ServiceEntity();
            service.setName(serviceName);
            session.save(service);
        }
        transaction.commit();
    }

    private static void insertClients(Session session) {
        Transaction transaction = session.beginTransaction();
        for (int i = 0; i < 100; i++) {
            ClientEntity client = new ClientEntity();
            client.setFirstName(faker.name().firstName());
            client.setLastName(faker.name().lastName());

            String phone = faker.phoneNumber().phoneNumber();
            // Перевірка довжини номера телефону перед обрізанням
            if (phone.length() > 20) {
                phone = phone.substring(0, 20);
            }

            client.setPhone(phone);
            client.setCar_model(faker.company().industry());  // Генерація випадкової автомобільної моделі
            client.setCar_year(faker.number().numberBetween(2000, 2022));

            session.save(client);
        }
        transaction.commit();
    }

    private static void insertOrderStatuses(Session session) {
        Transaction transaction = session.beginTransaction();
        String[] statuses = {
                "Нове замовлення",
                "В процесі виконання",
                "Виконано",
                "Скасовано клієнтом"
        };

        for (String statusName : statuses) {
            OrderStatusEntity status = new OrderStatusEntity();
            status.setName(statusName);
            session.save(status);
        }
        transaction.commit();
    }

    public static void insertOrders(Session session) {
        Transaction transaction = session.beginTransaction();

        // Генерація випадкових замовлень
        for (int i = 0; i < 100; i++) {
            // Створення нового замовлення
            OrderEntity order = new OrderEntity();
            order.setOrderDate(new Date());

            // Встановлення випадкового статусу
            OrderStatusEntity status = session.get(OrderStatusEntity.class, faker.number().numberBetween(1, 4));
            order.setStatus(status);

            // Встановлення випадкового клієнта
            ClientEntity client = session.get(ClientEntity.class, faker.number().numberBetween(1, 100));
            order.setClient(client);

            // Збереження замовлення
            session.save(order);

            // Створення елементів замовлення (OrderItemEntity)
            for (int j = 0; j < 5; j++) {
                OrderItemEntity orderItem = new OrderItemEntity();
                // Встановлення кількості послуг
                orderItem.setQuantity(faker.number().numberBetween(1, 10));

                // Встановлення послуги
                ServiceEntity service = session.get(ServiceEntity.class, faker.number().numberBetween(1, 10));
                orderItem.setService(service);

                // Встановлення зв'язку з замовленням
                orderItem.setOrder(order);  // Вказуємо замовлення для цього елемента

                // Збереження елементу замовлення
                session.save(orderItem);
            }
        }

        transaction.commit();
    }

    // Метод для виведення всіх замовлень
    public static void printOrdersList(Session session) {
        // Запит для отримання всіх замовлень з бази даних
        List<OrderEntity> orders = session.createQuery("FROM OrderEntity", OrderEntity.class).getResultList();

        // Перевірка, чи є замовлення
        if (orders.isEmpty()) {
            System.out.println("Немає замовлень в базі даних.");
            return;
        }

        for (OrderEntity order : orders) {
            // Перевірка, чи існує клієнт для цього замовлення
            if (order.getClient() != null) {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Client: " + order.getClient().getFirstName() + " " + order.getClient().getLastName());
                System.out.println("Phone: " + order.getClient().getPhone());
            } else {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Client: [не знайдений]");
            }
        }
    }
}
