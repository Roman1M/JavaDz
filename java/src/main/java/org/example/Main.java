package org.example;

import com.github.javafaker.Faker;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/postlol";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nОберіть дію:");
                System.out.println("1. Додати новий запис");
                System.out.println("2. Показати усіх тварин");
                System.out.println("3. Видалити запис");
                System.out.println("4. Змінити запис");
                System.out.println("5. Згенерувати випадкові записи");
                System.out.println("6. Вийти");

                int choice = scanner.nextInt();
                scanner.nextLine(); // очищаємо буфер

                switch (choice) {
                    case 1 -> insertAnimal(connection, scanner);
                    case 2 -> showAllAnimals(connection);
                    case 3 -> deleteAnimal(connection, scanner);
                    case 4 -> updateAnimal(connection, scanner);
                    case 5 -> {
                        System.out.print("Введіть кількість записів для генерації -> ");
                        int count = scanner.nextInt();
                        generateAndInsertAnimals(connection, count);
                    }
                    case 6 -> {
                        connection.close();
                        System.out.println("Програма завершена.");
                        return;
                    }
                    default -> System.out.println("Невірний вибір, спробуйте ще раз.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Помилка при роботі з БД: " + e.getMessage());
        }
    }

    private static void insertAnimal(Connection conn, Scanner scanner) throws SQLException {
        Animal animal = new Animal();

        System.out.print("Вкажіть назву -> ");
        animal.setName(scanner.nextLine());

        System.out.print("Вкажіть вид тварини -> ");
        animal.setSpecies(scanner.nextLine());

        System.out.print("Вкажіть вік -> ");
        animal.setAge(scanner.nextInt());

        System.out.print("Вкажіть вагу -> ");
        animal.setWeight(scanner.nextDouble());

        String sql = "INSERT INTO animals (name, species, age, weight) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, animal.getName());
        preparedStatement.setString(2, animal.getSpecies());
        preparedStatement.setInt(3, animal.getAge());
        preparedStatement.setDouble(4, animal.getWeight());

        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Тварину успішно додано!");
        }
        preparedStatement.close();
    }

    private static void showAllAnimals(Connection conn) throws SQLException {
        String sql = "SELECT * FROM animals";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String species = resultSet.getString("species");
            int age = resultSet.getInt("age");
            double weight = resultSet.getDouble("weight");

            System.out.println("ID: " + id + ", Name: " + name + ", Species: " + species + ", Age: " + age + ", Weight: " + weight);
        }

        resultSet.close();
        statement.close();
    }

    private static void deleteAnimal(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Введіть ID тварини для видалення -> ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM animals WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Тварину успішно видалено!");
        } else {
            System.out.println("Тварину з таким ID не знайдено.");
        }

        preparedStatement.close();
    }

    private static void updateAnimal(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Введіть ID тварини для оновлення -> ");
        int id = scanner.nextInt();
        scanner.nextLine(); // очищаємо буфер

        System.out.print("Введіть нову назву -> ");
        String name = scanner.nextLine();

        System.out.print("Введіть новий вид -> ");
        String species = scanner.nextLine();

        System.out.print("Введіть новий вік -> ");
        int age = scanner.nextInt();

        System.out.print("Введіть нову вагу -> ");
        double weight = scanner.nextDouble();

        String sql = "UPDATE animals SET name = ?, species = ?, age = ?, weight = ? WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, species);
        preparedStatement.setInt(3, age);
        preparedStatement.setDouble(4, weight);
        preparedStatement.setInt(5, id);

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Дані тварини успішно оновлено!");
        } else {
            System.out.println("Тварину з таким ID не знайдено.");
        }

        preparedStatement.close();
    }

    private static void generateAndInsertAnimals(Connection conn, int count) throws SQLException {
        Faker faker = new Faker();
        Random random = new Random();

        String sql = "INSERT INTO animals (name, species, age, weight) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        for (int i = 0; i < count; i++) {
            String name = faker.name().firstName();
            String species = faker.animal().name();
            int age = random.nextInt(15) + 1;
            double weight = 5.0 + (50.0 - 5.0) * random.nextDouble();

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, species);
            preparedStatement.setInt(3, age);
            preparedStatement.setDouble(4, weight);
            preparedStatement.addBatch();

            if (i % 1000 == 0) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.executeBatch();
        preparedStatement.close();
        System.out.println(count + " випадкових записів додано до бази даних.");
    }
}
