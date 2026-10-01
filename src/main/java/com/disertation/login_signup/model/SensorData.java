package com.disertation.login_signup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorData {
    private String timestamp;
    private double pm1;
    private double pm25;
    private double pm10;
    private double humidity;
    private double temperature;
}