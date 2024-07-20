package HospitalManagementSystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/HMS";
    private static final String username = "root";
    private static final String password = "root";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(con, sc);
            Doctor doctor = new Doctor(con);
            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, con, sc);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection con, Scanner sc) {
        System.out.print("Enter Doctor ID or Name: ");
        String doctorInput = sc.nextLine();

        int doctorId;
        try {
            doctorId = Integer.parseInt(doctorInput);
            if (!doctor.getDoctorById(doctorId)) {
                System.out.println("Doctor with ID " + doctorId + " not found.");
                return;
            }
        } catch (NumberFormatException e) {
            doctorId = getDoctorIdByName(doctorInput, con);
            if (doctorId == -1) {
                System.out.println("Doctor with name \"" + doctorInput + "\" not found.");
                return;
            }
        }

        List<String> availableDates = getAvailableDatesForDoctor(doctorId, con);
        if (availableDates.isEmpty()) {
            System.out.println("No available dates found for this doctor.");
            return;
        }

        System.out.println("Available Dates:");
        printTable(availableDates);

        System.out.print("Book your preferred date (YYYY-MM-DD): ");
        String appointmentDate = sc.nextLine();

        if (availableDates.contains(appointmentDate)) {
            String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(NULL, ?, ?)";
            try {
                PreparedStatement preparedStatement = con.prepareStatement(appointmentQuery);
                preparedStatement.setInt(1, doctorId);
                preparedStatement.setString(2, appointmentDate);
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Appointment booked successfully!");
                } else {
                    System.out.println("Failed to book appointment. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please choose a correct date from the available dates.");
        }
    }

    public static int getDoctorIdByName(String doctorName, Connection con) {
        String query = "SELECT id FROM doctors WHERE name = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, doctorName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String> getAvailableDatesForDoctor(int doctorId, Connection con) {
        List<String> availableDates = new ArrayList<>();
        String query = "SELECT DISTINCT appointment_date FROM appointments WHERE doctor_id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("appointment_date").toLocalDate();
                availableDates.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableDates;
    }

    public static void printTable(List<String> dates) {
        System.out.println("+-----------------+");
        System.out.println("| Available Dates |");
        System.out.println("+-----------------+");
        for (String date : dates) {
            System.out.printf("| %-15s |\n", date);
        }
        System.out.println("+-----------------+");
    }
}
