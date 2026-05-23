package main.state;

import main.GameManager;
import main.model.Board;
import main.model.Candy;
import main.model.LevelManager;
import main.model.ScoreManager;
import main.model.enums.SpecialType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SugarCrushState implements IGameState {
    private long lastTime = 0;

    @Override
    public void update(GameManager manager) {
        if (System.currentTimeMillis() - lastTime < 400) {
            return;
        }
        lastTime = System.currentTimeMillis();

        LevelManager lm = LevelManager.getInstance();
        if (lm.getMovesLeft() > 0) {
            lm.decrementMove();

            Board board = manager.getBoard();
            List<Candy> normalCandies = new ArrayList<>();
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    Candy candy = board.getCandy(r, c);
                    if (candy != null && candy.getSpecialType() == SpecialType.NONE) {
                        normalCandies.add(candy);
                    }
                }
            }

            if (!normalCandies.isEmpty()) {
                Random rand = new Random();
                Candy randomCandy = normalCandies.get(rand.nextInt(normalCandies.size()));
                
                SpecialType newType = rand.nextBoolean() ? SpecialType.STRIPED_HORIZONTAL : SpecialType.STRIPED_VERTICAL;
                randomCandy.setSpecialType(newType);
                
                ScoreManager.getInstance().addScore(10);
                
                manager.getSpecialCandyLogic().activateSpecialCandy(board, randomCandy, manager);
                board.setCandy(randomCandy.getRow(), randomCandy.getCol(), null);
                
                manager.refillAndAnimate();
                manager.setState(manager.getFallingState());
            }
        } else {
            manager.setState(manager.getIdleState());
        }
    }
}
