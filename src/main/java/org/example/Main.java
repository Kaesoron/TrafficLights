package org.example;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    // Параметры
    static final int NUM_ROADS = 4; //Число дорог
    static final int MAX_CARS = 10; // Максимальное число машин в очереди за ход
    static final int MAX_PEDESTRIANS = 10; // Максимальное число пешеходов в очереди за ход
    static final int CROSS_TIME_CARS = 10; // время пересечения для машины
    static final int CROSS_TIME_PEDESTRIANS = 20; // время пересечения для пешехода
    static final int SIMULATION_DURATION = 300; // Время симуляции в секундах
    static Random random = new Random();

    // Очереди
    static int[] cars = new int[NUM_ROADS];
    static int[] pedestriansLeft = new int[NUM_ROADS];
    static int[] pedestriansRight = new int[NUM_ROADS];

    public static void main(String[] args) {
        initializeQueues();

        int timeElapsed = 0;

        while (timeElapsed < SIMULATION_DURATION) {
            System.out.println("\nВремя: " + timeElapsed + " сек");

            // Вывод информации о текущих очередях
            printQueueInfo();

            // Находим оптимальные группы для включения
            String group = findOptimalGroup();
            int greenLightDuration = calculateQueueTime(group);

            // Запускаем группы светофоров
            activateGroup(group, greenLightDuration);

            // Обновляем очереди
            updateQueues();

            // Переход ко времени следующего включения
            timeElapsed += greenLightDuration;
        }

        System.out.println("\nСимуляция завершена.");
    }

    private static void initializeQueues() {
        for (int i = 0; i < NUM_ROADS; i++) {
            cars[i] = random.nextInt(MAX_CARS + 1);
            pedestriansLeft[i] = random.nextInt(MAX_PEDESTRIANS + 1);
            pedestriansRight[i] = random.nextInt(MAX_PEDESTRIANS + 1);
        }
    }

    private static void printQueueInfo() {
        for (int i = 0; i < NUM_ROADS; i++) {
            System.out.printf("A%d: авто - %d | П%dл - %d, П%dп - %d\n",
                    i + 1, cars[i], i + 1, pedestriansLeft[i], i + 1, pedestriansRight[i]);
        }
    }

    private static String findOptimalGroup() {
        // Возможные группы для включения
        String[] groups = new String[]{
                "A1_A3",
                "A2_A4",
                "А1_П2п_П2л",
                "А2_П3п_П3л",
                "А3_П4п_П4л",
                "А4_П1п_П1л",
                "П_all"
        };

        // Выбор группы с максимальным временем прохождения
        int maxQueue = 0;
        String optimalGroup = "";

        for (String group : groups) {
            int queueTime = calculateQueueTime(group);
            if (queueTime > maxQueue) {
                maxQueue = queueTime;
                optimalGroup = group;
            }
        }

        System.out.println("Выбрана группа светофоров: " + optimalGroup);
        return optimalGroup;
    }

    private static int calculateQueueTime(String group) {
        return switch (group) {
            case "A1_A3" -> Math.min(cars[0], cars[2])*CROSS_TIME_CARS;
            case "A2_A4" -> Math.min(cars[1], cars[3])*CROSS_TIME_CARS;
            case "A1_П2п_П2л" -> Math.min(cars[0]*CROSS_TIME_CARS, Math.min(pedestriansLeft[1], pedestriansRight[1])*CROSS_TIME_PEDESTRIANS);
            case "A2_П3п_П3л" -> Math.min(cars[1]*CROSS_TIME_CARS, Math.min(pedestriansLeft[2], pedestriansRight[2])*CROSS_TIME_PEDESTRIANS);
            case "A3_П4п_П4л" -> Math.min(cars[2]*CROSS_TIME_CARS, Math.min(pedestriansLeft[3], pedestriansRight[3])*CROSS_TIME_PEDESTRIANS);
            case "A4_П1п_П1л" -> Math.min(cars[3]*CROSS_TIME_CARS, Math.min(pedestriansLeft[4], pedestriansRight[4])*CROSS_TIME_PEDESTRIANS);
            case "П_all" -> Math.min(Math.min(Math.min(pedestriansLeft[0], pedestriansRight[0]), Math.min(pedestriansLeft[1], pedestriansRight[1])),
                    Math.min(Math.min(pedestriansLeft[2], pedestriansRight[2]), Math.min(pedestriansLeft[3], pedestriansRight[3])))*CROSS_TIME_PEDESTRIANS;
            default -> 0;
        };
    }

    private static void activateGroup(String group, int duration) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        if (group.equals("A1_A3")) {
            executor.execute(() -> simulateTraffic("A1", 0));
            executor.execute(() -> simulateTraffic("A3", 2));
        } else if (group.equals("A2_A4")) {
            executor.execute(() -> simulateTraffic("A2", 1));
            executor.execute(() -> simulateTraffic("A4", 3));
        } else if (group.equals("A1_П2п_П2л")) {
            executor.execute(() -> simulateTraffic("A1", 0));
            executor.execute(() -> simulatePedestrians("П2п", 1));
            executor.execute(() -> simulatePedestrians("П2л", 1));
        } else if (group.equals("A2_П3п_П3л")) {
            executor.execute(() -> simulateTraffic("A2", 1));
            executor.execute(() -> simulatePedestrians("П2п", 2));
            executor.execute(() -> simulatePedestrians("П2л", 2));

        } else if (group.equals("A3_П4п_П4л")) {
            executor.execute(() -> simulateTraffic("A3", 2));
            executor.execute(() -> simulatePedestrians("П2п", 3));
            executor.execute(() -> simulatePedestrians("П2л", 3));
        } else if (group.equals("A4_П1п_П1л")) {
            executor.execute(() -> simulateTraffic("A4", 3));
            executor.execute(() -> simulatePedestrians("П2п", 0));
            executor.execute(() -> simulatePedestrians("П2л", 0));
        } else if (group.equals("П_all")) {
            for (int i = 0; i < NUM_ROADS; i++) {
                int roadId = i;
                executor.execute(() -> simulatePedestrians("П" + (roadId + 1) + "п и П" + (roadId + 1) + "л", roadId));
            }
        }

        executor.shutdown();
        try {
            Thread.sleep(duration * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void simulateTraffic(String lightName, int roadId) {
        int carsToPass = cars[roadId];
        for (int i = 0; i < carsToPass; i++) {
            System.out.println("Машина " + (i + 1) + " на светофоре " + lightName + " пересекает перекресток за " + CROSS_TIME_CARS + " секунд.");
            cars[roadId]--;
        }
    }

    private static void simulatePedestrians(String lightName, int roadId) {
        int pedestriansToCross = Math.min(pedestriansLeft[roadId] + pedestriansRight[roadId], 5);
        for (int i = 0; i < pedestriansToCross; i++) {
            System.out.println("Пешеход " + (i + 1) + " на светофоре " + lightName + " пересекает перекресток за " + CROSS_TIME_PEDESTRIANS + " секунд.");
            pedestriansLeft[roadId]--;
            pedestriansRight[roadId]--;
        }
    }

    private static void updateQueues() {
        for (int i = 0; i < NUM_ROADS; i++) {
            cars[i] += random.nextInt(3);
            pedestriansLeft[i] += random.nextInt(3);
            pedestriansRight[i] += random.nextInt(3);
        }
    }
}
