package com.disertation.login_signup.controller;

import com.disertation.login_signup.model.SensorData;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
public class SensorController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String RASPBERRY_PI_URL = "http://192.168.137.204:5000/api/live-data";

    @GetMapping("/data")
    public List<SensorData> getSensorData(@RequestParam(defaultValue = "marasti") String location) {
        if ("live".equalsIgnoreCase(location)) {
            try {
                SensorData[] data = restTemplate.getForObject(RASPBERRY_PI_URL, SensorData[].class);
                return data != null ? List.of(data) : List.of();
            } catch (Exception e) {
                System.err.println("Pi connection error: " + e.getMessage());
                return List.of();
            }
        }

        // Distinct trend curves for static locations
        List<SensorData> list = new ArrayList<>();
        LocalTime time = LocalTime.now().minusMinutes(45);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (int i = 0; i < 10; i++) {
            String t = time.plusMinutes(i * 5).format(fmt);
            SensorData reading;

            switch (location.toLowerCase()) {
                case "gheorgheni": // Low pollution, high humidity park area
                    reading = new SensorData(t, 2.5 + i * 0.4, 5.1 + i * 0.6, 10.2 + i * 0.8, 65.0 - i * 0.5, 18.2 + i * 0.3);
                    break;
                case "manastur": // Moderate residential area
                    reading = new SensorData(t, 7.0 + (i % 3) * 2, 14.2 + (i % 4) * 3, 25.0 + (i % 2) * 5, 55.0 - i * 0.8, 20.5 + i * 0.2);
                    break;
                case "marasti": // High pollution traffic hub
                default:
                    reading = new SensorData(t, 15.0 + i * 1.2, 28.5 + i * 2.1, 52.0 + i * 3.5, 42.0 - i * 0.3, 23.0 + i * 0.4);
                    break;
            }
            list.add(reading);
        }
        return list;
    }
}