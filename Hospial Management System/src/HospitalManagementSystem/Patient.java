package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private Connection con;
    private Scanner sc;

    public Patient(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
    }

    public void addPatient() {
        System.out.print("Enter Patient Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Patient Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Patient Gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter Patient Phone number: ");
        long phone = sc.nextLong();
        sc.nextLine();
        System.out.print("Enter Patient Address (multi-line, end with a blank line): ");
        StringBuilder addressBuilder = new StringBuilder();
        String line;

        while (true) {
            line = sc.nextLine();
            if (line.isEmpty()) {
                break;
            }
            addressBuilder.append(line).append("\n");
        }

        String address = addressBuilder.toString().trim(); // Remove trailing newline and trim leading/trailing spaces

        try {
            String query = "INSERT INTO patients(name, age, gender, phone, address) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            preparedStatement.setLong(4, phone);
            preparedStatement.setString(5, address);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient Details Added Successfully!!");
            } else {
                System.out.println("Failed to add Patient Details!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewPatients() {
        String query = "SELECT id, name, age, gender, phone, address FROM patients";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients: ");
            System.out.println("+------------+--------------------+----------+------------+----------------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     | Phone                | Address              |");
            System.out.println("+------------+--------------------+----------+------------+----------------------+----------------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                long phone = resultSet.getLong("phone");
                String address = resultSet.getString("address");

                // Handle null address (if any)
                if (address == null) {
                    address = "N/A";
                }

                System.out.printf("| %-10s | %-18s | %-8s | %-10s | %-20s | %-20s |\n", id, name, age, gender, phone, address);
                System.out.println("+------------+--------------------+----------+------------+----------------------+----------------------+");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
