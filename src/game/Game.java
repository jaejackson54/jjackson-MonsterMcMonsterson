package game;

import java.util.ArrayList;

import gui.MonsterBattleGUI;

public class Game {

    // The window. You call its methods; you never need to open src/gui.
    private MonsterBattleGUI gui;

    // Game state: change these, then tell the gui.
    private ArrayList<Monster> monsters;
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int shield = 0;
    private int SHIELD_UP = 20;
    private int maxHealth;

    public static void main(String[] args) {
        // main is static, so it makes one Game object to play (Unit 3.7).
        Game game = new Game();
        game.play();
    }

    public void play() {
        setupGame();
        gameLoop();
    }

    // ACCESSORS
    public MonsterBattleGUI getGUI() {
        return gui;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("James' Monster Battle");

        // TODO: Difficulty Selection

        maxHealth = 100;
        playerHealth = 100;
        gui.setPlayerMaxHealth(maxHealth);
        gui.updatePlayerHealth(playerHealth);

        monsters = new ArrayList<>();
        monsters.add(new Monster(this));
        monsters.add(new Monster(this, "pfundstein"));
        monsters.add(new Monster(this, "Doc. T"));
        gui.updateMonsters(monsters);

        inventory = new ArrayList<>();
        addHealthPotion(20);
        addBomb(15);
        addBazooka(30);
        gui.updateInventory(inventory);

        String[] buttons = { "Attack", "Defend", "Heal", "Use Item" };
        gui.setActionButtons(buttons);

        // Welcome message
        gui.displayMessage("Battle Start! Choose your action.");
    }

    // Player turn, then monster turn, until one side is gone.
    private void gameLoop() {
        // Keep playing while monsters alive and player alive
        while (countLivingMonsters() > 0 && playerHealth > 0) {

            // PLAYER'S TURN
            gui.displayMessage("Your turn! HP: " + playerHealth);
            int action = gui.waitForAction(); // Wait for button click (0-3)
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);

            // MONSTER'S TURN (if any alive and player alive)
            if (countLivingMonsters() > 0 && playerHealth > 0) {
                monsterAttack();
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
        }

        // Game over!
        if (playerHealth <= 0) {
            gui.displayMessage("💀 DEFEAT! You have been defeated...");
        } else {
            gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
        }
    }

    // The four buttons, left to right, are 0 to 3.
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack button
                attackMonster();
                break;
            case 1: // Defend button
                defend();
                break;
            case 2: // Heal button
                heal();
                break;
            case 3: // Use Item button
                useItem();
                break;
        }
    }

    private void attackMonster() {
        Monster target = getRandomLivingMonster(); // get random every time?

        // determine my damage
        int damage = (int) (Math.random() * 15 + 10);
        // apply damage
        target.takeDamage(damage);
        // show message
        gui.displayMessage("Hit! You hit for " + damage + " points of damage!");
    }

    private void defend() {
        shield += SHIELD_UP;
        // EXTRA CREDIT: Show shield on GUI
        gui.displayMessage("SHIELD UP! Current shield: " + shield);
    }

    private void heal() {
        // random number 0-20
        int healNumber = (int) (Math.random() * 20 + 10);
        playerHealth += healNumber;
        if (playerHealth > maxHealth) {
            maxHealth = playerHealth;
            gui.setPlayerMaxHealth(maxHealth);
        }

        gui.updatePlayerHealth(playerHealth);
        gui.displayMessage("You healed for " + healNumber + " points!");
    }

    // Uses the first item in the inventory.
    private void useItem() {
        if (inventory.isEmpty()) {
            gui.displayMessage("No items in inventory!");
            return;
        }

        // Use first item
        Item item = inventory.remove(0);
        gui.updateInventory(inventory);
        item.use(); // The item knows what to do!
    }

    // ==================== ITEM CREATION HELPERS ====================
    
    /**
     * Add a health potion to inventory
     */
    private void addHealthPotion(int healAmount) {
        inventory.add(new Item("Health Potion", "🧪", () -> {
            playerHealth = Math.min(maxHealth, playerHealth + healAmount);
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("💚 Used Health Potion! Healed " + healAmount + " HP!");
        }));
    }
    
    /**
     * Add a bomb to inventory (damages all monsters)
     */
    private void addBomb(int damage) {
        inventory.add(new Item("Bomb", "💣", () -> {
            for (Monster m : monsters) {
                if (m.health() > 0) {
                    m.takeDamage(damage);
                }
            }
            gui.displayMessage("💣 BOOM! All monsters take " + damage + " damage!");
            gui.updateMonsters(monsters);
        }));

    }

    private void addBazooka(int damage) {
        inventory.add(new Item("Bazooka", "💣", () -> {
            for (Monster m : monsters) {
                if (m.health() > 0) {
                    m.takeDamage(damage * 2);
                }
            }
            gui.displayMessage("💣 BOOM! All monsters take " + damage + " damage!");
            gui.updateMonsters(monsters);
        }));

    }

    private void monsterAttack() {
        Monster monster = getRandomLivingMonster(); // TODO: Be smarter. Only one monster gets a turn?
        double incomingDamage = monster.damage();
        if (shield > 0) {
            // reduce incoming dmg by shield
            if (shield > incomingDamage) {
                shield -= incomingDamage;
                incomingDamage = 0;
                gui.displayMessage("Blocked it all!");

            } else {
                incomingDamage -= shield;
                shield = 0;
                gui.displayMessage("Owie!");
            }
        }
        playerHealth -= monster.damage();
        gui.displayMessage("Monster hits for " + monster.damage()); // you're ignoring the calculations you just made
        gui.updatePlayerHealth(playerHealth);
    }

    // Helpers. Add your own below these two.
    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters) {
            if (m.health() > 0)
                count++;
        }
        return count;
    }

    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.health() > 0)
                alive.add(m);
        }
        if (alive.isEmpty())
            return null;
        return alive.get((int) (Math.random() * alive.size()));
    }
}