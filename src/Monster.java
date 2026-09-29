public class Monster {
    public static int specialMonsters = 1; 
    
    // CONSTRUCTOR - Monster IS the return type
    public Monster() {
        if(Monster.specialMonsters > 0) {
            System.out.println("I'm so special!!");
            Monster.specialMonsters--; 
        }
        else System.out.println("I'm just a regular monster");
    }

}
