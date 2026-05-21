package main.logic.effects;

import main.model.Board;
import main.model.Candy;
import main.model.enums.CandyType;
import main.model.enums.SpecialType;
import main.logic.ISpecialCandyLogic;
import java.util.EnumMap;
import java.util.Map;

public class SpecialEffectRegistry {
    private static SpecialEffectRegistry instance;
    private final Map<SpecialType, ISpecialEffectStrategy> strategies = new EnumMap<>(SpecialType.class);

    private SpecialEffectRegistry() {
        strategies.put(SpecialType.STRIPED_HORIZONTAL, (board, candy, logic) -> {
            int row = candy.getRow();
            board.setCandy(candy.getRow(), candy.getCol(), null);
            for (int c = 0; c < board.getCols(); c++) {
                Candy target = board.getCandy(row, c);
                if (target != null) {
                    board.setCandy(row, c, null);
                    if (target.getSpecialType() != SpecialType.NONE && target != candy) {
                        logic.activateSpecialCandy(board, target);
                    }
                }
            }
        });

        strategies.put(SpecialType.STRIPED_VERTICAL, (board, candy, logic) -> {
            int col = candy.getCol();
            board.setCandy(candy.getRow(), candy.getCol(), null);
            for (int r = 0; r < board.getRows(); r++) {
                Candy target = board.getCandy(r, col);
                if (target != null) {
                    board.setCandy(r, col, null);
                    if (target.getSpecialType() != SpecialType.NONE && target != candy) {
                        logic.activateSpecialCandy(board, target);
                    }
                }
            }
        });

        strategies.put(SpecialType.WRAPPED, (board, candy, logic) -> {
            int row = candy.getRow();
            int col = candy.getCol();
            board.setCandy(candy.getRow(), candy.getCol(), null);
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (board.isInside(r, c)) {
                        Candy target = board.getCandy(r, c);
                        if (target != null) {
                            board.setCandy(r, c, null);
                            if (target.getSpecialType() != SpecialType.NONE && target != candy) {
                                logic.activateSpecialCandy(board, target);
                            }
                        }
                    }
                }
            }
        });
        strategies.put(SpecialType.COLOR_BOMB, (board, candy, logic) -> {
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
                        
                        // 3. Nếu là kẹo đặc biệt, kích hoạt logic của nó
                        if (target.getSpecialType() != SpecialType.NONE) {
                            // Mẹo: Cần xóa hoặc đánh dấu target ĐÃ NỔ trước khi gọi đệ quy 
                            // để tránh các viên kẹo đặc biệt cùng loại gọi chéo nhau.
                            Candy specialTarget = target;
                            board.setCandy(r, c, null); // Xóa trên board trước
                            logic.activateSpecialCandy(board, specialTarget); // Rồi mới kích hoạt hiệu ứng
                        } else {
                            // Nếu là kẹo thường thì chỉ cần xóa
                            board.setCandy(r, c, null);
                        }
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
