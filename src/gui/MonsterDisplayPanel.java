package gui;

import javax.swing.*;
import game.Monster;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

/**
 * MonsterDisplayPanel - Displays monster tiles in a responsive grid
 * 
 * Styled with a polished Crimson / Scarlet theme.
 */
public class MonsterDisplayPanel extends JPanel {
    
    private ArrayList<Monster> monsters;
    private ArrayList<MonsterTile> tiles;
    private int highlightedIndex = -1;
    
    // Theme Palette: Dark Crimson & Scarlet
    private static final Color PANEL_BG = new Color(26, 11, 14);         // Deep dark crimson body background
    private static final Color BORDER_SCARLET = new Color(220, 20, 60);    // Bright crimson border
    private static final int TILE_SPACING = 12;

    /**
     * Constructor
     */
    public MonsterDisplayPanel() {
        monsters = new ArrayList<>();
        tiles = new ArrayList<>();
        
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_SCARLET, 2),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                "MONSTERS",
                0, 0, new Font("SansSerif", Font.BOLD, 16), BORDER_SCARLET
            )
        ));
    }
    
    /**
     * Set the monsters to display
     * @param monsters The list of monsters
     */
    public void setMonsters(ArrayList<Monster> monsters) {
        this.monsters = monsters;
        this.highlightedIndex = -1;
        createTiles();
    }
    
    /**
     * Highlight a specific monster
     * @param index The index to highlight (-1 for none)
     */
    public void highlightMonster(int index) {
        this.highlightedIndex = index;
        repaint();
    }
    
    /**
     * Create tiles for all monsters
     */
    private void createTiles() {
        removeAll();
        tiles.clear();
        
        if (monsters == null || monsters.isEmpty()) {
            JLabel emptyLabel = new JLabel("No monsters to display");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(180, 150, 150));
            add(emptyLabel);
            revalidate();
            repaint();
            return;
        }
        
        // Determine grid layout
        int monsterCount = monsters.size();
        int cols = (int) Math.ceil(Math.sqrt(monsterCount));
        int rows = (int) Math.ceil((double) monsterCount / cols);
        
        setLayout(new GridLayout(rows, cols, TILE_SPACING, TILE_SPACING));
        
        // Create a tile for each monster
        for (int i = 0; i < monsters.size(); i++) {
            MonsterTile tile = new MonsterTile(monsters.get(i), i);
            tiles.add(tile);
            add(tile);
        }
        
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Update highlighting
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setHighlighted(i == highlightedIndex);
        }
    }
    
    /**
     * MonsterTile - Individual monster display card
     */
    private class MonsterTile extends JPanel {
        private Monster monster;
        private int index;
        private boolean highlighted = false;
        
        // Tile Colors
        private static final Color CARD_BG_TOP = new Color(50, 20, 26);
        private static final Color CARD_BG_BOTTOM = new Color(35, 12, 16);
        private static final Color DEAD_CARD_BG = new Color(20, 8, 10);
        
        private static final Color NORMAL_BORDER = new Color(130, 35, 45);
        private static final Color HIGHLIGHT_BORDER = new Color(255, 215, 0); // Gold accent highlight
        private static final Color DEAD_BORDER = new Color(70, 25, 30);
        
        private static final Color STAT_DMG = new Color(255, 120, 80);
        private static final Color STAT_SPD = new Color(255, 180, 100);
        private static final Color SPECIAL_ACCENT = new Color(255, 215, 0);
        
        public MonsterTile(Monster monster, int index) {
            this.monster = monster;
            this.index = index;
            setLayout(new BorderLayout(8, 8));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            createLayout();
        }
        
        private void createLayout() {
            // Monster info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            
            // Monster number/name
            JLabel numberLabel = new JLabel("Monster #" + index);
            numberLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            numberLabel.setForeground(monster.health() > 0 ? Color.WHITE : new Color(140, 110, 110));
            numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Stats
            JLabel healthLabel = new JLabel("HP: " + monster.health());
            healthLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            healthLabel.setForeground(new Color(240, 220, 220));
            healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel damageLabel = new JLabel("DMG: " + monster.damage());
            damageLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            damageLabel.setForeground(STAT_DMG);
            damageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel speedLabel = new JLabel("SPD: " + monster.speed());
            speedLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            speedLabel.setForeground(STAT_SPD);
            speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            infoPanel.add(numberLabel);
            infoPanel.add(Box.createVerticalStrut(6));
            
            // Special ability if exists
            String special = monster.special();
            if (special != null && !special.isEmpty()) {
                Font specialFont = new Font("SansSerif", Font.ITALIC, 11);
                JLabel specialLabel = new JLabel(Emoji.fit("★ " + special, specialFont));
                specialLabel.setForeground(SPECIAL_ACCENT);
                specialLabel.setFont(specialFont);
                specialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                infoPanel.add(specialLabel);
                infoPanel.add(Box.createVerticalStrut(4));
            }
            
            infoPanel.add(healthLabel);
            infoPanel.add(damageLabel);
            infoPanel.add(speedLabel);
            
            add(infoPanel, BorderLayout.CENTER);
            
            // Health bar at bottom
            add(new HealthBar(), BorderLayout.SOUTH);
        }
        
        public void setHighlighted(boolean highlighted) {
            if (this.highlighted != highlighted) {
                this.highlighted = highlighted;
                repaint();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int arc = 12;

            // Gradient background fill
            if (monster.health() <= 0) {
                g2.setColor(DEAD_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
            } else {
                GradientPaint bgGradient = new GradientPaint(0, 0, CARD_BG_TOP, 0, h, CARD_BG_BOTTOM);
                g2.setPaint(bgGradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
            }
            
            // Border styling
            if (highlighted) {
                g2.setColor(HIGHLIGHT_BORDER);
                g2.setStroke(new BasicStroke(3f));
            } else if (monster.health() <= 0) {
                g2.setColor(DEAD_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(NORMAL_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
            }
            
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 3, h - 3, arc, arc));
            g2.dispose();
            
            super.paintComponent(g);
        }
        
        /**
         * Visual health bar
         */
        private class HealthBar extends JPanel {
            private static final int BAR_HEIGHT = 16;
            
            private static final Color HEALTH_FULL = new Color(230, 40, 50);   // Scarlet / Vibrant Red
            private static final Color HEALTH_MED  = new Color(220, 120, 30);  // Amber
            private static final Color HEALTH_LOW  = new Color(140, 20, 30);   // Dark Crimson
            private static final Color HEALTH_BG   = new Color(15, 5, 8);
            
            public HealthBar() {
                setPreferredSize(new Dimension(0, BAR_HEIGHT));
                setOpaque(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int arc = 6;
                
                // Track Background
                g2.setColor(HEALTH_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, width, height, arc, arc));
                
                // Calculate health percentage (assume max 100)
                double healthPercent = Math.max(0, Math.min(100, monster.health())) / 100.0;
                int barWidth = (int) (width * healthPercent);
                
                if (barWidth > 0) {
                    Color barColor;
                    if (healthPercent > 0.5) {
                        barColor = HEALTH_FULL;
                    } else if (healthPercent > 0.25) {
                        barColor = HEALTH_MED;
                    } else {
                        barColor = HEALTH_LOW;
                    }
                    
                    g2.setColor(barColor);
                    g2.fill(new RoundRectangle2D.Float(0, 0, barWidth, height, arc, arc));
                }
                
                // Track Border
                g2.setColor(new Color(90, 25, 35));
                g2.draw(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arc, arc));
                
                g2.dispose();
            }
        }
    }
}