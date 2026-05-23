package main.logic.effects;

import main.model.Candy;
import main.model.enums.CandyType;
import main.model.enums.SpecialType;
import java.util.EnumMap;
import java.util.Map;

public class SpecialEffectRegistry {
    private static SpecialEffectRegistry instance;
    private final Map<SpecialType, ISpecialEffectStrategy> strategies = new EnumMap<>(SpecialType.class);

    private SpecialEffectRegistry() {
        strategies.put(SpecialType.STRIPED_HORIZONTAL, (board, candy, logic, listener) -> {
            int row = candy.getRow();
            board.setCandy(candy.getRow(), candy.getCol(), null);
            for (int c = 0; c < board.getCols(); c++) {
                Candy target = board.getCandy(row, c);
                if (target != null) {
                    board.setCandy(row, c, null);
                    listener.onSecondaryCandyDestroyed(target);
                }
            }
        });

        strategies.put(SpecialType.STRIPED_VERTICAL, (board, candy, logic, listener) -> {
            int col = candy.getCol();
            board.setCandy(candy.getRow(), candy.getCol(), null);
            for (int r = 0; r < board.getRows(); r++) {
                Candy target = board.getCandy(r, col);
                if (target != null) {
                    board.setCandy(r, col, null);
                    listener.onSecondaryCandyDestroyed(target);
                }
            }
        });

        strategies.put(SpecialType.WRAPPED, (board, candy, logic, listener) -> {
            int row = candy.getRow();
            int col = candy.getCol();
            board.setCandy(candy.getRow(), candy.getCol(), null);
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (board.isInside(r, c)) {
                        Candy target = board.getCandy(r, c);
                        if (target != null) {
                            board.setCandy(r, c, null);
                            listener.onSecondaryCandyDestroyed(target);
                        }
                    }
                }
            }
        });
        strategies.put(SpecialType.COLOR_BOMB, (board, candy, logic, listener) -> {
            // 1. Phải lấy màu/loại mục tiêu được CHỈ ĐỊNH (ví dụ lưu trong candy khi người chơi vuốt)
            // Giả sử bạn có hàm getTargetType() hoặc getTargetColor() được gán trước khi nổ
            CandyType targetType = candy.getType(); 
            if (targetType == null) return; 

            // 2. Xóa chính quả bom màu này trước để tránh việc hiệu ứng dây chuyền quét trúng lại nó
            board.setCandy(candy.getRow(), candy.getCol(), null);

            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    Candy target = board.getCandy(r, c);
                    
                    if (target != null && target.getType() == targetType && target != candy) {
                        
                        Candy specialTarget = target;
                        board.setCandy(r, c, null); // Xóa trên board trước
                        listener.onSecondaryCandyDestroyed(specialTarget);
                    }
                }
            }
        });
    }

    public static SpecialEffectRegistry getInstance() {
        if (instance == null) {
            instance = new SpecialEffectRegistry();
        }
        return instance;
    }

    public ISpecialEffectStrategy getStrategy(SpecialType type) {
        return strategies.get(type);
    }
    
    public void registerStrategy(SpecialType type, ISpecialEffectStrategy strategy) {
        strategies.put(type, strategy);
    }
}
