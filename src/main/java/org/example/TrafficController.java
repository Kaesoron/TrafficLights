package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficController {
    private List<TrafficLight> lights;
    private Random rand;

    public TrafficController() {
        lights = new ArrayList<>();
        rand = new Random();
    }

    public void addLight(TrafficLight light) {
        lights.add(light);
    }

    public void updateTrafficData(String lightId, int cars, int pedestrians) {
        for (TrafficLight light : lights) {
            if (light.getId().equals(lightId)) {
                light.updateQueues(cars, pedestrians);
                break;
            }
        }
    }

    public void adjustLights() {
        // Для каждого светофора регулируем время зелёного света
        for (TrafficLight light : lights) {
            light.adjustGreenTime();
        }
    }

    public void printLightStatuses() {
        for (TrafficLight light : lights) {
            System.out.println(light);
        }
    }

    public void simulateTraffic() {
        // Для каждого автомобильного светофора генерируем случайное количество машин, двигающихся прямо или направо
        for (int i = 1; i <= Main.NUM_ROADS; i++) {
            int cars = rand.nextInt(20) + 1;  // Случайное количество автомобилей (от 1 до 20)
            int straightCars = rand.nextInt(cars + 1);  // Случайное количество автомобилей, едущих прямо
            int rightCars = cars - straightCars;  // Остальные автомобили едут направо

            // Обновляем данные для автомобильного светофора
            updateTrafficData("Auto" + i, straightCars + rightCars, 0);  // Суммарное количество машин
        }

        // Пешеходы
        for (int i = 1; i <= Main.NUM_ROADS; i++) {
            int pedestrians = rand.nextInt(20) + 1;  // Случайное количество пешеходов (от 1 до 20)
            updateTrafficData("Pedestrian" + i + "Left", 0, pedestrians);  // Пешеходы на левой стороне
            updateTrafficData("Pedestrian" + i + "Right", 0, pedestrians);  // Пешеходы на правой стороне
        }
    }
}
