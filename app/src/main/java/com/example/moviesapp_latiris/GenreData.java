package com.example.moviesapp_latiris;

public class GenreData {
    private int id;
    private String name;
    private boolean isSelected;

    public GenreData(int id, String name) {
        this.id = id;
        this.name = name;
        this.isSelected = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
