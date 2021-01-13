package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.*;
import com.apd.tema2.intersections.*;
import com.apd.tema2.utils.Constants;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

/**
 * Clasa Factory ce returneaza implementari ale InterfaceHandler sub forma unor
 * clase anonime.
 */
public class IntersectionHandlerFactory {

    public static IntersectionHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of
        // them)
        // road in maintenance - 2 ways 1 lane each, X cars at a time
        // road in maintenance - 1 way, M out of N lanes are blocked, X cars at a time
        // railroad blockage for s seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " has reached the semaphore, now waiting...");
                    try {
                        Main.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Car " + car.getId() + " has waited enough, now driving...");
                }
            };
            case "simple_n_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");
                    try {
                        Main.sem.acquire();
                        System.out.println("Car " + car.getId() + " has entered the roundabout");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        sleep(Main.waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int t = Main.waitingTime / 1000;
                    System.out.println("Car " + car.getId() + " has exited the roundabout after " + t + " seconds");
                    Main.sem.release();
                }
            };
            case "simple_strict_1_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " has reached the roundabout");
                    try {
                        Main.sem.acquire();
                        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " + car.getStartDirection());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        sleep(Main.waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int t = Main.waitingTime / 1000;
                    System.out.println("Car " + car.getId() + " has exited the roundabout after " + t + " seconds");
                    Main.sem.release();
                }
            };
            case "simple_strict_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    Main.barrier1 = new CyclicBarrier(Main.init);
                    for(int i = 0; i < Main.lanesNo; i++) {
                        Main.strict_sem.add(new Semaphore(Main.acceptedCars));
                    }
                    System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");
                    try {
                        Main.barrier.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Main.strict_sem.get(car.getStartDirection()).acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Car " + car.getId() + " was selected to enter the roundabout from lane " + car.getStartDirection());
                    try {
                        Main.barrier1.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Car " + car.getId() + " has entered the roundabout from lane " + car.getStartDirection());
                    try {
                        sleep(Main.waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Main.barrier1.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    int t = Main.waitingTime / 1000;
                    System.out.println("Car " + car.getId() + " has exited the roundabout after " + t + " seconds");
                    try {
                        Main.barrier1.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Main.strict_sem.get(car.getStartDirection()).release();

                }
            };
            case "simple_max_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI


                    // Continuati de aici
                    for(int i = 0; i < Main.lanesNo; i++) {
                        Main.strict_sem.add(new Semaphore(Main.acceptedCars));
                    }
                    try {
                        System.out.println("Car " + car.getId() + " has reached the roundabout from lane " + car.getStartDirection());
                        Main.strict_sem.get(car.getStartDirection()).acquire();
                        System.out.println("Car " + car.getId() + " has entered the roundabout from lane " + car.getStartDirection());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    try {
                        sleep(Main.waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int t = Main.waitingTime / 1000;
                    System.out.println("Car " + car.getId() + " has exited the roundabout after " + t + " seconds");

                    Main.strict_sem.get(car.getStartDirection()).release();

                }
            };
            case "priority_intersection" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici
                }
            };
            case "crosswalk" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "simple_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "complex_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "railroad" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            default -> null;
        };
    }
}
