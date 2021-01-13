package com.apd.tema2;

import com.apd.tema2.entities.Intersection;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.io.Reader;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Main {
    public static Pedestrians pedestrians = null;
    public static Intersection intersection;
    public static int carsNo;
    public static int acceptedCars = 0;
    public static int lanesNo = 0;
    public static int waitingTime;
    public static CyclicBarrier barrier;
    public static CyclicBarrier barrier1;
    public static int init;
    public static Semaphore sem;
    public static ArrayList<Semaphore> strict_sem = new ArrayList<>();
    public static int highPrCars;
    public static int lowPrCars;

    public static void main(String[] args) {
        Reader fileReader = Reader.getInstance(args[0]);
        Set<Thread> cars = fileReader.getCarsFromInput();
        barrier = new CyclicBarrier(carsNo);
        sem = new Semaphore(acceptedCars);

        for(Thread car : cars) {
            car.start();
        }

        if(pedestrians != null) {
            try {
                Thread p = new Thread(pedestrians);
                p.start();
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Thread car : cars) {
            try {
                car.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
