package main.logic;

import main.model.Board;
import main.model.Candy;

public interface ISpecialCandyLogic {
    void activateSpecialCandy(Board board, Candy candy, ICandyDestroyListener listener);
}
