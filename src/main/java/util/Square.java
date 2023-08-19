package main.java.util;

public class Square {
    public int col;
    public int row;

    public Square(int col, int row){
        this.col = col;
        this.row = row;
    }

    public Square displace(Square displacement){
        return new Square(this.col + displacement.col,this.row + displacement.row);


    }

    public boolean equals(Square other){
        return this.col == other.col && this.row == other.row;
    }

    @Override
    public String toString(){
        return "[" + this.col + " , " + this.row + "]";
    }
}