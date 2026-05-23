package main.ui;

import main.logic.SelectionController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputHandler extends MouseAdapter {
    private final SelectionController selectionController;
    private final GamePanel gamePanel;

    public InputHandler(SelectionController selectionController, GamePanel gamePanel) {
        this.selectionController = selectionController;
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gamePanel.isPlaying()) {
            gamePanel.handleMouseClick(e.getX(), e.getY());
            return;
        }

        if (!gamePanel.isInsideBoard(e.getX(), e.getY())) {
            return;
        }

        int col = gamePanel.getBoardColFromMouse(e.getX());
        int row = gamePanel.getBoardRowFromMouse(e.getY());

        int selectedRowBefore = selectionController.getSelectedRow();
        int selectedColBefore = selectionController.getSelectedCol();

        boolean isRealSwap = selectedRowBefore != -1
                && selectedColBefore != -1
                && !(selectedRowBefore == row && selectedColBefore == col)
                && Math.abs(selectedRowBefore - row) + Math.abs(selectedColBefore - col) == 1;

        selectionController.selectCandy(row, col);

        if (isRealSwap) {
            gamePanel.consumeMoveAfterSwap();
        }
    }
}
