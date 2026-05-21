package main.logic;

import main.model.Board;
import main.model.Candy;
import main.model.CandyFactory;
import main.model.MatchResult;
import main.model.enums.MatchType;
import main.model.enums.SpecialType;

import java.util.*;

public class BasicMatchLogic implements IMatchLogic {

    private final CandyFactory candyFactory =
            CandyFactory.getInstance();

    @Override
    public List<MatchResult> findMatches(Board board) {

        int rows = board.getRows();
        int cols = board.getCols();

        // Cắm cờ các ô bị match
        boolean[][] matched = new boolean[rows][cols];

        // =========================
        // 1. Scan Horizontal
        // =========================
        for (int r = 0; r < rows; r++) {

            int c = 0;

            while (c < cols - 2) {

                Candy current = board.getCandy(r, c);

                if (current == null || current.getSpecialType() == SpecialType.COLOR_BOMB) {
                    c++;
                    continue;
                }

                int length = 1;

                while (c + length < cols &&
                        board.getCandy(r, c + length) != null &&
                        board.getCandy(r, c + length).getType() == current.getType() &&
                        board.getCandy(r, c + length).getSpecialType() != SpecialType.COLOR_BOMB) {

                    length++;
                }

                if (length >= 3) {
                    for (int i = 0; i < length; i++) {
                        matched[r][c + i] = true;
                    }
                }

                c += length;
            }
        }

        // =========================
        // 2. Scan Vertical
        // =========================
        for (int c = 0; c < cols; c++) {

            int r = 0;

            while (r < rows - 2) {

                Candy current = board.getCandy(r, c);

                if (current == null || current.getSpecialType() == SpecialType.COLOR_BOMB) {
                    r++;
                    continue;
                }

                int length = 1;

                while (r + length < rows &&
                        board.getCandy(r + length, c) != null &&
                        board.getCandy(r + length, c).getType() == current.getType() &&
                        board.getCandy(r + length, c).getSpecialType() != SpecialType.COLOR_BOMB) {

                    length++;
                }

                if (length >= 3) {
                    for (int i = 0; i < length; i++) {
                        matched[r + i][c] = true;
                    }
                }

                r += length;
            }
        }

        // =========================
        // 3. Build Clusters (BFS)
        // =========================
        boolean[][] visited = new boolean[rows][cols];

        List<MatchResult> results = new ArrayList<>();

        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < cols; c++) {

                if (!matched[r][c] || visited[r][c]) {
                    continue;
                }

                Candy start = board.getCandy(r, c);

                if (start == null) {
                    continue;
                }

                List<Candy> cluster = new ArrayList<>();

                Queue<Candy> queue = new LinkedList<>();

                queue.add(start);

                visited[r][c] = true;

                while (!queue.isEmpty()) {

                    Candy current = queue.poll();

                    cluster.add(current);

                    for (int[] dir : directions) {

                        int nr = current.getRow() + dir[0];
                        int nc = current.getCol() + dir[1];

                        if (nr < 0 || nr >= rows ||
                                nc < 0 || nc >= cols) {
                            continue;
                        }

                        if (visited[nr][nc]) {
                            continue;
                        }

                        if (!matched[nr][nc]) {
                            continue;
                        }

                        Candy neighbor =
                                board.getCandy(nr, nc);

                        if (neighbor == null) {
                            continue;
                        }

                        // Chỉ nối cluster cùng màu
                        if (neighbor.getType()
                                != current.getType()) {
                            continue;
                        }

                        visited[nr][nc] = true;

                        queue.add(neighbor);
                    }
                }

                MatchResult result =
                        analyzeCluster(cluster);

                results.add(result);
            }
        }

        return results;
    }

    private MatchResult analyzeCluster(List<Candy> cluster) {

        MatchType matchType = MatchType.THREE;

        Candy spawned = null;

        Set<Integer> rows = new HashSet<>();
        Set<Integer> cols = new HashSet<>();

        for (Candy candy : cluster) {

            rows.add(candy.getRow());
            cols.add(candy.getCol());
        }

        // =========================
        // 5 line (COLOR_BOMB)
        // =========================
        if (cols.size() >= 5 || rows.size() >= 5) {
            matchType = MatchType.FIVE;
            
            // Tìm viên ở giữa của hàng hoặc cột 5
            Candy center = cluster.get(0);
            if (cols.size() >= 5) {
                // Find middle column
                int minCol = Integer.MAX_VALUE;
                for (int c : cols) if (c < minCol) minCol = c;
                int midCol = minCol + 2;
                for (Candy candy : cluster) {
                    if (candy.getCol() == midCol) {
                        center = candy;
                        break;
                    }
                }
            } else {
                // Find middle row
                int minRow = Integer.MAX_VALUE;
                for (int r : rows) if (r < minRow) minRow = r;
                int midRow = minRow + 2;
                for (Candy candy : cluster) {
                    if (candy.getRow() == midRow) {
                        center = candy;
                        break;
                    }
                }
            }

            spawned = candyFactory.createSpecialCandy(
                    center.getType(),
                    SpecialType.COLOR_BOMB,
                    center.getRow(),
                    center.getCol()
            );
        }
        // =========================
        // T hoặc L Shape (WRAPPED)
        // =========================
        else if (cluster.size() >= 5 &&
                rows.size() >= 2 &&
                cols.size() >= 2) {

            matchType = MatchType.FIVE;

            Candy center =
                    findIntersectionCandy(cluster);

            spawned =
                    candyFactory.createSpecialCandy(
                            center.getType(),
                            SpecialType.WRAPPED,
                            center.getRow(),
                            center.getCol()
                    );
        }

        // =========================
        // 4 horizontal
        // =========================
        else if (cluster.size() == 4 &&
                rows.size() == 1) {

            matchType = MatchType.FOUR_HORIZONTAL;

            Candy center =
                    cluster.get(1);

            spawned =
                    candyFactory.createSpecialCandy(
                            center.getType(),
                            SpecialType.STRIPED_VERTICAL,
                            center.getRow(),
                            center.getCol()
                    );
        }

        // =========================
        // 4 vertical
        // =========================
        else if (cluster.size() == 4 &&
                cols.size() == 1) {

            matchType = MatchType.FOUR_VERTICAL;

            Candy center =
                    cluster.get(1);

            spawned =
                    candyFactory.createSpecialCandy(
                            center.getType(),
                            SpecialType.STRIPED_HORIZONTAL,
                            center.getRow(),
                            center.getCol()
                    );
        }

        return new MatchResult(
                cluster,
                matchType,
                spawned
        );
    }

    // Tìm viên giao nhau cho T/L
    private Candy findIntersectionCandy(
            List<Candy> cluster) {

        for (Candy candy : cluster) {

            int sameRow = 0;
            int sameCol = 0;

            for (Candy other : cluster) {

                if (candy.getRow()
                        == other.getRow()) {
                    sameRow++;
                }

                if (candy.getCol()
                        == other.getCol()) {
                    sameCol++;
                }
            }

            // giao điểm
            if (sameRow >= 3 &&
                    sameCol >= 3) {

                return candy;
            }
        }

        return cluster.get(cluster.size() / 2);
    }
}