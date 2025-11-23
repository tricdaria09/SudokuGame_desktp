package com.sudokugame.cafe;

import com.sudokugame.utils.AssetsLoader;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Random;

public class Customer {
    private int x, y;
    private int targetX, targetY;
    private int width, height;
    private int speed;
    private CustomerState state;
    private int patience;
    private int maxPatience;
    private int satisfaction;
    private String type;
    private Random random;

    // 🎯 TIPURI DE CLIENTI
    public static final String[] CUSTOMER_TYPES = {
            "business", "student", "tourist", "regular"
    };

    // 🎯 STĂRILE CLIENTULUI
    public enum CustomerState {
        ENTERING,      // Intra în cafenea
        WAITING,       // Așteaptă la coadă
        ORDERING,      // Plasează comanda
        EATING,        // Mănâncă/bea
        LEAVING,       // Pleacă
        ANGRY          // Pleacă nemulțumit
    }

    public Customer(int startX, int startY) {
        this.random = new Random();
        this.x = startX;
        this.y = startY;
        this.width = 60;
        this.height = 90;
        this.speed = 2 + random.nextInt(3); // Viteză între 2-4
        this.state = CustomerState.ENTERING;
        this.type = CUSTOMER_TYPES[random.nextInt(CUSTOMER_TYPES.length)];
        this.maxPatience = calculateMaxPatience();
        this.patience = maxPatience;
        this.satisfaction = 100;

        // 🎯 SETEAZĂ DESTINAȚIA INIȚIALĂ (intrare cafenea)
        this.targetX = 300 + random.nextInt(200);
        this.targetY = 250 + random.nextInt(100);
    }

    private int calculateMaxPatience() {
        switch (type) {
            case "business": return 120; // Mai puțin răbdare
            case "student": return 180;  // Multă răbdare
            case "tourist": return 150;  // Răbdare medie
            case "regular": return 200;  // Foarte multă răbdare
            default: return 150;
        }
    }

    public void update() {
        switch (state) {
            case ENTERING:
                moveToTarget();
                if (hasReachedTarget()) {
                    // 🎯 AJUNS LA DESTINAȚIE - ÎNCEPE SĂ AȘTEPTE
                    state = CustomerState.WAITING;
                    setRandomWaitTarget();
                }
                break;

            case WAITING:
                patience--;
                satisfaction = (int)((double)patience / maxPatience * 100);

                // 🎯 CLIENTUL SE ENERVEAZĂ DACA AȘTEAPTĂ PREA MULT
                if (patience <= 0) {
                    state = CustomerState.ANGRY;
                    setExitTarget();
                } else if (patience < maxPatience / 3) {
                    // Schimbă poziția dacă așteaptă prea mult
                    if (random.nextInt(100) < 5) {
                        setRandomWaitTarget();
                    }
                }
                break;

            case ORDERING:
                // Simulează timpul de plasare a comenzii
                patience--;
                if (patience <= maxPatience / 2) {
                    state = CustomerState.EATING;
                    setEatingTarget();
                }
                break;

            case EATING:
                // 🍩 MĂNÂNCĂ/BEA
                patience--;
                if (patience <= 0) {
                    state = CustomerState.LEAVING;
                    setExitTarget();
                }
                break;

            case LEAVING:
            case ANGRY:
                moveToTarget();
                if (x < -100 || x > 1000 || y < -100 || y > 800) {
                    // 🚪 CLIENTUL A PLECAT
                    state = null; // Marchează pentru eliminare
                }
                break;
        }
    }

    public void draw(Graphics2D g2d) {
        // 🖼️ DESENEAZĂ CLIENTUL CU IMAGINE
        Image customerImage = AssetsLoader.getImage("customer");
        if (customerImage != null) {
            g2d.drawImage(customerImage, x, y, width, height, (ImageObserver) null);
        } else {
            // 🎨 FALLBACK LA FORME GEOMETRICE
            drawFallbackCustomer(g2d);
        }

        // 📊 BARĂ DE RĂBDARE
        drawPatienceBar(g2d);

        // 😊 EMOJI BASED ON STATE
        drawEmoji(g2d);
    }

    private void drawFallbackCustomer(Graphics2D g2d) {
        // 🎨 CULOARE BASED ON TYPE
        Color bodyColor = getCustomerColor();
        g2d.setColor(bodyColor);
        g2d.fillRect(x, y, width, height - 20); // Corp

        // 👤 CAP
        g2d.setColor(new Color(255, 220, 177)); // Piele
        g2d.fillOval(x + 15, y - 10, 30, 30);

        // STATE INDICATOR
        g2d.setColor(getStateColor());
        g2d.fillRect(x, y + height - 15, width, 5);
    }

    private void drawPatienceBar(Graphics2D g2d) {
        int barWidth = 50;
        int barHeight = 6;
        int barX = x + (width - barWidth) / 2;
        int barY = y - 15;

        // 🎯 FUNDAL BARĂ
        g2d.setColor(new Color(100, 100, 100, 200));
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // 🎯 BARĂ DE RĂBDARE
        double patienceRatio = (double) patience / maxPatience;
        Color barColor = getPatienceColor(patienceRatio);
        g2d.setColor(barColor);
        g2d.fillRect(barX, barY, (int)(barWidth * patienceRatio), barHeight);

        // 🎯 BORDER
        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }

    private void drawEmoji(Graphics2D g2d) {
        String emoji = getStateEmoji();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        g2d.drawString(emoji, x + width/2 - 8, y - 5);
    }

    private Color getCustomerColor() {
        switch (type) {
            case "business": return new Color(100, 100, 200); // Albastru
            case "student": return new Color(200, 100, 100);  // Roșu
            case "tourist": return new Color(100, 200, 100);  // Verde
            case "regular": return new Color(200, 200, 100);  // Galben
            default: return Color.GRAY;
        }
    }

    private Color getStateColor() {
        switch (state) {
            case ENTERING: return Color.BLUE;
            case WAITING: return Color.ORANGE;
            case ORDERING: return Color.CYAN;
            case EATING: return Color.GREEN;
            case LEAVING: return Color.GRAY;
            case ANGRY: return Color.RED;
            default: return Color.BLACK;
        }
    }

    private Color getPatienceColor(double ratio) {
        if (ratio > 0.7) return Color.GREEN;
        if (ratio > 0.4) return Color.YELLOW;
        if (ratio > 0.2) return Color.ORANGE;
        return Color.RED;
    }

    private String getStateEmoji() {
        switch (state) {
            case ENTERING: return "🚶";
            case WAITING: return "⏳";
            case ORDERING: return "📝";
            case EATING: return "☕";
            case LEAVING: return "👋";
            case ANGRY: return "😠";
            default: return "❓";
        }
    }

    private void moveToTarget() {
        // 🎯 MISCARE SPRE TINTĂ
        int dx = targetX - x;
        int dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > speed) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        } else {
            x = targetX;
            y = targetY;
        }
    }

    private boolean hasReachedTarget() {
        return Math.abs(x - targetX) < speed && Math.abs(y - targetY) < speed;
    }

    private void setRandomWaitTarget() {
        // 🎯 POZIȚIE ALEATOARE PENTRU AȘTEPTARE
        targetX = 250 + random.nextInt(300);
        targetY = 200 + random.nextInt(200);
    }

    private void setEatingTarget() {
        // 🎯 MERGE LA MASĂ
        targetX = 200 + random.nextInt(400);
        targetY = 400 + random.nextInt(100);
    }

    private void setExitTarget() {
        // 🚪 MERGE SPRE IEȘIRE
        targetX = -100;
        targetY = 300 + random.nextInt(200);
    }

    public void startOrdering() {
        if (state == CustomerState.WAITING) {
            state = CustomerState.ORDERING;
            // Reset patience for ordering phase
            patience = maxPatience / 2;
        }
    }

    public void serve() {
        if (state == CustomerState.ORDERING) {
            state = CustomerState.EATING;
            setEatingTarget();
            // Bonus satisfaction pentru servire rapidă
            satisfaction = Math.min(100, satisfaction + 20);
        }
    }

    // 🎯 GETTERS
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public CustomerState getState() { return state; }
    public String getType() { return type; }
    public int getSatisfaction() { return satisfaction; }
    public int getPatience() { return patience; }
    public int getMaxPatience() { return maxPatience; }

    public boolean shouldRemove() {
        return state == null;
    }

    public boolean isReadyToOrder() {
        return state == CustomerState.WAITING && patience > maxPatience / 2;
    }

    public boolean isAngry() {
        return state == CustomerState.ANGRY;
    }

    public int calculateSpending() {
        // 💰 CAT CHELTUIE CLIENTUL
        int baseSpending = 0;
        switch (type) {
            case "business": baseSpending = 15; break;
            case "student": baseSpending = 8; break;
            case "tourist": baseSpending = 12; break;
            case "regular": baseSpending = 10; break;
        }

        // 🎯 BONUS SATISFACTIE
        double satisfactionBonus = satisfaction / 100.0;
        return (int)(baseSpending * (1.0 + satisfactionBonus * 0.5));
    }
}