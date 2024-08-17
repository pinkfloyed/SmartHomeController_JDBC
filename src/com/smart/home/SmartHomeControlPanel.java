package com.smart.home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SmartHomeControlPanel extends JFrame {
    private JComboBox<String> roomSelector;
    private JCheckBox lightSwitch;
    private JCheckBox fanSwitch;
    private JCheckBox doorLockSwitch;
    private JCheckBox windowSwitch;
    private JCheckBox motionSensorSwitch;
    private JCheckBox acSwitch;
    private JSlider temperatureSlider;
    private JSlider humiditySlider;
    private JLabel temperatureLabel;
    private JLabel humidityLabel;
    private JButton saveButton;

    private Connection connection;

    public SmartHomeControlPanel() {
        // Initialize the GUI components
        setTitle("Smart Home Control Panel");
        setSize(600, 400);
        setLayout(new GridLayout(11, 1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        roomSelector = new JComboBox<>(new String[]{"Living Room", "Bedroom", "Kitchen", "Garage"});
        lightSwitch = new JCheckBox("Light On/Off");
        fanSwitch = new JCheckBox("Fan On/Off");
        doorLockSwitch = new JCheckBox("Door Lock/Unlock");
        windowSwitch = new JCheckBox("Window Open/Closed");
        motionSensorSwitch = new JCheckBox("Motion Sensor Active/Inactive");
        acSwitch = new JCheckBox("AC On/Off");

        temperatureSlider = new JSlider(16, 30, 22);
        temperatureLabel = new JLabel("Temperature: " + temperatureSlider.getValue() + "°C");

        humiditySlider = new JSlider(0, 100, 45);
        humidityLabel = new JLabel("Humidity: " + humiditySlider.getValue() + "%");

        saveButton = new JButton("Save Settings");

        add(new JLabel("Select Room:"));
        add(roomSelector);
        add(lightSwitch);
        add(fanSwitch);
        add(doorLockSwitch);
        add(windowSwitch);
        add(motionSensorSwitch);
        add(acSwitch);
        add(temperatureLabel);
        add(temperatureSlider);
        add(humidityLabel);
        add(humiditySlider);
        add(saveButton);

        // Database connection setup
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/SmartHome", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Event listeners
        roomSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRoomSettings(roomSelector.getSelectedItem().toString());
            }
        });

        temperatureSlider.addChangeListener(e -> temperatureLabel.setText("Temperature: " + temperatureSlider.getValue() + "°C"));
        humiditySlider.addChangeListener(e -> humidityLabel.setText("Humidity: " + humiditySlider.getValue() + "%"));

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRoomSettings(roomSelector.getSelectedItem().toString());
            }
        });

        loadRoomSettings(roomSelector.getSelectedItem().toString());
        setVisible(true);
    }

    private void loadRoomSettings(String room) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT light_status, temperature, fan_status, door_lock_status, window_status, motion_sensor, ac_status, humidity " +
                            "FROM devices WHERE room_name = ?");
            stmt.setString(1, room);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lightSwitch.setSelected(rs.getBoolean("light_status"));
                temperatureSlider.setValue(rs.getInt("temperature"));
                fanSwitch.setSelected(rs.getBoolean("fan_status"));
                doorLockSwitch.setSelected(rs.getBoolean("door_lock_status"));
                windowSwitch.setSelected(rs.getBoolean("window_status"));
                motionSensorSwitch.setSelected(rs.getBoolean("motion_sensor"));
                acSwitch.setSelected(rs.getBoolean("ac_status"));
                humiditySlider.setValue(rs.getInt("humidity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load settings!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRoomSettings(String room) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE devices SET light_status = ?, temperature = ?, fan_status = ?, door_lock_status = ?, window_status = ?, " +
                            "motion_sensor = ?, ac_status = ?, humidity = ? WHERE room_name = ?");
            stmt.setBoolean(1, lightSwitch.isSelected());
            stmt.setInt(2, temperatureSlider.getValue());
            stmt.setBoolean(3, fanSwitch.isSelected());
            stmt.setBoolean(4, doorLockSwitch.isSelected());
            stmt.setBoolean(5, windowSwitch.isSelected());
            stmt.setBoolean(6, motionSensorSwitch.isSelected());
            stmt.setBoolean(7, acSwitch.isSelected());
            stmt.setInt(8, humiditySlider.getValue());
            stmt.setString(9, room);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Settings saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save settings!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartHomeControlPanel::new);
    }
}
