package org.example;

public class TrafficLight {
    private String id; // Идентификатор светофора
    private int carsQueue; // Количество автомобилей в очереди
    private int pedestriansQueue; // Количество пешеходов в очереди
    private int greenTime; // Время зелёного сигнала
    private boolean isPedestrian; // Это пешеходный светофор или автомобильный?

    public TrafficLight(String id, boolean isPedestrian) {
        this.id = id;
        this.isPedestrian = isPedestrian;
        this.carsQueue = 0;
        this.pedestriansQueue = 0;
        this.greenTime = isPedestrian ? 10 : 15; // Пешеходные светофоры начинают с 10 сек, автомобильные с 15 сек
    }

    public void updateQueues(int cars, int pedestrians) {
        this.carsQueue = cars;
        this.pedestriansQueue = pedestrians;
    }

    public void adjustGreenTime() {
        if (isPedestrian) {
            // Для пешеходов увеличиваем время зелёного света, если много пешеходов, но не более 30 секунд
            greenTime = Math.min(30, 10 + pedestriansQueue);
        } else {
            // Для автомобилей увеличиваем время зелёного света в зависимости от количества машин, но не более минуты
            greenTime = Math.min(60, 15 + carsQueue);
        }
    }

    public String getId() {
        return id;
    }

    public int getGreenTime() {
        return greenTime;
    }

    public boolean isPedestrian() {
        return isPedestrian;
    }

    @Override
    public String toString() {
        return "Светофор " + id + ": Авт. в очереди - " + carsQueue + ", Пешеходы в очереди - " + pedestriansQueue + ", Зеленый свет - " + greenTime + " секунд.";
    }
}
