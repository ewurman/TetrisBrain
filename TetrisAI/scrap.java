    public JBrainNoGraphics(int w, int h, double maxHeight, double holes, double roughness, double blockades) {
        super(w, h);
        brainActive = true;
        previousCount = count;
        brains = new ErikWurmanSinaBakhtiariBrain(maxHeight, holes, roughness, blockades);
        gamesPlayed = 0;
        drop = true;
        myOpponent = this; //new Sith(false);
    }




37.40295238810659
23.40689619244571
0.0
5.263249750947891