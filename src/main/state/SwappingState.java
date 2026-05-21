package main.state;

import main.GameManager;
import main.model.Candy;
import main.model.MatchResult;
import main.model.enums.SpecialType;
import main.animation.SwapAnimation;
import java.util.List;

public class SwappingState implements IGameState {
    @Override
    public void update(GameManager manager) {
        // Mẹo: Đợi animation hệ thống chạy xong hoàn toàn rồi mới xử lý logic kết quả swap
        if (manager.getAnimationSystem().isAnimating()) {
            return; 
        }

        // 1. Lấy thông tin 2 viên kẹo vừa được swap
        Candy c1 = manager.getBoard().getCandy(manager.getSwapR1(), manager.getSwapC1());
        Candy c2 = manager.getBoard().getCandy(manager.getSwapR2(), manager.getSwapC2());

        // Đề phòng trường hợp kẹo bị null
        if (c1 == null || c2 == null) {
            manager.setState(manager.getIdleState());
            return;
        }

        // 2. KIỂM TRA TRƯỜNG HỢP SWAP CÓ BOM MÀU (COLOR_BOMB)
        boolean isC1Bomb = c1.getSpecialType() == SpecialType.COLOR_BOMB;
        boolean isC2Bomb = c2.getSpecialType() == SpecialType.COLOR_BOMB;

        if (isC1Bomb || isC2Bomb) {
            // Xác định viên nào là bom, viên nào là kẹo mục tiêu để lấy màu
            Candy bombCandy = isC1Bomb ? c1 : c2;
            Candy targetCandy = isC1Bomb ? c2 : c1;

            // Thiết lập loại màu mục tiêu cho quả bom (để Registry nhận diện giống như thảo luận trước)
            bombCandy.setType(targetCandy.getType());

            // Kích hoạt nổ quả bom màu ngay lập tức!
            manager.getSpecialCandyLogic().activateSpecialCandy(manager.getBoard(), bombCandy);

            // Chuyển sang FallingState và gọi refill để rơi kẹo xuống lấp chỗ trống
            manager.refillAndAnimate();
            manager.setState(manager.getFallingState());
            return;
        }

        // 3. TRƯỜNG HỢP SWAP KẸO THƯỜNG (Logic Match 3 cũ của bạn)
        List<MatchResult> matches = manager.getMatchLogic().findMatches(manager.getBoard());
        
        if (matches.isEmpty()) {
            // Vì GameManager.handleSwap() ĐÃ SWAP RỒI, nên khi hụt ta mới swap NGƯỢC LẠI ở đây
            manager.getBoard().swap(manager.getSwapR1(), manager.getSwapC1(), manager.getSwapR2(), manager.getSwapC2());
            
            // Lấy lại đối tượng sau khi swap ngược để làm hiệu ứng trả về vị trí cũ
            Candy revertedC1 = manager.getBoard().getCandy(manager.getSwapR1(), manager.getSwapC1());
            Candy revertedC2 = manager.getBoard().getCandy(manager.getSwapR2(), manager.getSwapC2());
            
            manager.getAnimationSystem().addAnimation(new SwapAnimation(revertedC1, revertedC2, 0.2f));
            manager.setState(manager.getRevertingState());
        } else {
            // Có match hợp lệ, chuyển sang trạng thái nổ kẹo kiếm điểm
            manager.setState(manager.getMatchingState());
        }
    }
}