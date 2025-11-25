package com.sudokugame.cafe;

import javax.swing.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CafeManager {
    private int money;
    private int totalIncome;
    private int gamesPlayed;
    private int gamesWon;
    private int cafeLevel;
    private int customers;
    private int satisfaction;
    private javax.swing.Timer incomeTimer;
    private javax.swing.Timer customerTimer;

    private Map<String, Integer> upgrades;
    private Map<String, Integer> upgradeCosts;
    private List<CafeObject> cafeObjects;
    private List<Customer> customersList;
    private javax.swing.Timer customerSpawnTimer;
    private int maxCustomers;

    public CafeManager() {
        this.money = 500;
        this.totalIncome = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.cafeLevel = 1;
        this.customers = 5;
        this.satisfaction = 70;
        this.customersList = new ArrayList<>();
        this.maxCustomers = 8;

        initializeUpgrades();
        initializeCafeObjects();
        startPassiveSystems();
        startCustomerSystem();
    }

    private void initializeUpgrades() {
        upgrades = new HashMap<>();
        upgradeCosts = new HashMap<>();

        upgrades.put("coffee_quality", 1);
        upgrades.put("pastry_variety", 1);
        upgrades.put("service_speed", 1);
        upgrades.put("decor", 1);
        upgrades.put("marketing", 1);

        upgradeCosts.put("coffee_quality", 150);
        upgradeCosts.put("pastry_variety", 120);
        upgradeCosts.put("service_speed", 100);
        upgradeCosts.put("decor", 200);
        upgradeCosts.put("marketing", 180);
    }

    private void initializeCafeObjects() {
        cafeObjects = new ArrayList<>();
    }

    private void startPassiveSystems() {
        incomeTimer = new javax.swing.Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePassiveIncome();
            }
        });
        incomeTimer.start();

        customerTimer = new javax.swing.Timer(15000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomers();
            }
        });
        customerTimer.start();
    }

    private void startCustomerSystem() {
        customerSpawnTimer = new javax.swing.Timer(8000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnCustomer();
            }
        });
        customerSpawnTimer.start();

        javax.swing.Timer customerUpdateTimer = new javax.swing.Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomersList();
            }
        });
        customerUpdateTimer.start();
    }

    private void generatePassiveIncome() {
        int hourlyIncome = getHourlyIncome();
        int income = hourlyIncome / 360;

        money += income;
        totalIncome += income;

        if (satisfaction > 80) {
            money += (int)(income * 0.2);
        }
    }

    private void updateCustomers() {
        int maxCustomers = getMaxCustomers();
        int change = calculateCustomerChange();

        customers += change;
        customers = Math.max(3, Math.min(maxCustomers, customers));
        updateSatisfaction();
    }

    private void spawnCustomer() {
        if (customersList.size() < maxCustomers) {
            Random rand = new Random();
            int spawnChance = getSpawnChance();
            if (rand.nextInt(100) < spawnChance) {
                int startX = 1000; // Începe din dreapta
                int startY = 300 + rand.nextInt(200);
                Customer newCustomer = new Customer(startX, startY);
                customersList.add(newCustomer);
                System.out.println("👥 New customer spawned! Total: " + customersList.size());
            }
        }
    }

    private void updateCustomersList() {
        for (int i = customersList.size() - 1; i >= 0; i--) {
            Customer customer = customersList.get(i);
            customer.update();

            if (customer.shouldRemove()) {
                if (!customer.isAngry()) {
                    int spending = customer.calculateSpending();
                    money += spending;
                    totalIncome += spending;
                }
                customersList.remove(i);
            }
        }
    }

    private int calculateCustomerChange() {
        Random rand = new Random();
        int baseChange = rand.nextInt(3) - 1;
        double satisfactionEffect = (satisfaction - 50) / 100.0;
        double marketingEffect = upgrades.get("marketing") * 0.1;

        return baseChange + (int)(satisfactionEffect * 2) + (int)marketingEffect;
    }

    private void updateSatisfaction() {
        Random rand = new Random();
        int change = rand.nextInt(5) - 2;
        int serviceBonus = upgrades.get("service_speed") * 3;
        int decorBonus = upgrades.get("decor") * 2;

        satisfaction += change + (serviceBonus + decorBonus) / 10;
        satisfaction = Math.max(30, Math.min(100, satisfaction));
    }

    private int getSpawnChance() {
        int baseChance = 30;
        int marketingBonus = upgrades.get("marketing") * 5;
        int satisfactionBonus = satisfaction / 10;

        return baseChance + marketingBonus + satisfactionBonus;
    }

    public void addGameResult(boolean won, int baseReward) {
        gamesPlayed++;
        if (won) {
            gamesWon++;
            double qualityBonus = 1.0 + (upgrades.get("coffee_quality") * 0.1);
            double pastryBonus = 1.0 + (upgrades.get("pastry_variety") * 0.08);
            double satisfactionBonus = 1.0 + ((satisfaction - 50) * 0.01);

            int totalReward = (int)(baseReward * qualityBonus * pastryBonus * satisfactionBonus);
            money += totalReward;
            customers += 2;
            satisfaction = Math.min(100, satisfaction + 10);
        }
        updateCafeLevel();
    }

    private void updateCafeLevel() {
        int newLevel = (gamesWon / 5) + 1;
        cafeLevel = Math.max(cafeLevel, newLevel);
        maxCustomers = 8 + (cafeLevel * 2);
    }

    public boolean upgradeItem(String item) {
        int cost = upgradeCosts.get(item);
        if (money >= cost) {
            money -= cost;
            upgrades.put(item, upgrades.get(item) + 1);
            upgradeCosts.put(item, (int)(cost * 1.6));
            applyUpgradeEffects(item);
            return true;
        }
        return false;
    }

    private void applyUpgradeEffects(String item) {
        switch (item) {
            case "coffee_quality":
                satisfaction += 5;
                break;
            case "pastry_variety":
                customers += 1;
                break;
            case "service_speed":
                satisfaction += 3;
                break;
            case "decor":
                satisfaction += 8;
                break;
            case "marketing":
                customers += 2;
                break;
        }
        satisfaction = Math.min(100, satisfaction);
    }

    public void serveCustomer(Customer customer) {
        if (customersList.contains(customer) && customer.isReadyToOrder()) {
            customer.serve();
            satisfaction = Math.min(100, satisfaction + 5);
            int spending = customer.calculateSpending();
            money += spending;
            totalIncome += spending;

            // Adaugă un mesaj de feedback
            System.out.println("✅ Customer served! Earned: " + spending + " coins");
        }
    }

    public void debugCustomers() {
        System.out.println("=== CUSTOMER DEBUG ===");
        System.out.println("Total customers: " + customersList.size());
        for (int i = 0; i < customersList.size(); i++) {
            Customer c = customersList.get(i);
            System.out.println("Customer " + i + ": " + c.getType() +
                    ", State: " + c.getState() +
                    ", Ready to order: " + c.isReadyToOrder());
        }
        System.out.println("======================");
    }

    public int getHourlyIncome() {
        int baseIncome = customers * 5;
        int upgradeBonus = upgrades.values().stream().mapToInt(Integer::intValue).sum() * 3;
        int customerBonus = customersList.size() * 2;
        return baseIncome + upgradeBonus + customerBonus;
    }

    public int getMaxCustomers() {
        return 10 + (upgrades.get("marketing") * 3) + (cafeLevel * 2);
    }

    public int getCafeBonus() {
        return (upgrades.get("coffee_quality") * 10) +
                (upgrades.get("pastry_variety") * 8) +
                (satisfaction / 10);
    }

    public int getTotalValue() {
        return money + totalIncome + (cafeLevel * 1000);
    }

    public double getWinRate() {
        return gamesPlayed == 0 ? 0 : (double) gamesWon / gamesPlayed * 100;
    }

    public int getMoney() { return money; }
    public int getTotalIncome() { return totalIncome; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() { return gamesWon; }
    public int getCafeLevel() { return cafeLevel; }
    public int getCustomers() { return customers; }
    public int getSatisfaction() { return satisfaction; }
    public int getCustomersPerHour() { return customers * 6; }

    public List<Customer> getCustomersList() { return new ArrayList<>(customersList); }
    public int getCustomerCount() { return customersList.size(); }
    public int getAngryCustomerCount() {
        return (int) customersList.stream().filter(Customer::isAngry).count();
    }

    public Map<String, Integer> getUpgrades() { return new HashMap<>(upgrades); }
    public Map<String, Integer> getUpgradeCosts() { return new HashMap<>(upgradeCosts); }

    public boolean canAfford(int amount) { return money >= amount; }
    public void addMoney(int amount) { money += amount; }

    public int getCoffeeLevel() { return upgrades.get("coffee_quality"); }
    public int getPastryLevel() { return upgrades.get("pastry_variety"); }
    public int getServiceLevel() { return upgrades.get("service_speed"); }
    public int getDecorLevel() { return upgrades.get("decor"); }
    public int getMarketingLevel() { return upgrades.get("marketing"); }

    public void addCafeObject(CafeObject object) {
        cafeObjects.add(object);
    }

    public List<CafeObject> getCafeObjects() {
        return new ArrayList<>(cafeObjects);
    }

    public String getFormattedMoney() {
        if (money >= 1000000) {
            return String.format("%.1fM", money / 1000000.0);
        } else if (money >= 1000) {
            return String.format("%.1fK", money / 1000.0);
        }
        return String.valueOf(money);
    }
}