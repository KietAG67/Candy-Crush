package main.logic;

public interface GameStateListener {
    // Kích hoạt bất cứ khi nào bàn cờ có sự thay đổi (đổi chỗ, nổ kẹo, kẹo rơi)
    void onGridChanged();
}