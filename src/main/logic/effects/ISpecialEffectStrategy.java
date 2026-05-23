package main.logic.effects;

import main.model.Board;
import main.model.Candy;
import main.logic.ISpecialCandyLogic;
import main.logic.ICandyDestroyListener;

public interface ISpecialEffectStrategy {
    void execute(Board board, Candy candy, ISpecialCandyLogic logic, ICandyDestroyListener listener);
}
