public class Monster {
    // INSTANCE VARS
    private final int health; 
    private int maxDmg; 

    // CONSTRUCTOR - Monster IS the return type
    public Monster() {
        health = 100; 
        // 10 - 25 as max dmg
        maxDmg = (int)(Math.random() * 15 + 1) + 10; 
    }

    // ACCESORS
    public int health(){ return health; }
    public int maxDmg(){ return maxDmg; }

    // MUTATORS
    public void takeDmg(int change){
        health -= dmg; 
    }

}
