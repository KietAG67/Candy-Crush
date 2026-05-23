package main.logic;

import main.model.Board;
import main.model.Candy;
import main.logic.effects.SpecialEffectRegistry;
import main.logic.effects.ISpecialEffectStrategy;

public class BasicSpecialCandyLogic implements ISpecialCandyLogic {

    @Override
    public void activateSpecialCandy(Board board, Candy candy, ICandyDestroyListener listener) {
        ISpecialEffectStrategy strategy = SpecialEffectRegistry.getInstance().getStrategy(candy.getSpecialType());
        if (strategy != null) {
            strategy.execute(board, candy, this, listener);
        }
    }
}
